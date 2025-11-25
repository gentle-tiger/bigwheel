package com.game.bigwheel.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.bigwheel.domain.bigwheel.Bigwheel;
import com.game.bigwheel.domain.bigwheel.BigwheelRepository;
import com.game.bigwheel.domain.bigwheel.GameMode;
import com.game.bigwheel.domain.coin.ChipType;
import com.game.bigwheel.domain.user.User;
import com.game.bigwheel.domain.user.UserRepository;
import com.game.bigwheel.presentation.dto.GameRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor  // final 필드 생성자 자동 생성
@Slf4j
public class BigwheelService {

  private final BigwheelRepository bigwheelRepository;
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper; // json 변환용
  /*
  * 게임 결과 저장
  * */
  @Transactional
  public Bigwheel saveGame(GameRequest request){
    log.info("Saving game: userId={}, totalBet={}", request.getUserId(), request.getTotalBetAmount());

    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다." + request.getUserId()));

    Bigwheel game = Bigwheel.builder()
        .user(user)
        .betDetails(betDetails)
        .resultSector(resultZone.name())
        .totalBetAmount(calculateTotalBet(betDetails)) // 계산
        .winningAmount(calculateWinning(winningChips)) // 계산
        .netProfit(calculateNetProfit(winningChips, betDetails)) // 계산
        .playedAt(LocalDateTime.now())
        .gameMode(request.getGameMode())
        .build();

    Bigwheel savedGame = bigwheelRepository.save(game);
    log.info("Saved game: gameId={}, netProfit={}", savedGame.getGameId(), savedGame.getNetProfit());
    return savedGame;
  }

  /*
  * 특정 사용자의 게임 기록 조회
  * */
  @Transactional(readOnly = true)
  public List<Bigwheel> getUserGames(Long userId){
    log.info("user Games log: userId={}", userId);
    return bigwheelRepository.findByUserId(userId);
  }

  /*
  * 전체 게임 기록 조회
  * */
  @Transactional(readOnly = true)
  public List<Bigwheel> getAllGames(){
    log.info("전체 게임 기록 조회");
    return bigwheelRepository.findAllByOrderByPlayedAtDesc();
  }

  /*
  * 특정 사용자의 N개 게임 조회
  * */
  @Transactional(readOnly = true)
  public List<Bigwheel> getUserGamesTop(Long userId, Timestamp playedAt){
    log.info("특정 사용자의 N개 게임 조회: userId={}, playedAt={}", userId, playedAt);
    return bigwheelRepository.findByUserIdAndPlayedAt(userId, playedAt);
  }

  /*
  *  특정 게임 조회
  * */
  @Transactional(readOnly = true)
  public Bigwheel getGame(Long gameId){
    log.info("특정 게임 조회: gameId={}", gameId);
    return bigwheelRepository.findById(gameId)
        .orElseThrow(() -> new RuntimeException("게임을 찾을 수 없습니다:" + gameId));
  }


}

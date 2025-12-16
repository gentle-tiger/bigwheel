package com.game.bigwheel.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.bigwheel.domain.bigwheel.BetZone;
import com.game.bigwheel.domain.bigwheel.Bigwheel;
import com.game.bigwheel.domain.bigwheel.BigwheelRepository;
import com.game.bigwheel.domain.bigwheel.GameMode;
import com.game.bigwheel.domain.coin.ChipType;
import com.game.bigwheel.domain.user.User;
import com.game.bigwheel.domain.user.UserRepository;
import com.game.bigwheel.presentation.dto.UserChipDTO;
import com.game.bigwheel.presentation.dto.GameSpinRequest;
import com.game.bigwheel.presentation.dto.GameSpinResponse;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
  public GameSpinResponse saveGame(GameSpinRequest request) {
    log.info("Saving game: userId={}, totalBet={}", request.getUserId(),
        request.getTotalBetAmount());

    // 1. User 조회
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다." + request.getUserId()));

    // 2. 베팅한 칩 차감
    Map<String, Map<String, Integer>> betDetails = request.getBetDetails();
    Map<String, Integer> lostChips = new HashMap<>();

    for (Map.Entry<String, Map<String, Integer>> entry : betDetails.entrySet()) {
      for (Map.Entry<String, Integer> chipEntry : entry.getValue().entrySet()) {
        ChipType chipType = ChipType.valueOf(chipEntry.getKey());
        Integer count = chipEntry.getValue();

        // 칩 차감 (도메인 로직)
        user.deductChip(chipType, count);

        // 차감된 칩 기록
        lostChips.put(chipEntry.getKey(),
            lostChips.getOrDefault(chipEntry.getKey(), 0) + count);
      }
    }

    // 3. 결과 생성 - Request에 있으면 사용, 없으면 랜덤
    BetZone resultZone;
    if (request.getResultSector() != null && !request.getResultSector().isBlank()) {
      // 테스트/프론트엔드가 결과를 지정한 경우
      resultZone = BetZone.valueOf(request.getResultSector());
      log.info("게임 결과 (지정됨): {}", resultZone.name());
    } else {
      // 실제 게임 - 랜덤 생성
      resultZone = generateRandomResult();
      log.info("게임 결과 (랜덤): {}", resultZone.name());
    }

    // 4. 당첨 칩 계산 및 지급
    Map<String, Integer> winningChips = new HashMap<>();

    if(betDetails.containsKey(resultZone.name())) {
      Map<String, Integer> winningBet = betDetails.get(resultZone.name());

      for(Map.Entry<String, Integer> entry : winningBet.entrySet()){
        ChipType chipType = ChipType.valueOf(entry.getKey());
        Integer betCount = entry.getValue();
        // 칩 환산
        Integer wonCount = betCount * resultZone.getPayout();

        // 칩 지급 (도메인 로직)
        user.addChip(chipType, wonCount);

        // 당첨 칩 개수
        winningChips.put(entry.getKey(), wonCount);

        // lostChips에서 당첨 구역 베팅 제거
        lostChips.put(
            entry.getKey(), lostChips.get(entry.getKey()) - betCount
        );
      }
    }

    // 5. 게임 저장
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
    log.info("✅ 게임 저장 완료: gameId={}, netProfit={}", savedGame.getGameId(), savedGame.getNetProfit());


    // 6. 응답 생성 (게임 실행 시 모든 정보 포함)
    return GameSpinResponse.builder()
        .gameId(savedGame.getGameId())
        .userId(savedGame.getUser().getId())
        .username(savedGame.getUser().getUsername())
        .resultSector(resultZone.name())
        .betDetails(betDetails)
        .totalBetAmount(savedGame.getTotalBetAmount())
        .winningAmount(savedGame.getWinningAmount())
        .netProfit(savedGame.getNetProfit())
        .winningChips(winningChips)
        .lostChips(lostChips)
        .updateChips(user.getChips().stream()
            .map(UserChipDTO::from)
            .collect(Collectors.toList()))
        .totalBalance(user.getTotalBalance())
        .gameMode(savedGame.getGameMode())
        .playedAt(savedGame.getPlayedAt().toString())
        .build();
  }

  // 랜덤 결과 생성(BetZone 확률 기반)
  private BetZone generateRandomResult() {
    int totalSlot = Arrays.stream(BetZone.values())
        .mapToInt(BetZone::getCount)
        .sum();
    int ramdom = new Random().nextInt(totalSlot) + 1;
    int current = 0;
    for(BetZone zone : BetZone.values()) {
      current += zone.getCount();
      if(current >= ramdom) {
        return zone;
      }
    }
    return BetZone.SILVER; // Fallback
  }

  private long calculateTotalBet(Map<String, Map<String, Integer>> betDetails) {
    return betDetails.values().stream()
        .flatMap(chip -> chip.entrySet().stream())
        .mapToLong(entry -> {
          ChipType chipType = ChipType.valueOf(entry.getKey());
          return (long) chipType.getValue() * entry.getValue();
        })
        .sum();
  }

  private long calculateWinning(Map<String, Integer> winningChips) {
    return winningChips.entrySet().stream()
        .mapToLong(entry -> {
          ChipType chipType = ChipType.valueOf(entry.getKey());
          return (long) chipType.getValue() * entry.getValue();
        })
        .sum();
  }

  private Long calculateNetProfit(Map<String, Integer> winningChips,
      Map<String, Map<String, Integer>> betDetails) {
    Long winning = calculateWinning(winningChips);
    Long totalBet = calculateTotalBet(betDetails);
    return winning - totalBet;
  }

  /*
   * 전체 게임 기록 조회
   * */
  @Transactional(readOnly = true)
  public List<Bigwheel> getAllGames() {
    log.info("전체 게임 기록 조회");
    return bigwheelRepository.findAllByOrderByPlayedAtDesc();
  }

  /*
   * 특정 사용자의 N개 게임 조회
   * */
  @Transactional(readOnly = true)
  public List<Bigwheel> getGamesTop(Long userId, Timestamp playedAt) {
    log.info("특정 사용자의 N개 게임 조회: userId={}, playedAt={}", userId, playedAt);
    return bigwheelRepository.findByUserIdAndPlayedAt(userId, playedAt);
  }

  /*
   *  특정 게임 조회
   * */
  @Transactional(readOnly = true)
  public Bigwheel getGame(Long gameId) {
    log.info("특정 게임 조회: gameId={}", gameId);
    return bigwheelRepository.findById(gameId)
        .orElseThrow(() -> new RuntimeException("게임을 찾을 수 없습니다:"));
  }

  /*
   * 특정 사용자의 게임 기록 조회
   */
  @Transactional(readOnly = true)
  public Page<Bigwheel> getGames(Long userId, GameMode gameMode, Pageable pageable){
    log.info("사용자 게임 조회: userId={}, gameMode={}", userId, gameMode);

    // null 이면 전체 조회.
    if(gameMode == null){
      log.info("게임 기록 gameMode={} 조회 ", gameMode == null ? "전체" : gameMode);
      return bigwheelRepository.findByUserId(userId, pageable);
    }else{
      log.info("게임 기록 gameMode={} 조회 ", gameMode);
      return bigwheelRepository.findByUserIdAndGameModeOrderByPlayedAtDesc(userId, gameMode, pageable);
    }
  }


}

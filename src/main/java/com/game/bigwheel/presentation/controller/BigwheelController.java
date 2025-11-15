package com.game.bigwheel.presentation.controller;

import com.game.bigwheel.application.BigwheelService;
import com.game.bigwheel.domain.bigwheel.Bigwheel;
import com.game.bigwheel.presentation.dto.GameRequest;
import com.game.bigwheel.presentation.dto.GameResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/games")
@RequiredArgsConstructor
@Slf4j
public class BigwheelController {

  private final BigwheelService bigwheelService;

  /*
  * method: 게임 결과 저장
  * api: POST /api/games
  *  - @Vaild로 요청 검증
  *  - DTO를 받아 Service에 전달
  *  - Entity를 DTO로 변환하여 응답
  * */
  @PostMapping
  public ResponseEntity<GameResponse> saveGame(@Valid @RequestBody GameRequest request){
    log.info("게임 저장 요청 수신: userId={}, gameId={}", request.getUserId(), request.getTotalBetAmount() );
    // Service 호출 (DTO -> entiry 변환은 Service Layer에서 진행)
    Bigwheel savedGame = bigwheelService.saveGame(request);
    // Entity -> DTO 변환(정적 팩토리 메서드 사용)으로 Controller는 Entity의 구조를 몰라도 됨.(느슨한 결합)
    GameResponse response = GameResponse.from(savedGame);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }


  /*
  * method: 전체 게임 조회
  * api: GET /api/games
   */
  @GetMapping
  public ResponseEntity<List<Bigwheel>> getAllGames(){
    log.info("전체 게임 조회 요청");
    List<Bigwheel> games = bigwheelService.getAllGames();
    return ResponseEntity.ok(games);
  }

  /*
  * method: 특정 사용자의 게임 조회
  * GET /api/games/{id}
  * */
  @GetMapping(value ="/{id}")
  public ResponseEntity<List<Bigwheel>> getGameById(@PathVariable Long userId){
    log.info("사용자 게임 조회 요청: userId={}", userId);
    List<Bigwheel> games = bigwheelService.getUserGames(userId);
    return ResponseEntity.ok(games);
  }

}

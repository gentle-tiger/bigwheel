package com.game.bigwheel.presentation.controller;

import com.game.bigwheel.application.BigwheelService;
import com.game.bigwheel.domain.bigwheel.BetZone;
import com.game.bigwheel.domain.bigwheel.Bigwheel;
import com.game.bigwheel.presentation.dto.BetZoneResponse;
import com.game.bigwheel.presentation.dto.GameSpinRequest;
import com.game.bigwheel.presentation.dto.GameSpinResponse;
import jakarta.validation.Valid;
import java.util.Collections;
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
  public ResponseEntity<GameSpinResponse> saveGame(@Valid @RequestBody GameSpinRequest request){
    log.info("게임 저장 요청 수신: userId={}, gameId={}", request.getUserId(), request.getTotalBetAmount() );
     // entiry -> DTO 변환은 Controller 단이 맞지만, 계산 결과가 service 단에 있어서 바로 반환해야함...
    GameSpinResponse response = bigwheelService.saveGame(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }


  /*
  * method: 전체 게임 조회
  * api: GET /api/games
   */
  @GetMapping
  public ResponseEntity<List<GameSpinResponse>> getAllGames(){
    log.info("전체 게임 조회 요청");
    List<Bigwheel> games = bigwheelService.getAllGames();
    List<GameSpinResponse> gameResponseList = games.stream().map(GameSpinResponse:: from).toList();
    return ResponseEntity.ok(gameResponseList);
  }

  /*
  * method: 사용자별 전체 게임 조회
  * GET /api/games/{userId}
  * */
  @GetMapping(value ="/{userId}")
  public ResponseEntity<List<GameSpinResponse>> getGameById(@PathVariable Long userId){
    log.info("사용자 게임 조회 요청: userId={}", userId);
    List<Bigwheel> games = bigwheelService.getUserGames(userId);
    List<GameSpinResponse> gameResponseList = games.stream().map(GameSpinResponse:: from).toList();
    return ResponseEntity.ok(gameResponseList);
  }

  @GetMapping("/zones")
  public ResponseEntity<BetZoneResponse> getZones() {
    log.info("베팅 구역 정보 조회 요청");
    BetZoneResponse response = BetZoneResponse.from();
    return ResponseEntity.ok(response);
  }
}

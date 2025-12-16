package com.game.bigwheel.presentation.controller;

import com.game.bigwheel.application.BigwheelService;
import com.game.bigwheel.domain.bigwheel.Bigwheel;
import com.game.bigwheel.domain.bigwheel.GameMode;
import com.game.bigwheel.presentation.dto.BetZoneResponse;
import com.game.bigwheel.presentation.dto.GameSpinRequest;
import com.game.bigwheel.presentation.dto.GameSpinResponse;
import com.game.bigwheel.presentation.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
  * method: 베팅 구역 조회
  * api: GET /api/games/zones
   */
  @GetMapping("/zones")
  public ResponseEntity<BetZoneResponse> getZones() {
    log.info("베팅 구역 정보 조회 요청");
    BetZoneResponse response = BetZoneResponse.from();
    return ResponseEntity.ok(response);
  }

  /*
   * method: 사용자별 게임 기록 조회  (+ 페이징)
   * api: GET /api/games/{userId}/{gameMode}
   * gameMode가 없을 때는 전체 조회겠지?
   */
  @GetMapping()
  public ResponseEntity<PageResponse<GameSpinResponse>> getGames(
      @RequestHeader("X-User-Id") Long userId,
      @RequestParam(required = false) GameMode gameMode,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ){
    log.info("사용자별 게임 기록 조회 요청(+ 페이징)");
    Pageable pageable = PageRequest.of(page, size);
    Page<Bigwheel> entityPage =  bigwheelService.getGames(userId, gameMode, pageable);
    Page<GameSpinResponse> dtoPage = entityPage.map(GameSpinResponse::from);
    PageResponse<GameSpinResponse> response = PageResponse.from(dtoPage);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{gameId}")
  public ResponseEntity<GameSpinResponse> getGame(
      @PathVariable Long gameId
  ){
    log.info("특정 게임 기록 조회 gameId={}", gameId);
    Bigwheel game = bigwheelService.getGame(gameId);
    GameSpinResponse response = GameSpinResponse.from(game);
    return ResponseEntity.ok(response);
  }
}

package com.game.bigwheel.presentation.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.bigwheel.domain.bigwheel.Bigwheel;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
* 게임 조회 응답 DTO
* - Service -> Controller -> Client로 데이터 전달
* - Entity를 직접 노출하지 않음(보완)
* */
@Builder
@Getter // Jackson이 Json으로 변환할 때 필요.
@NoArgsConstructor
@AllArgsConstructor
public class GameResponse {

  private Long gameId;
  private Long userId;
  private String username;
  private Map<String, Long> betDetails;
  private String resultSector;
  private Long totalBetAmount;
  private Long winningAmount;
  private Long netProfit;
  private String playedAt;

  /*
  * Entity -> DTO 변환 메서드 (정적 팩토리 메서드 패턴)
  * - Entity 받아서 DTO로 변환
  * - 변환 로직을 DTO 내부에 캡슐화
  * */
  public static GameResponse from(Bigwheel game){
    return GameResponse.builder()
        .gameId(game.getGameId())
        .userId(game.getUser().getId())
        .username(game.getUser().getUsername())
        .betDetails(game.getBetDetails())
        .resultSector(game.getResultSector())
        .totalBetAmount(game.getTotalBetAmount())
        .winningAmount(game.getWinningAmount())
        .netProfit(game.getNetProfit())
        .playedAt(game.getPlayedAt().toString())
        .build();
  }
}

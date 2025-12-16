package com.game.bigwheel.presentation.dto;

import com.game.bigwheel.domain.bigwheel.GameMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/*
* 게임 저장 요청 DTO
* - Controller -> Service 계층으로 데이터 전달
* - Entity가 아닌 DTO로 받아 계층 분리
* - 게임 내역은 수정할 일이 없어 Setter 제외
* */
@Getter
@Setter
@Builder
public class GameSpinRequest {

  @NotNull(message = "사용자 ID는 필수입니다") private Long userId;
  @NotNull(message = "베팅 상세는 필수입니다") private Map<String, Map<String, Integer>> betDetails;

  // 선택적 필드 (Service에서 처리)
  private String resultSector;
  private Long totalBetAmount;
  private Long winningAmount;
  private Long netProfit;
  private GameMode gameMode;        // 기본값: ONLINE
  private String playedAt;          // ISO 8601

  public GameMode getGameMode(GameMode gameMode) {
    return gameMode != null ? gameMode : GameMode.ONLINE;
  }
}

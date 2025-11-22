package com.game.bigwheel.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
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
public class GameSpinRequest {

  @NotNull(message = "사용자 ID는 필수입니다") private Long userId;
  @NotNull(message = "베팅 상세는 필수입니다") private Map<String, Map<String, Integer>> betDetails;
  @NotBlank(message = "결과 섹터는 필수입니다") private String resultSector;
  @NotNull(message = "총 베팅액은 필수입니다") private Long totalBetAmount;
  @NotNull(message = "당첨금은 필수입니다") private Long winningAmount;
  @NotNull(message = "순이익은 필수입니다") private Long netProfit;
  private String playedAt; // ISO 8601
}

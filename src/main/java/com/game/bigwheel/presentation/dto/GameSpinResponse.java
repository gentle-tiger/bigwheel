package com.game.bigwheel.presentation.dto;

import com.game.bigwheel.domain.bigwheel.Bigwheel;
import com.game.bigwheel.domain.bigwheel.GameMode;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
/*
* 게임 조회 응답 DTO
* - Service -> Controller -> Client로 데이터 전달
* - Entity를 직접 노출하지 않음(보완)
* */
@Builder
@Getter // Jackson이 Json으로 변환할 때 필요.
@NoArgsConstructor
@AllArgsConstructor
public class GameSpinResponse {

  private Long gameId;
  private Long userId;
  private String username;
  private String resultSector;                        // 결과 구역
  private Map<String, Map<String, Integer>> betDetails;  // 베팅 구역 내역
  private Long totalBetAmount;                        // 총 베팅 금액
  private Long winningAmount;                         // 당첨 금액
  private Long netProfit;                             // 순손익
  private Map<String, Integer> winningChips;          // 승리 칩 (게임 실행 시에만 사용)
  private Map<String, Integer> lostChips;             // 잃어버린 칩 (게임 실행 시에만 사용)
  private List<UserChipDTO> updateChips;                  // 최종 보유 칩 (게임 실행 시에만 사용)
  private Long totalBalance;                          // 최종 잔액 (게임 실행 시에만 사용)
  private GameMode gameMode;
  private String playedAt;

  /*
  * Entity -> DTO 변환 메서드 (정적 팩토리 메서드 패턴)
  * - Entity 받아서 DTO로 변환
  * - 변환 로직을 DTO 내부에 캡슐화
  * - 게임 조회 시 사용 (winningChips, lostChips, updateChips는 null)
  * */
  public static GameSpinResponse from(Bigwheel game){
    return GameSpinResponse.builder()
        .gameId(game.getGameId())
        .userId(game.getUser().getId())
        .username(game.getUser().getUsername())
        .resultSector(game.getResultSector())
        .betDetails(game.getBetDetails())
        .totalBetAmount(game.getTotalBetAmount())
        .winningAmount(game.getWinningAmount())
        .netProfit(game.getNetProfit())
        .winningChips(null)  // 게임 조회에서는 불필요
        .lostChips(null)     // 게임 조회에서는 불필요
        .updateChips(null)   // 게임 조회에서는 불필요
        .totalBalance(null)  // 게임 조회에서는 불필요
        .gameMode(game.getGameMode())
        .playedAt(game.getPlayedAt().toString())
        .build();
  }
}

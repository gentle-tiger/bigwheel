package com.game.bigwheel.domain.bigwheel;

import com.game.bigwheel.domain.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Entity
@Table(name ="p_bigwheel_games")
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // Builder
@Builder
public class Bigwheel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long gameId;

  @ManyToOne(fetch = FetchType.LAZY) // 지연로딩 (성능)
  @JoinColumn(name ="user_id" ,nullable = false)
  private User user;


  @Column(nullable = false, columnDefinition = "jsonb") // JSON 문자열로 저장
  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, Map<String, Integer>> betDetails; // 모든 베팅 구역 상세, Map<BetZone명, Map<ChipType명, 개수>>

  @Column(nullable = false)
  private String resultSector; // 당첨구역

  @Column(nullable = false)
  private Long totalBetAmount; // 총 베팅액

  @Column(nullable = false)
  private Long  winningAmount; // 당첨금

  @Column(nullable = false)
  private Long netProfit; // 순이익

  private LocalDateTime playedAt; // 게임 날짜

}



package com.game.bigwheel;

import com.game.bigwheel.user.User;
import jakarta.persistence.*;
import java.sql.Timestamp;
import lombok.Getter;

@Getter
@Entity
@Table(name ="p_bigwheel_games")
public class Bigwheel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long gameId;

  @ManyToOne(fetch = FetchType.LAZY) // 지연로딩 (성능)
  @JoinColumn(nullable = false)
  private User userId;


  @Column(nullable = false, columnDefinition = "jsonb") // JSON 문자열로 저장
  private BetZone betDetails; // 모든 베팅 구역 상세

  @Column(nullable = false)
  private String resultSector; // 당첨구역

  @Column(nullable = false)
  private Long totalBetAmount; // 총 베팅액

  @Column(nullable = false)
  private Long  winningAmount; // 당첨금

  @Column(nullable = false)
  private Long newProfit; // 순이익

  private Timestamp playedAt; // 게임 날짜

}



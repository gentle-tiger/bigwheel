package com.game.bigwheel.domain.user;


import com.game.bigwheel.domain.coin.ChipType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_user_chips")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserChip {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ChipType chipType;

  @Builder.Default
  @Column(nullable = false)
  private Integer count = 0;

  // 칩 차감
  public void deduct(int amount) {
    if (this.count < amount) {
      throw new IllegalStateException(
          String.format("칩이 부족합니다. (보유: %d개, 필요: %d)",
              this.count, amount)
      );
    }
    this.count -= amount;
  }

  // 칩 추가
  public void add(int amount) {
    if (amount < 0) {
      throw new IllegalStateException("추가할 칩 개수는 양수여야 합니다.");
    }
    this.count += amount;
  }

  // 총 가치 계산
  public long getTotalValue() {
    return (long) chipType.getValue() * count;
  }

}





















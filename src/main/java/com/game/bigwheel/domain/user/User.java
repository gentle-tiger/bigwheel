package com.game.bigwheel.domain.user;

import com.game.bigwheel.domain.coin.ChipType;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Getter
@Entity
@Table(name = "p_users")
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // Builder
@Builder
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String nickname; // lowercase

  @Column(nullable = false)
  private String email;

  /*
  * UserChip과의 일대다 관계
  * CascadeType.ALL:User 저장시 UserChip도 자동 저장
  * orphanRemoval = true: User에서 제거된 UserChip은 DB에서도 삭제
  * */
  @Builder.Default
  @Column(nullable = false)
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UserChip> chips = new ArrayList<>();

  // 총 보유 금액 계산
  public long getTotalBalance(){
    return chips.stream()
        .mapToLong(UserChip::getTotalValue)
        .sum();
  }

  // 특정 칩 개수 계산
  public int getChipCount(ChipType chipType){
    return chips.stream()
        .filter(chip -> chip.getChipType() == chipType)
        .findFirst()
        .map(UserChip::getCount)
        .orElse(0);
  }

  // 칩 차감
  public void deductChip(ChipType chipType, int count){
    UserChip userChip = findOrCreateChip(chipType);
    userChip.deduct(count);
  }

  // 칩 추가
  public void addChip(ChipType chipType, int count){
    UserChip userChip = findOrCreateChip(chipType);
    userChip.add(count);
  }

  // private 헬퍼 메서드: 칩 찾기 또는 생성
  private UserChip findOrCreateChip(ChipType chipType){
    return chips.stream()
        .filter(chip -> chip.getChipType() == chipType)
        .findFirst()
        .orElseGet(() -> {
          UserChip newChip = UserChip.builder()
              .user(this)
              .chipType(chipType)
              .count(0)
              .build();
          chips.add(newChip);
          return newChip;
        });
  }
}






















package com.game.bigwheel.presentation.dto;

import com.game.bigwheel.domain.user.UserChip;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserChipDTO {

  private String chipType;
  private Integer value;
  private Integer count;
  private Long totalValue;

  public static UserChipDTO from(UserChip userChip) {
    return UserChipDTO.builder()
        .chipType(userChip.getChipType().name())
        .value(userChip.getChipType().getValue())
        .count(userChip.getCount())
        .totalValue(userChip.getTotalValue())
        .build();
  }
}

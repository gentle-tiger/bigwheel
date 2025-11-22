package com.game.bigwheel.presentation.dto;

import com.game.bigwheel.domain.coin.ChipType;
import java.util.Map;
import lombok.Getter;

@Getter // 나중에 가져올 거 생각해서 Getter
public class SaveChipsRequest {
  private Map<ChipType, Integer> chips;

}

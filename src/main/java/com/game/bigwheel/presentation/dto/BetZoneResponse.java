package com.game.bigwheel.presentation.dto;

import com.game.bigwheel.domain.bigwheel.BetZone;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BetZoneResponse {
    private List<ZoneDTO> zones;
    private Integer totalSlots;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ZoneDTO {
      private String name;
      private Integer count;
      private Integer payout;
      private Double probability;
    }

    public static BetZoneResponse from(){
      int totalSlots = Arrays.stream(BetZone.values())
          .mapToInt(BetZone:: getCount) //이게 뭐지???
          .sum();
      List<ZoneDTO> zones = Arrays.stream(BetZone.values())
          .map(zone -> ZoneDTO.builder()
              .name(zone.name())
              .count(zone.getCount())
              .payout(zone.getPayout())
              .probability((double) (zone.getCount() / totalSlots * 100))
              .build())
          .collect(Collectors.toList());

      return BetZoneResponse.builder()
          .zones(zones)
          .totalSlots(totalSlots)
          .build();
    }

}























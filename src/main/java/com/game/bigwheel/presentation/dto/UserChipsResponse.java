package com.game.bigwheel.presentation.dto;


import com.game.bigwheel.domain.user.User;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserChipsResponse {

  private Long userId;
  private String username;
  private List<UserChipDTO> chips;
  private Long totalBalance;

  public static UserChipsResponse from(User user){
    return UserChipsResponse.builder()
        .userId(user.getId())
        .username(user.getUsername())
        .chips(user.getChips().stream()
            .map(UserChipDTO::from)
            .collect(Collectors.toList()))
        .totalBalance(user.getTotalBalance())
        .build();
  }
}

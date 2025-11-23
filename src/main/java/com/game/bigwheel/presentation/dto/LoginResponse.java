package com.game.bigwheel.presentation.dto;

import com.game.bigwheel.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
  private String userId;
  private String username;
  private String nickname;


  public static LoginResponse from(User user){
    return LoginResponse.builder()
        .userId(String.valueOf(user.getId())) // 왜 string valueOf 로 해야할까?
        .username(user.getUsername())
        .nickname(user.getNickname())
        .build();
  }
}

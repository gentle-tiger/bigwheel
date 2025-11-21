package com.game.bigwheel.domain.user;

import lombok.Builder;

@Builder
public class UserResponse {
  private String userId;
  private String username;
  private String nickname;


  public static UserResponse from(User user){
    return UserResponse.builder()
        .userId(String.valueOf(user.getId())) // 왜 string valueOf 로 해야할까?
        .username(user.getUsername())
        .nickname(user.getNickname())
        .build();
  }
}

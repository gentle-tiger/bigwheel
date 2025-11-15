package com.game.bigwheel.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  UserRepository userRepository;

  public void register(User user) {
    userRepository.save(user);
  }

  public void login(String username, String password) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("로그인 실패"));

    if (!password.equals(user.getPassword())) {
      throw new RuntimeException("로그인 실패");
    }
  }
}

package com.game.bigwheel.presentation.controller;

import com.game.bigwheel.presentation.dto.LoginResponse;
import com.game.bigwheel.domain.user.User;
import com.game.bigwheel.application.UserService;
import com.game.bigwheel.presentation.dto.SaveChipsRequest;
import com.game.bigwheel.presentation.dto.UserChipsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {

  @Autowired
  public UserService userService;

  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody User user) {
    User savedUser = userService.register(user);
    return ResponseEntity.ok("회원가입 성공");
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody User user) {
    log.info("로그인 요청 userId = {}", user.getId());
    User res = userService.login(user.getUsername(), user.getPassword());
    LoginResponse response = LoginResponse.from(res);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{userId}/chips")
  public ResponseEntity<UserChipsResponse> chips(@PathVariable Long userId) {
    log.info("사용자 보유 칩 조회 요청 userId = {}", userId);
    UserChipsResponse response = userService.getUserChips(userId);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{userId}/chips")
  public ResponseEntity<UserChipsResponse> saveChips(
      @PathVariable Long userId,
      @RequestBody SaveChipsRequest request
  ) {
    try {
      log.info("사용자 보유 칩 저장 요청 userId = {}", userId);
      UserChipsResponse response = userService.saveChips(userId, request.getChips());
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      // 잘못된 입력 (count <= 0);
      return ResponseEntity.badRequest().build();
    } catch (RuntimeException e) {
      // 사용자 없음 또는 기타 에러
      return ResponseEntity.status(500).build();
    }
  }

}

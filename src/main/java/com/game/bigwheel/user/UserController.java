package com.game.bigwheel.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {

  @Autowired
  public UserService userService;

  @PostMapping("/register")
  public String register(@RequestBody User user) {
    userService.register(user);
    return "OK";
  }

  @PostMapping("/login")
  public String login(@RequestBody User user) {
    userService.login(user.getUsername(), user.getPassword());
    return "OK";
  }
}

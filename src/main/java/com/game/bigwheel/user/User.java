package com.game.bigwheel.user;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "p_users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String nickName;

  @Column(nullable = false)
  private String email;

}
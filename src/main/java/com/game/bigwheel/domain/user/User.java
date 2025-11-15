package com.game.bigwheel.domain.user;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "p_users")
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // Builder
@Builder
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String nickname; // lowercase

  @Column(nullable = false)
  private String email;

}
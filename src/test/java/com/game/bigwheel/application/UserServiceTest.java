package com.game.bigwheel.application;

import com.game.bigwheel.domain.coin.ChipType;
import com.game.bigwheel.domain.user.User;
import com.game.bigwheel.domain.user.UserRepository;
import com.game.bigwheel.presentation.dto.UserChipsResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks // 이거 뭐하느거지?
  private UserService userService;

  private User testUser;

  @BeforeEach
  public void setUp() {
    final long id = 1L;
    final String username = "testUser";
    final String passsword = "password123";
    final String nickname = "개발자";
    final String email = "test123@naver.com";
    testUser = User.builder()
        .id(id)
        .username(username)
        .password(passsword)
        .nickname(nickname)
        .email(email)
        .build();
  }

  @Test
  @DisplayName("회원가입 성공")
  void register_Success() {
    // given
    when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty()); // 계정 중복 없음
    when(userRepository.existsByNickname(any(String.class))).thenReturn(false); // 닉네임 중복 없음
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    // when
    User savedUser = userService.register(testUser);

    // then
    assertThat(savedUser).isNotNull();
    assertThat(savedUser.getUsername()).isEqualTo("testUser");
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("회원가입 실패 - 중복 요청")
    // 중복 요청의 경우 service(비즈니스 로직)에서 다루고 있어서 when에서 확인하는 건가? 근데r register_success에서 given에서도 검증하지 않나??
  void register_Fail() {
    //given
    when(userRepository.findByUsername("testUser")).thenReturn(
        Optional.of(testUser)); // 이미 존재하는 유저

    // when & then
    assertThatThrownBy(() -> userService.register(testUser))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("이미 가입된 유저입니다.");

    // save가 호출되지 않았는지 검증
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("로그인 성공")
  void login_Success() {
    // given
    when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

    // when
    User foundUser = userService.login("testUser", "password123");

    // then
    assertThat(foundUser).isNotNull();
    assertThat(foundUser.getUsername()).isEqualTo("testUser");
  }

  @Test
  @DisplayName("로그인 실패 - 유저 없음")
  void login_Fail_UserNotFound() {
    // give
    String wrongUsername = "없는 유저 조회";
    String wrongPassword = "NotPassword123";
    when(userRepository.findByUsername(wrongUsername)).thenReturn(Optional.empty()); // 유저 없음

    // when & then
    assertThatThrownBy(() -> userService.login(wrongUsername, wrongPassword)) // 없는 유저 정보로 로그인을 했을 떄
        .isInstanceOf(RuntimeException.class) // 런타임 에러가 발생해야 하고,
        .hasMessage("가입한 유저가 아닙니다."); // service 단과 동일한 메시지가 반환 되어야 함.
  }

  @Test
  @DisplayName("로그인 실패 - 잘못된 비밀번호")
  void login_Fail_WrongPassword() {
    // given
    when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

    // when & then
    assertThatThrownBy(() -> userService.login("testUser", "wrongPassword123"))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("패스워드가 일치하지 않습니다.");
  }

  @Test
  @DisplayName("칩 추가 성공")
  void addChip_Success() {
    // given
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

    Map<ChipType, Integer> chips = Map.of(
        ChipType.CHIP_10000, 10,
        ChipType.CHIP_5000, 20
    );

    // when
    UserChipsResponse response = userService.saveChips(1L, chips);

    // then
    assertThat(response).isNotNull();
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("칩 저장 실패 - 사용자 없음")
  void addChip_Fail_UserNotFound() {
    // given
    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    Map<ChipType, Integer> chips = Map.of(
        ChipType.CHIP_10000, 10,
        ChipType.CHIP_5000, 20
    );

    // when & then
    assertThatThrownBy(() -> userService.saveChips(999L, chips))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("사용자를 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("칩 저장 실패 - 음수 개수")
  void addChip_Fail_NegativeCount() {
    // given
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

    Map<ChipType, Integer> chips = Map.of(ChipType.CHIP_10000, -10);

    // when & then
    assertThatThrownBy(() -> userService.saveChips(1L, chips))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("칩 개수는 0보다 커야 합니다");
  }



}



























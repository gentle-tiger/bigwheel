package com.game.bigwheel.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.game.bigwheel.domain.bigwheel.BetZone;
import com.game.bigwheel.domain.bigwheel.Bigwheel;
import com.game.bigwheel.domain.bigwheel.BigwheelRepository;
import com.game.bigwheel.domain.bigwheel.GameMode;
import com.game.bigwheel.domain.coin.ChipType;
import com.game.bigwheel.domain.user.User;
import com.game.bigwheel.domain.user.UserRepository;
import com.game.bigwheel.presentation.dto.GameSpinRequest;
import com.game.bigwheel.presentation.dto.GameSpinResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@ExtendWith(MockitoExtension.class) // mokito만 사용
@DisplayName("BigwheelService 테스트 실행")
public class BigwheelServiceTest {

  @Mock
  private BigwheelRepository bigwheelRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private BigwheelService bigwheelService;

  private User testUser;
  private GameSpinRequest testRequest;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
        .id(1L)
        .username("testUser")
        .password("testPassword")
        .email("testUser@gmail.com")
        .nickname("testUser")
        .build();
    // 칩 추가
    testUser.addChip(ChipType.CHIP_1000, 100);

    // 베팅 정보 생성
    Map<String, Map<String, Integer>> betDetails = new HashMap<>();
    Map<String, Integer> goldBet = new HashMap<>();
    goldBet.put("CHIP_1000", 10);
    betDetails.put("GOLD", goldBet);

    testRequest = GameSpinRequest.builder()
        .userId(1L)
        .betDetails(betDetails)
        .resultSector("GOLD")    // 당첨 구역
        .totalBetAmount(10000L)  // 10개
        .winningAmount(20000L)   // 당첨금
        .netProfit(10000L)       // 순이익
        .gameMode(GameMode.ONLINE)
        .playedAt("2025-11-26T10:00:00")
        .build();
  }

  @Test
  @DisplayName("게임 저장 성공 - 온라인")
  void saveGame_online_success() {
    // given
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(bigwheelRepository.save(any(Bigwheel.class)))
        .thenAnswer(invocation -> invocation.getArgument(0)); // 이게 무슨 코드지? Answer?? invocation??

    // when
    GameSpinResponse response = bigwheelService.saveGame(testRequest);

    // then
    assertThat(response).isNotNull();
    assertThat(response.getGameMode()).isEqualTo(GameMode.ONLINE);
    assertThat(response.getResultSector()).isEqualTo("GOLD");
    assertThat(response.getTotalBetAmount()).isEqualTo(10000L);
    verify(bigwheelRepository, times(1)).save(any(Bigwheel.class)); // any 가 뭐엿더라...
  }

  @Test
  @DisplayName("게임 저장 성공 - 오프라인")
  void saveGame_offline_success(){}

  @Test
  @DisplayName("게임 조회 - 전체 (gameMode null)")
  void getGames_AllGames() {
    // given
    Pageable pageable = PageRequest.of(0, 10);
    List<Bigwheel> games = List.of(
        createTestGame(GameMode.ONLINE),
        createTestGame(GameMode.OFFLINE)
    );
    Page<Bigwheel> page = new PageImpl<>(games, pageable, games.size());

    when(bigwheelRepository.findByUserId(1L, pageable)).thenReturn(page);

    // when
    Page<Bigwheel> result = bigwheelService.getGames(1L, null, pageable);

    // then
    assertThat(result.getContent()).hasSize(2); // ONLINE, OFFLINE 둘 다 반환
  }


  @Test
  @DisplayName("게임 조회 - ONLINE만 필터링")
  void getGames_Online() {
    // given
    Pageable pageable = PageRequest.of(0, 10);
    List<Bigwheel> games = List.of(
        createTestGame(GameMode.ONLINE)
    );
    Page<Bigwheel> page = new PageImpl<>(games, pageable, games.size());

    when(bigwheelRepository.findByUserIdAndGameModeOrderByPlayedAtDesc(1L, GameMode.ONLINE, pageable)).thenReturn(page);

    // when
    Page<Bigwheel> result = bigwheelService.getGames(1L, GameMode.ONLINE, pageable);

    // then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().getFirst().getGameMode()).isEqualTo(GameMode.ONLINE);

  }

  @Test
  @DisplayName("게임 조회 - OFFLINE만 필터링")
    // 왜 OFFLINE 필터링 테스트는 뺸거지? ONLINE만 하면 된다는건가? 아님 TDD에 위배되나?
  void getGames_Offline() {
    // given
    Pageable pageable = PageRequest.of(0, 10);
    List<Bigwheel> games = List.of(
        createTestGame(GameMode.OFFLINE)
    );

    Page<Bigwheel> page = new PageImpl<>(games, pageable, games.size());

    when(bigwheelRepository.findByUserIdAndGameModeOrderByPlayedAtDesc(1L, GameMode.OFFLINE, pageable)).thenReturn(page);

    // when
    Page<Bigwheel> result = bigwheelService.getGames(1L, GameMode.OFFLINE, pageable);

    // then
    assertThat(result.getContent()).hasSize(1); // 어디서는 isEmpty를 , 어디서는 hasSize를 nullable 을 이게 다 어떤 기준인거지... 진짜 TDD에서는 이런걸 사용할떄 기준을 뭘로 정했을까
    assertThat(result.getContent().getFirst().getGameMode()).isEqualTo(GameMode.OFFLINE);
  }

  @Test
  @DisplayName("단일 게임 조회 성공")
  void getGame() {
    // given
    Bigwheel game = createTestGame(GameMode.ONLINE);
    when(bigwheelRepository.findById(1L)).thenReturn(
        Optional.of(game)); // optional 은 "선택"을 뜻하는 걸로아는데 of는 뭐지?

    // when
    Bigwheel result = bigwheelService.getGame(1L);

    // then
    assertThat(result).isNotNull(); // 존재하는지 확인
    assertThat(result.getGameMode()).isEqualTo(GameMode.ONLINE); // online 인지 확인.
  }

  @Test
  @DisplayName("단일 게임 조회 실패 - 존재하지 않는 게임")
  void getGame_NotFound() {
    // given
    Bigwheel game = createTestGame(GameMode.OFFLINE);
    when(bigwheelRepository.findById(1L)).thenReturn(Optional.empty()); // 없으면 null 반환 예상

    // when & then
    assertThatThrownBy(() -> bigwheelService.getGame(1L))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("게임을 찾을 수 없습니다:");
  }

  private Bigwheel createTestGame(GameMode gameMode) {
    Map<String, Map<String, Integer>> betDetails = new HashMap<>();
    Map<String, Integer> goldBet = new HashMap<>();
    goldBet.put("CHIP_1000", 10);
    betDetails.put("GOLD", goldBet);

    return Bigwheel.builder()
        .user(testUser)
        .betDetails(betDetails)
        .resultSector("GOLD")
        .totalBetAmount(10000L)
        .winningAmount(50000L)
        .netProfit(40000L)
        .gameMode(gameMode)
        .build();
  }


}

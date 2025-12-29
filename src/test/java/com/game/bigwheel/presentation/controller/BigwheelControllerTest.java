package com.game.bigwheel.presentation.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.bigwheel.application.BigwheelService;
import com.game.bigwheel.domain.bigwheel.Bigwheel;
import com.game.bigwheel.domain.bigwheel.GameMode;
import com.game.bigwheel.domain.user.User;
import com.game.bigwheel.presentation.dto.GameSpinRequest;
import com.game.bigwheel.presentation.dto.GameSpinResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = BigwheelController.class)
@AutoConfigureMockMvc(addFilters = false)  // Security Filter 비활성화
@DisplayName("BigwheelController 테스트")
public class BigwheelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private BigwheelService bigwheelService;

  @Test
  @DisplayName("게임 저장 API - 성공")
  void saveGame_success() throws Exception {
    // given: 가짜 HTTP 요청 생성
    Map<String, Map<String, Integer>> betDetails = new HashMap<>();
    Map<String, Integer> goldBet = new HashMap<>();
    goldBet.put("gold", 1);
    betDetails.put("GOLD", goldBet);

    GameSpinRequest request = GameSpinRequest.builder()
        .userId(1L)
        .betDetails(betDetails)
        .gameMode(GameMode.ONLINE)
        .build();

    GameSpinResponse response = GameSpinResponse.builder()
        .gameId(1L)
        .userId(1L)
        .resultSector("GOLD")
        .totalBetAmount(10000L)
        .winningAmount(50000L)
        .netProfit(40000L)
        .gameMode(GameMode.ONLINE)
        .build();

    when(bigwheelService.saveGame(any(GameSpinRequest.class)))
        .thenReturn(response);

    // when & then
    mockMvc.perform(post("/api/v1/games")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))) // object -> json 변환
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.gameId").value(1))
        .andExpect(jsonPath("$.userId").value(1))
        .andExpect(jsonPath("$.resultSector").value("GOLD"))
        .andExpect(jsonPath("$.totalBetAmount").value(10000))
        .andExpect(jsonPath("$.netProfit").value(40000));

  }

  @Test
  @DisplayName("게임 목록 조회 API - 페이징 + 필터링")
  void getGames_withPagingAndFilter() throws Exception {
    // given: Mock User와 Game Entity 생성
    User testUser = User.builder()
        .id(1L)
        .username("testuser")
        .password("password")
        .nickname("tester")
        .email("test@test.com")
        .build();

    Bigwheel game = Bigwheel.builder()
        .gameId(1L)
        .user(testUser)
        .resultSector("GOLD")
        .totalBetAmount(10000L)
        .winningAmount(30000L)
        .netProfit(20000L)
        .gameMode(GameMode.ONLINE)
        .playedAt(LocalDateTime.now())
        .build();

    // Page 객체 생성 (PageImpl 사용)
    Page<Bigwheel> page = new PageImpl<>(
        List.of(game),
        PageRequest.of(0, 10),
        1  // total elements
    );

    // Service Mock 설정
    when(bigwheelService.getGames(eq(1L), eq(GameMode.ONLINE), any()))
        .thenReturn(page);

    // when & then: MockMvc로 HTTP 요청 시뮬레이션
    mockMvc.perform(get("/api/v1/games")
            .header("X-User-Id", "1")
            .param("gameMode", "ONLINE")
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].resultSector").value("GOLD"))
        .andExpect(jsonPath("$.content[0].totalBetAmount").value(10000))
        .andExpect(jsonPath("$.content[0].gameMode").value("ONLINE"))
        .andExpect(jsonPath("$.pageNumber").value(0))
        .andExpect(jsonPath("$.pageSize").value(10))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.hasNext").value(false));
  }

  @Test
  @DisplayName("특정 게임 조회 API - 성공")
  void getGame_success() throws Exception {
    // given: 테스트용 User와 Game Entity 생성
    User testUser = User.builder()
        .id(1L)
        .username("testuser")
        .password("password")
        .nickname("tester")
        .email("test@test.com")
        .build();

    Bigwheel game = Bigwheel.builder()
        .gameId(123L)
        .user(testUser)
        .resultSector("SILVER")
        .totalBetAmount(5000L)
        .winningAmount(10000L)
        .netProfit(5000L)
        .gameMode(GameMode.OFFLINE)
        .playedAt(LocalDateTime.now())
        .build();

    // Service Mock 설정: gameId 123으로 조회 시 game 반환
    when(bigwheelService.getGame(123L)).thenReturn(game);

    // when & then: GET /api/v1/games/123 요청
    mockMvc.perform(get("/api/v1/games/123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.gameId").value(123))
        .andExpect(jsonPath("$.resultSector").value("SILVER"))
        .andExpect(jsonPath("$.totalBetAmount").value(5000))
        .andExpect(jsonPath("$.winningAmount").value(10000))
        .andExpect(jsonPath("$.netProfit").value(5000))
        .andExpect(jsonPath("$.gameMode").value("OFFLINE"));
  }


  @Test
  @DisplayName("베팅 구역 정보 조회 API")
  void getZone_success() throws Exception {
    // given: BetZoneResponse.from()이 정적 메서드라 Mock 불필요
    // BetZone enum: SILVER, GOLD, EMERALD, DIAMOND, CRYSTAL, JOKER, MEGA (총 7개)

    // when & then: GET /api/v1/games/zones 요청
    mockMvc.perform(get("/api/v1/games/zones"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.zones", hasSize(7)))  // BetZone enum 개수
        .andExpect(jsonPath("$.zones[0].name").value("SILVER"))
        .andExpect(jsonPath("$.zones[0].count").value(24))
        .andExpect(jsonPath("$.zones[0].payout").value(2))
        .andExpect(jsonPath("$.zones[1].name").value("GOLD"))
        .andExpect(jsonPath("$.zones[1].count").value(15))
        .andExpect(jsonPath("$.zones[1].payout").value(3))
        .andExpect(jsonPath("$.totalSlots").value(54));  // 24+15+7+4+2+1+1 = 54
  }
}

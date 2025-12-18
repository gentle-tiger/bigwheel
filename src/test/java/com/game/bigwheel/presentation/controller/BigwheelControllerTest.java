package com.game.bigwheel.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.bigwheel.application.BigwheelService;
import com.game.bigwheel.domain.bigwheel.GameMode;
import com.game.bigwheel.presentation.dto.GameSpinRequest;
import com.game.bigwheel.presentation.dto.GameSpinResponse;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
    // given


    // when & then

  }

  @Test
  @DisplayName("툭정 게임 조회 API - 성공")
  void getGame_success() throws Exception {
    // given


    // when & then

  }


  @Test
  @DisplayName("베팅 구역 정보 조회 API")
  void getZone_success() throws Exception {
    // given


    // when & then

  }
}

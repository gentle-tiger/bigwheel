import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

export const userApi = {
  register(user) {
    return axios.post(`${API_BASE_URL}/v1/user/register`, user)
  },

  login(user) {
    return axios.post(`${API_BASE_URL}/v1/user/login`, user)
  },

  getUserChips(userId) {
    return axios.get(`${API_BASE_URL}/v1/user/${userId}/chips`)
  }
}

export const gameApi = {
  // 내 게임 기록 조회 (페이징 + 필터링)
  getMyGames(userId, params = {}) {
    const { gameMode, page = 0, size = 10 } = params

    // 쿼리 파라미터 구성
    const queryParams = new URLSearchParams()
    if (gameMode && gameMode !== 'ALL') {
      queryParams.append('gameMode', gameMode)
    }
    queryParams.append('page', page)
    queryParams.append('size', size)

    return axios.get(`${API_BASE_URL}/v1/games?${queryParams.toString()}`, {
      headers: {
        'X-User-Id': userId
      }
    })
  },

  // 특정 게임 조회
  getGame(gameId, userId) {
    return axios.get(`${API_BASE_URL}/v1/games/${gameId}`, {
      headers: {
        'X-User-Id': userId
      }
    })
  },

  // 게임 저장
  saveGame(gameData) {
    const userId = gameData.userId || localStorage.getItem('userId')
    return axios.post(`${API_BASE_URL}/v1/games`, gameData, {
      headers: {
        'X-User-Id': userId
      }
    })
  },

  // 베팅 구역 정보 조회
  getBetZones() {
    return axios.get(`${API_BASE_URL}/v1/games/zones`)
  }
}

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
  // 사용자별 게임 기록 조회
  getUserGames(userId) {
    return axios.get(`${API_BASE_URL}/v1/games/${userId}`)
  },

  // 전체 게임 기록 조회
  getAllGames() {
    return axios.get(`${API_BASE_URL}/v1/games`)
  }
}

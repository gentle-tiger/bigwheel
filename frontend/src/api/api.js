import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

export const userApi = {
  register(user) {
    return axios.post(`${API_BASE_URL}/register`, user)
  },

  login(user) {
    return axios.post(`${API_BASE_URL}/login`, user)
  }
}

import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Play from '../views/Play.vue'
import BigWheel from '../views/BigWheel.vue'
import Statistics from '../views/Statistics.vue'
import Chips from '../views/Chips.vue'
import Profile from '../views/Profile.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home
    },
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/register',
      name: 'register',
      component: Register
    },
    {
      path: '/play',
      name: 'play',
      component: Play
    },
    {
      path: '/bigwheel',
      name: 'bigwheel',
      component: BigWheel
    },
    {
      path: '/statistics',
      name: 'statistics',
      component: Statistics
    },
    {
      path: '/chips',
      name: 'chips',
      component: Chips
    },
    {
      path: '/profile',
      name: 'profile',
      component: Profile
    },
    // 하위 호환성을 위한 리다이렉트
    {
      path: '/category',
      redirect: '/play'
    },
    {
      path: '/chip-input',
      redirect: '/chips'
    },
    {
      path: '/game-history',
      redirect: '/statistics'
    }
  ]
})

export default router

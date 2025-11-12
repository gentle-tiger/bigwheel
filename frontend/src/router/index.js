import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Category from '../views/Category.vue'
import BigWheel from '../views/BigWheel.vue'
import ChipInput from '../views/ChipInput.vue'
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
      path: '/category',
      name: 'category',
      component: Category
    },
    {
      path: '/bigwheel',
      name: 'bigwheel',
      component: BigWheel
    },
    {
      path: '/chip-input',
      name: 'chip-input',
      component: ChipInput
    },
    {
      path: '/profile',
      name: 'profile',
      component: Profile
    }
  ]
})

export default router

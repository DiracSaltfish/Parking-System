import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

const STORAGE_KEY = 'parking-session'

export const useSessionStore = defineStore('session', () => {
  const profile = ref(loadProfile())

  function loadProfile() {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? JSON.parse(raw) : null
  }

  function login(payload) {
    profile.value = payload
    localStorage.setItem(STORAGE_KEY, JSON.stringify(payload))
  }

  function logout() {
    profile.value = null
    localStorage.removeItem(STORAGE_KEY)
  }

  const isLoggedIn = computed(() => Boolean(profile.value))

  return {
    profile,
    isLoggedIn,
    login,
    logout
  }
})

<template>
  <div class="min-h-screen flex items-center justify-center bg-green-50">
    <div class="bg-white p-8 rounded-lg shadow-md w-full max-w-md text-center">
      <h2 class="text-2xl font-bold mb-4">Dashboard</h2>
      <p class="mb-6">Welcome! You are logged in.</p>
      <button @click="logout" class="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 transition">Logout</button>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'
const auth = useAuthStore()
const logout = auth.logout
</script>

<style scoped></style>

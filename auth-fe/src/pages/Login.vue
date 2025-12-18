<script setup>
import LoginForm from "../components/LoginForm.vue";
import GoogleLoginButton from "../components/GoogleLoginButton.vue";
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100">
    <div class="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
      <h2 class="text-2xl font-bold mb-6 text-center">Login</h2>
      <LoginForm />
      <div class="flex items-center my-6">
        <div class="flex-grow border-t border-gray-300"></div>
        <span class="mx-4 text-gray-400">or</span>
        <div class="flex-grow border-t border-gray-300"></div>
      </div>
      <GoogleLoginButton />
    </div>
  </div>
</template>

<style scoped></style>

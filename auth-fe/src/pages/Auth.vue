<script setup>
import { ref } from "vue";
import LoginForm from "../components/LoginForm.vue";
import RegisterForm from "../components/RegisterForm.vue";
import GoogleLoginButton from "../components/GoogleLoginButton.vue";

const isLogin = ref(true);

function toggleForm() {
  isLogin.value = !isLogin.value;
}
</script>

<template>
  <div
    class="min-h-screen flex items-center justify-center bg-gray-100"
    style="
      background-image: url('/auth-bg.jpg');
      background-size: cover;
      background-position: center;
    "
  >
    <div class="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
      <h2 class="text-2xl font-bold mb-6 text-center">
        {{ isLogin ? "Login" : "Register" }}
      </h2>
      <LoginForm v-if="isLogin" />
      <RegisterForm v-else />
      <div class="flex items-center my-6">
        <div class="flex-grow border-t border-gray-300"></div>
        <span class="mx-4 text-gray-400">or</span>
        <div class="flex-grow border-t border-gray-300"></div>
      </div>
      <button
        @click="toggleForm"
        class="w-full py-2 px-4 bg-gray-200 rounded hover:bg-gray-300 mb-2"
      >
        {{ isLogin ? "Switch to Register" : "Switch to Login" }}
      </button>
      <GoogleLoginButton />
    </div>
  </div>
</template>

<style scoped></style>

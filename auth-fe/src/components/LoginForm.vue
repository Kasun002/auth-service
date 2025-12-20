<script setup>
import { ref, onMounted } from "vue";
import { useAuthStore } from "../stores/auth";
import { useRouter } from "vue-router";

const username = ref("");
const password = ref("");
const auth = useAuthStore();
const router = useRouter();
const showPassword = ref(false);

onMounted(() => {
  const authObj = sessionStorage.getItem("auth");
  if (authObj) {
    try {
      const { accessToken } = JSON.parse(authObj);
      if (accessToken) {
        if (router.currentRoute.value.path === "/") {
          router.push("/dashboard");
        }
      }
    } catch {}
  }
});

const onSubmit = () => {
  auth.login({ username: username.value, password: password.value });
};
</script>

<template>
  <form @submit.prevent="onSubmit" class="space-y-6">
    <div>
      <label for="username" class="block text-sm font-medium text-gray-700"
        >Username</label
      >
      <input
        id="username"
        v-model="username"
        type="text"
        autocomplete="username"
        required
        class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
      />
    </div>
    <div>
      <label for="password" class="block text-sm font-medium text-gray-700">Password</label>
      <div class="relative">
        <input
          id="password"
          v-model="password"
          :type="showPassword ? 'text' : 'password'"
          autocomplete="current-password"
          required
          class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 pr-10"
        />
        <button type="button" @click="showPassword = !showPassword" class="absolute inset-y-0 right-0 px-3 flex items-center text-gray-400 focus:outline-none">
          <svg v-if="showPassword" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.477 0 8.268 2.943 9.542 7-.274.832-.66 1.613-1.144 2.318M15.362 17.362A8.962 8.962 0 0112 19c-4.477 0-8.268-2.943-9.542-7a9.956 9.956 0 012.362-3.362" />
          </svg>
          <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.477 0-8.268-2.943-9.542-7a9.956 9.956 0 012.362-3.362m3.638-2.638A8.962 8.962 0 0112 5c4.477 0 8.268 2.943 9.542 7-.274.832-.66 1.613-1.144 2.318M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3l18 18" />
          </svg>
        </button>
      </div>
    </div>
    <div>
      <button
        type="submit"
        class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
      >
        Login
      </button>
    </div>
    <div v-if="auth.error" class="text-red-500 text-center">
      {{ auth.error }}
    </div>
  </form>
</template>

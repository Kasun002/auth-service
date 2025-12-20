<script setup>
import { ref } from "vue";
import { useAuthStore } from "../stores/auth";
import { useRouter } from "vue-router";

const username = ref("");
const password = ref("");
const email = ref("");
const role = ref("MAKER");
const loading = ref(false);
const error = ref(null);
const router = useRouter();
const showDropdown = ref(false);

const roles = [
  { label: "MAKER", value: "MAKER" },
  { label: "CHECKER", value: "CHECKER" },
];

async function onRegister() {
  loading.value = true;
  error.value = null;
  try {
    await useAuthStore().register({
      username: username.value,
      password: password.value,
      email: email.value,
      role: role.value,
    });
    router.push("/");
  } catch (err) {
    error.value = err.response?.data?.message || "Registration failed";
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <form @submit.prevent="onRegister" class="space-y-6">
    <div>
      <label for="username" class="block text-sm font-medium text-gray-700"
        >Username</label
      >
      <input
        id="username"
        v-model="username"
        type="text"
        required
        class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
      />
    </div>
    <div>
      <label for="email" class="block text-sm font-medium text-gray-700"
        >Email</label
      >
      <input
        id="email"
        v-model="email"
        type="email"
        required
        class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
      />
    </div>
    <div>
      <label for="password" class="block text-sm font-medium text-gray-700"
        >Password</label
      >
      <input
        id="password"
        v-model="password"
        type="password"
        required
        class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
      />
    </div>
    <div>
      <label for="role" class="block text-sm font-medium text-gray-700">Role</label>
      <div class="relative mt-1">
        <button type="button" @click="showDropdown = !showDropdown" class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm bg-white text-left focus:outline-none focus:ring-blue-500 focus:border-blue-500 flex justify-between items-center">
          <span>{{ roles.find(r => r.value === role)?.label }}</span>
          <svg class="w-4 h-4 ml-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/></svg>
        </button>
        <div v-if="showDropdown" class="absolute z-10 mt-1 w-full bg-white border border-gray-300 rounded-md shadow-lg">
          <ul>
            <li v-for="r in roles" :key="r.value">
              <button type="button" @click="role = r.value; showDropdown = false" class="w-full text-left px-3 py-2 hover:bg-blue-100 focus:bg-blue-100">
                {{ r.label }}
              </button>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <div v-if="error" class="text-red-500 text-sm">{{ error }}</div>
    <button
      type="submit"
      :disabled="loading"
      class="w-full py-2 px-4 bg-blue-600 text-white rounded hover:bg-blue-700"
    >
      Register
    </button>
  </form>
</template>

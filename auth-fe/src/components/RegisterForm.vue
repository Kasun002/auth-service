<script setup>
import { ref, computed } from "vue";
import { useAuthStore } from "../stores/auth";
import { useRouter } from "vue-router";
import { passwordRegex } from "../utils/validation.js";

const auth = useAuthStore();
const username = ref("");
const password = ref("");
const email = ref("");
const role = ref("MAKER");

const isValid = computed(() => {
  return (
    username.value &&
    email.value &&
    role.value &&
    passwordRegex.test(password.value)
  );
});

const roles = [
  { label: "MAKER", value: "MAKER" },
  { label: "CHECKER", value: "CHECKER" },
];

const onRegister = () => {
  if (isValid.value) {
    auth.register({
      username: username.value,
      password: password.value,
      email: email.value,
      role: role.value,
    });
  }
};
</script>

<template>
  <FormKit type="form" ref="form" :actions="false">
    <FormKit
      type="text"
      label="Username"
      v-model="username"
      validation="required"
      :disabled="auth.loading"
    />
    <FormKit
      type="email"
      label="Email"
      v-model="email"
      validation="required|email"
      :disabled="auth.loading"
    />
    <FormKit
      type="password"
      label="Password"
      v-model="password"
      :validation="'required|matches:' + passwordRegex.source"
      validation-label="Password (min 8 chars, upper, lower, number, special)"
      :disabled="auth.loading"
    />
    <FormKit
      type="select"
      label="Role"
      v-model="role"
      :options="roles"
      validation="required"
      :disabled="auth.loading"
    />
    <button
      type="button"
      :disabled="!isValid || auth.loading"
      @click="onRegister"
      :class="[
        'w-full py-2 px-4 rounded',
        (!isValid || auth.loading)
          ? 'bg-gray-400 text-white cursor-not-allowed'
          : 'bg-blue-600 text-white hover:bg-blue-700'
      ]"
    >
      Register
    </button>
  </FormKit>
</template>

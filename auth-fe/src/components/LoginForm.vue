<script setup>
import { ref, computed } from "vue";
import { useAuthStore } from "../stores/auth";

const auth = useAuthStore();
const username = ref("");
const password = ref("");
const form = ref(null);
const touched = ref(false);

const onInput = () => {
  touched.value = true;
};

const isValid = computed(() => {
  return username.value && password.value;
});

const isButtonDisabled = computed(() => {
  return !isValid.value || auth.loading || !touched.value;
});

const onSubmit = () => {
  if (isValid.value) {
    auth.login({ username: username.value, password: password.value });
  }
};
</script>

<template>
  <FormKit type="form" ref="form" @submit.prevent="onSubmit" :actions="false">
    <FormKit
      type="text"
      label="Username"
      v-model="username"
      validation="required"
      :disabled="auth.loading"
      @input="onInput"
    />
    <FormKit
      type="password"
      label="Password"
      v-model="password"
      validation="required"
      validation-label="Password (min 8 chars, upper, lower, number, special)"
      :disabled="auth.loading"
      @input="onInput"
    />
    <button
      type="button"
      :disabled="isButtonDisabled"
      @click="onSubmit"
      :class="[
        'w-full py-2 px-4 rounded',
        isButtonDisabled
          ? 'bg-gray-400 text-white cursor-not-allowed'
          : 'bg-blue-600 text-white hover:bg-blue-700'
      ]"
    >
      Login
    </button>
  </FormKit>
</template>

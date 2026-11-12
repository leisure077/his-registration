<script setup>
import { useLoadingStore } from '@/stores'
const loadingStore = useLoadingStore()
</script>

<template>
  <router-view v-loading.fullscreen.lock="loadingStore.loading" />
</template>

<style scoped></style>

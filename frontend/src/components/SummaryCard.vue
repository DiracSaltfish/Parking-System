<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  value: {
    type: String,
    required: true
  },
  tone: {
    type: String,
    default: 'sun'
  }
})
</script>

<template>
  <div class="summary-card" :data-tone="tone">
    <span class="summary-card__title">{{ title }}</span>
    <strong class="summary-card__value">{{ value }}</strong>
  </div>
</template>

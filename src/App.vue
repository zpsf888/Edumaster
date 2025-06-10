<script setup lang="ts">
import { RouterView } from 'vue-router'
import NavBar from './components/NavBar.vue'
</script>

<template>
  <NavBar />
  <router-view></router-view>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  width: 100%;
  margin: 0;
  padding: 0;
  overflow-x: hidden;
  min-width: 100vw;
}

body {
  font-family: Arial, sans-serif;
  line-height: 1.6;
  background-color: #ffffff;
  position: relative;
}

#app {
  position: relative;
  min-height: 100vh;
}

.router-view {
  width: 100%;
  margin: 0;
  padding: 0;
  min-width: 100vw;
}
</style>

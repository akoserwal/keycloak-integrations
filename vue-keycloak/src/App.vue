<template>
  <div id="app">
    <img alt="Vue logo" src="./assets/logo.png" style="margin-left: 50px;">
     <img alt="heart" src="./assets/heart.png" style="height: 100px;margin: 30px;">
    <img alt="keycloak logo" src="./assets/keycloak.png">
    <HelloWorld msg="Welcome to Your Vue.js App secured with Keycloak"/>
    
  </div>
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'

export default {
  name: 'app',
  components: {
    HelloWorld
  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>

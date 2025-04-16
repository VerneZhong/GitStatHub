import { createApp } from 'vue'
import App from './App.vue'
import './assets/main.css'
import VChart from 'vue-echarts'
import 'echarts'

const app = createApp(App)
app.component('v-chart', VChart)
app.mount('#app')


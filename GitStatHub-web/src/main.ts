import { createApp } from 'vue'
import App from './App.vue'
import './assets/main.css'
import VChart from 'vue-echarts'
import 'echarts'
import router from './router'
import './assets/main.css';

const app = createApp(App)
app.use(router);
app.component('v-chart', VChart)
app.mount('#app')


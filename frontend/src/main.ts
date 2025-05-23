import { createApp } from 'vue';
import App from './App.vue';
import router from './router';

// // Import Vuetify 3
// import { createVuetify } from 'vuetify';
// import 'vuetify/styles';
// import { aliases, mdi } from 'vuetify/iconsets/mdi-svg';

// // Create Vuetify instance
// const vuetify = createVuetify({
//   icons: {
// 	defaultSet: 'mdi',
// 	aliases,
// 	sets: { mdi },
//   },
// });

const app = createApp(App);
app.use(router);
// app.use(vuetify);

app.mount('#app');

export default app;
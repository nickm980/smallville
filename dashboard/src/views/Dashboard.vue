<!-- 
	This is the dashboard page, it uses the dashboard layout in: 
	"./layouts/Dashboard.vue" .
 -->

<template>
  <div>
    <!-- Counter Widgets -->
    <a-row :gutter="24">
      <a-col
        :span="24"
        :lg="12"
        :xl="12"
        class="mb-24"
        v-for="(stat, index) in stats"
        :key="index"
      >
        <!-- Widget 1 Card -->
        <WidgetCounter
          :title="stat.title"
          :value="stat.value"
          :prefix="stat.prefix"
          :suffix="stat.suffix"
          :icon="stat.icon"
          :status="stat.status"
        ></WidgetCounter>
        <!-- / Widget 1 Card -->
      </a-col>
    </a-row>

    <a-row :gutter="24">
      <a-col :gutter="24" :span="24" :xl="24" :lg="24">
        <a-card :bordered="false" class="card-info mb-24">
          <a-row type="flex">
            <a-col class="col-content">
              <div class="card-content">
                <h5>Updating the time and state</h5>
                <p>
                  Updating the state can take about 10 seconds-3 minutes. You
                  can also update the state manually from the example game.
                </p>
              </div>
              <div class="card-footer">
                <a href="#" size="small">
                  <a-button v-if="loading" type="primary">Loading...</a-button>
                  <a-button v-else type="primary" @click="updateState"
                    >Update State</a-button
                  >
                </a>
              </div>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
    </a-row>
    <!-- / Counter Widgets -->
    <a-row>
      <a-card :bordered="false" class="card-info mb-24">
        <a-row type="flex">
          <a-col class="col-content" :span="24" :xl="12">
            <div class="card-content">
              <h5>Prompts</h5>
              <p>
                View all the prompt requests and responses that were created in
                the last update
              </p>
              <a-collapse v-model="activeKey">
                <a-collapse-panel
                  v-for="prompt in prompts"
                  :key="prompt"
                  :header="getHeader(prompt)"
                >
                  {{ prompt }}
                </a-collapse-panel>
              </a-collapse>
            </div>
          </a-col>
        </a-row>
      </a-card>
    </a-row>
  </div>
</template>

<script>
import CardBarChart from "../components/Cards/CardBarChart";
import CardLineChart from "../components/Cards/CardLineChart";
import WidgetCounter from "../components/Widgets/WidgetCounter";
import CardProjectTable from "../components/Cards/CardProjectTable";
import CardOrderHistory from "../components/Cards/CardOrderHistory";
import CardInfo from "../components/Cards/CardInfo";
import CardInfo2 from "../components/Cards/CardInfo2";
import { updateGameState, getGameInfo } from "../server";

// Counter Widgets stats
const stats = [
  {
    title: "Current Time",
    value: "loading...",
    icon: `
						<svg width="22" height="22" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
							<path d="M8.43338 7.41784C8.58818 7.31464 8.77939 7.2224 9 7.15101L9.00001 8.84899C8.77939 8.7776 8.58818 8.68536 8.43338 8.58216C8.06927 8.33942 8 8.1139 8 8C8 7.8861 8.06927 7.66058 8.43338 7.41784Z" fill="#111827"/>
							<path d="M11 12.849L11 11.151C11.2206 11.2224 11.4118 11.3146 11.5666 11.4178C11.9308 11.6606 12 11.8861 12 12C12 12.1139 11.9308 12.3394 11.5666 12.5822C11.4118 12.6854 11.2206 12.7776 11 12.849Z" fill="#111827"/>
							<path fill-rule="evenodd" clip-rule="evenodd" d="M10 18C14.4183 18 18 14.4183 18 10C18 5.58172 14.4183 2 10 2C5.58172 2 2 5.58172 2 10C2 14.4183 5.58172 18 10 18ZM11 5C11 4.44772 10.5523 4 10 4C9.44772 4 9 4.44772 9 5V5.09199C8.3784 5.20873 7.80348 5.43407 7.32398 5.75374C6.6023 6.23485 6 7.00933 6 8C6 8.99067 6.6023 9.76515 7.32398 10.2463C7.80348 10.5659 8.37841 10.7913 9.00001 10.908L9.00002 12.8492C8.60902 12.7223 8.31917 12.5319 8.15667 12.3446C7.79471 11.9275 7.16313 11.8827 6.74599 12.2447C6.32885 12.6067 6.28411 13.2382 6.64607 13.6554C7.20855 14.3036 8.05956 14.7308 9 14.9076L9 15C8.99999 15.5523 9.44769 16 9.99998 16C10.5523 16 11 15.5523 11 15L11 14.908C11.6216 14.7913 12.1965 14.5659 12.676 14.2463C13.3977 13.7651 14 12.9907 14 12C14 11.0093 13.3977 10.2348 12.676 9.75373C12.1965 9.43407 11.6216 9.20873 11 9.09199L11 7.15075C11.391 7.27771 11.6808 7.4681 11.8434 7.65538C12.2053 8.07252 12.8369 8.11726 13.254 7.7553C13.6712 7.39335 13.7159 6.76176 13.354 6.34462C12.7915 5.69637 11.9405 5.26915 11 5.09236V5Z" fill="#111827"/>
						</svg>`,
  },
  {
    title: "Time Step",
    value: "loading...",
    icon: `
						<svg width="22" height="22" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
							<path d="M9 6C9 7.65685 7.65685 9 6 9C4.34315 9 3 7.65685 3 6C3 4.34315 4.34315 3 6 3C7.65685 3 9 4.34315 9 6Z" fill="#111827"/>
							<path d="M17 6C17 7.65685 15.6569 9 14 9C12.3431 9 11 7.65685 11 6C11 4.34315 12.3431 3 14 3C15.6569 3 17 4.34315 17 6Z" fill="#111827"/>
							<path d="M12.9291 17C12.9758 16.6734 13 16.3395 13 16C13 14.3648 12.4393 12.8606 11.4998 11.6691C12.2352 11.2435 13.0892 11 14 11C16.7614 11 19 13.2386 19 16V17H12.9291Z" fill="#111827"/>
							<path d="M6 11C8.76142 11 11 13.2386 11 16V17H1V16C1 13.2386 3.23858 11 6 11Z" fill="#111827"/>
						</svg>`,
  },
];

export default {
  components: {
    CardLineChart,
    WidgetCounter,
    CardProjectTable,
  },
  methods: {
    async updateState(event) {
      event.preventDefault();
      this.loading = true;

      updateGameState().then((data) => {
        this.loading = false;
      });
    },
    getHeader(sentence) {
      let truncatedSentence = sentence.substring(0, 50);
      if (sentence.length > 50) {
        truncatedSentence += "...";
      }

      console.log(truncatedSentence);
      return truncatedSentence;
    },
  },
  mounted() {
    getGameInfo()
      .then((data) => {
        this.prompts = data.prompts;

        (this.stats = [
          {
            title: "Current Time",
            value: data.time, // Update the value here
            icon: `
          <svg width="22" height="22" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <!-- SVG icon code -->
          </svg>
        `,
          },
          {
            title: "Update interval",
            value: data.step, // Update the value here
            suffix: "minutes",
            icon: `
          <svg width="22" height="22" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <!-- SVG icon code -->
          </svg>
        `,
          },
        ]),
          (this.step = data.step);
      })
      .catch((error) => {
        console.error("Error updating state:", error);
      });
  },
  data() {
    return {
      step: "loading...",
      time: "loading...",
      prompts: [],
      loading: false,
      stats: stats,
    };
  },
};
</script>

<style lang="scss"></style>

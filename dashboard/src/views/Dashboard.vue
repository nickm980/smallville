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
                  <a-button v-else type="primary" @click="updateState">Update State</a-button>
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
import CardLineChart from "../components/Cards/CardLineChart";
import WidgetCounter from "../components/Widgets/WidgetCounter";
import CardProjectTable from "../components/Cards/CardProjectTable";
import { updateGameState, getGameInfo } from "../server";

// Counter Widgets stats
const stats = [
  {
    title: "Current Time",
    value: "loading..."
  },
  {
    title: "Time Step",
    value: "loading...",
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
          },
          {
            title: "Update interval (minutes)",
            value: data.step, // Update the value here
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

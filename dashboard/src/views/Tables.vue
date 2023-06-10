<template>
  <div>
    <a-row :gutter="24" type="flex">
      <a-col :span="24" :lg="16" class="mb-24">
        <CardAuthorTable
          :data="agentData"
          :columns="agentColumns"
        ></CardAuthorTable>
      </a-col>
      <a-col :span="24" :lg="8" class="mb-24">
        <CardProjectTable2
          :data="locationData"
          :columns="table2Columns"
        ></CardProjectTable2>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-card class="b-card">
        <h5 class="font-semibold m-0">Interview Agents</h5>
        <p>Ask the agents questions taking the role of a newscaster</p>
        <a-col span="4">
          <a-dropdown>
            <a class="ant-dropdown-link" @click="(e) => e.preventDefault()">
              {{ currentAgent }} <a-icon type="down" />
            </a>
            <a-menu slot="overlay">
              <a-menu-item
                v-for="agent in agentData"
                @click="setAgent(agent.name)"
              >
                <a href="javascript:;">{{ agent.name }}</a>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </a-col>
        <a-col span="20">
          <a-input
            placeholder="What do you have planned for today?"
            @keyup.enter="submitInput"
          >
            <a-tooltip slot="suffix" title="Interview an agent">
              <a-icon type="info-circle" style="color: rgba(0, 0, 0, 0.45)" />
            </a-tooltip>
          </a-input>
        </a-col>
      </a-card>
    </a-row>
    <a-modal v-model="visible" title="Interivew Answer" @ok="toggleModal">
      <p>{{ answer }}</p>
    </a-modal>
    <a-modal
      v-model="memoryStreamVisible"
      title="Memory Stream"
      @ok="toggleMemoryStream"
    >
      <p>View the memory stream of the agent</p>
      <CardAuthorTable
        :data="agentData"
        :columns="agentColumns"
      ></CardAuthorTable>
    </a-modal>
  </div>
</template>

<script>
import CardAuthorTable from "../components/Cards/CardAuthorTable";
import CardProjectTable2 from "../components/Cards/CardProjectTable2";
import { getAgents, getLocations, ask } from "../server";

const agentColumns = [
  {
    title: "NAME",
    dataIndex: "name",
  },
  {
    title: "CURRENT ACTIVITY",
    dataIndex: "action",
  },
  {
    title: "LOCATION",
    dataIndex: "location",
  },
  {
    title: "OBJECT",
    dataIndex: "object",
  },
  {
    title: "EMOJI",
    dataIndex: "emoji",
  },
];

const table2Columns = [
  {
    title: "LOCATION",
    dataIndex: "name",
  },

  {
    title: "STATE",
    dataIndex: "state",
    class: "font-semibold text-muted",
  },
  {
    title: "",
    scopedSlots: { customRender: "editBtn" },
    width: 50,
  },
];

export default {
  components: {
    CardAuthorTable,
    CardProjectTable2,
  },
  methods: {
    toggleModal() {
      this.visible = !this.visible;
    },
    async submitInput() {
      this.toggleModal();
      this.answer = "Loading...";
      const answer = await ask(this.currentAgent, "How are you");
      if (answer.error) {
        alert("Error pick an agent");
        return;
      }

      this.answer = answer.answer;
    },
    setAgent(agent) {
      this.currentAgent = agent;
    },
  },
  data() {
    return {
      loading: true,
      answer: "No data",
      visible: false,
      memoryStreamVisible: false,
      currentAgent: "Choose an agent",
      agentData: [],
      agentColumns: agentColumns,
      locationData: [],
      table2Columns: table2Columns,
    };
  },
  mounted() {
    getAgents().then((data) => {
      this.agentData = data.agents;
    });
    getLocations().then((data) => {
      this.locationData = data.locations;
    });
  },
};
</script>

<style lang="scss"></style>

<template>
  <!-- Projects Table Column -->
  <div>
    <a-card
      :bordered="false"
      class="header-solid h-full"
      :bodyStyle="{ padding: 0 }"
    >
      <template #title>
        <a-row type="flex" align="middle">
          <a-col :span="24" :md="12">
            <h5 class="font-semibold m-0">Locations and Objects</h5>
          </a-col>
        </a-row>
      </template>
      <a-table :columns="columns" :data-source="data" :pagination="false">
        <template slot="name" slot-scope="text">
          <a>{{ text }}</a>
        </template>

        <a-space
          slot="members"
          slot-scope="members"
          :size="-12"
          class="avatar-chips"
        >
          <template v-for="member in members">
            <a-avatar :key="member" size="small" :src="member" />
          </template>
        </a-space>

        <template slot="completion" slot-scope="completion">
          <div class="progress-right">
            <div class="text-right text-sm font-semibold text-muted pr-15">
              {{
                completion.value || completion.value == 0
                  ? completion.value
                  : completion
              }}%
            </div>
            <a-progress
              class="m-0"
              :percent="
                completion.value || completion.value == 0
                  ? completion.value
                  : completion
              "
              :show-info="false"
              size="small"
              :status="completion.status ? completion.status : 'normal'"
            />
          </div>
        </template>

        <template slot="editBtn" slot-scope="row">
          <a-button
            type="link"
            :data-id="row.key"
            class="btn-edit"
            @click="toggleModal(row)"
          >
            Edit
          </a-button>
        </template>
      </a-table>
    </a-card>
    <a-modal v-model="visible" title="Location State" @ok="submitModal">
      <p>Enter the new location state for {{ currentLocation.name }}</p>
      <a-input :placeholder="currentLocation.state" v-model="input">df</a-input>
    </a-modal>
  </div>
  <!-- / Projects Table Column -->
</template>

<script>
import {changeLocation} from '../../server'

export default {
  props: {
    data: {
      type: Array,
      default: () => [],
    },
    columns: {
      type: Array,
      default: () => [],
    },
  },
  methods: {
    toggleModal(row) {
      this.currentLocation = row;
      this.visible = !this.visible;
    },
    submitModal(event) {
      this.visible = !this.visible;
      changeLocation(this.currentLocation.name, this.input);
      this.input = "";
    },
  },
  data() {
    return {
      input: "",
      currentLocation: "",
      visible: false,
      // Active button for the "Projects" table's card header radio button group.
      projectHeaderBtns: "all",
    };
  },
};
</script>

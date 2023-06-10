<template>

	<a-card :bordered="false" class="widget-1">
		<a-statistic
			:title="title"
			:value="value"
			:prefix="prefix"
			:suffix="suffix"
			:precision="0"
			class="text-success"
			:class="'text-' + status"
		>
		</a-statistic>
		<div class="icon" v-html="icon"></div>
	</a-card>

</template>

<script>

	export default ({
		props: {
			title: {
				type: String,
				default: "",
			},
			value: {
				type: String,
				default: "",
			},
			prefix: {
				type: String,
				default: "",
			},
			suffix: {
				type: String,
				default: "",
			},
			icon: {
				type: String,
				default: "",
			},
			status: {
				type: String,
				default: "success",
			},
		},
		data() {
			return {
			}
		},
	})

</script>
<template>

	<!-- Salary Card -->
	<a-card :bordered="false" class="widget-2 h-full">
		<a-statistic :value="value" :prefix="prefix">
			<template #title>
				<div class="icon" v-html="icon"></div>
				<h6>{{ title }}</h6>
				<p>{{ content }}</p>
			</template>
		</a-statistic>
	</a-card>
	<!-- / Salary Card -->

</template>

<script>

	export default ({
		props: {
			value: {
				type: Number,
				default: 0,
			},
			prefix: {
				type: String,
				default: "+$",
			},
			icon: {
				type: String,
				default: "",
			},
			title: {
				type: String,
				default: "",
			},
			content: {
				type: String,
				default: "",
			},
		},
		data() {
			return {
			}
		},
	})

</script>
<template>

	<!-- Payment Methods Card -->
	<a-card :bordered="false" class="header-solid h-full" :bodyStyle="{paddingTop: 0,}">
		<template #title>
			<a-row type="flex" align="middle">
				<a-col :span="24" :md="12">
					<h6 class="font-semibold m-0">Payment Methods</h6>
				</a-col>
				<a-col :span="24" :md="12" style="display: flex; align-items: center; justify-content: flex-end">
					<a-button type="primary">
						ADD NEW CARD
					</a-button>
				</a-col>
			</a-row>
		</template>
		<a-row :gutter="[24, 24]">
			<a-col :span="24" :md="12">
				<a-card class="payment-method-card">
					<img src="images/logos/mastercard-logo.png" alt="">
					<h6 class="card-number">**** **** **** 7362</h6>
					<a-button type="link">
						<svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
							<path class="fill-gray-7" d="M13.5858 3.58579C14.3668 2.80474 15.6332 2.80474 16.4142 3.58579C17.1953 4.36683 17.1953 5.63316 16.4142 6.41421L15.6213 7.20711L12.7929 4.37868L13.5858 3.58579Z"/>
							<path class="fill-gray-7" d="M11.3787 5.79289L3 14.1716V17H5.82842L14.2071 8.62132L11.3787 5.79289Z"/>
						</svg>
					</a-button>
				</a-card>
			</a-col>
			<a-col :span="24" :md="12">
				<a-card class="payment-method-card">
					<img src="images/logos/visa-logo.png" alt="">
					<h6 class="card-number">**** **** **** 3288</h6>
					<a-button type="link">
						<svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
							<path class="fill-gray-7" d="M13.5858 3.58579C14.3668 2.80474 15.6332 2.80474 16.4142 3.58579C17.1953 4.36683 17.1953 5.63316 16.4142 6.41421L15.6213 7.20711L12.7929 4.37868L13.5858 3.58579Z"/>
							<path class="fill-gray-7" d="M11.3787 5.79289L3 14.1716V17H5.82842L14.2071 8.62132L11.3787 5.79289Z"/>
						</svg>
					</a-button>
				</a-card>
			</a-col>
		</a-row>
	</a-card>
	<!-- Payment Methods Card -->

</template>

<script>

	export default ({
		data() {
			return {
			}
		},
	})

</script>
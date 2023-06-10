<template>

	<!-- Settings Drawer -->
	<a-drawer
		class="settings-drawer"
		:class="[ rtl ? 'settings-drawer-rtl' : '' ]"
		:placement="rtl ? 'left' : 'right'"
		:closable="false"
		:visible="showSettingsDrawer"
		width="360"
		:getContainer="() => wrapper"
		@close="$emit('toggleSettingsDrawer', false)"
	>

		<!-- Settings Drawer Close Button -->
		<a-button type="link" class="btn-close" @click="$emit('toggleSettingsDrawer', false)">
			<svg xmlns="http://www.w3.org/2000/svg" width="9" height="9" viewBox="0 0 9 9">
				<g id="close" transform="translate(0.75 0.75)">
					<path id="Path" d="M7.5,0,0,7.5" fill="none" stroke="#000" stroke-linecap="round" stroke-linejoin="round" stroke-miterlimit="10" stroke-width="1.5"/>
					<path id="Path-2" data-name="Path" d="M0,0,7.5,7.5" fill="none" stroke="#000" stroke-linecap="round" stroke-linejoin="round" stroke-miterlimit="10" stroke-width="1.5"/>
				</g>
			</svg>
		</a-button>
		<!-- / Settings Drawer Close Button -->
		
		<!-- Settings Drawer Content -->
		<div class="drawer-content">
			<h6>Configurator</h6>
			<p>See our dashboard options.</p>
			<hr>
			<div class="sidebar-color">
				<h6>Sidebar Color</h6>
				<a-radio-group v-model="sidebarColorModel" @change="$emit('updateSidebarColor', $event.target.value)" defaultValue="primary">
					<a-radio-button value="primary" class="bg-primary"></a-radio-button>
					<a-radio-button value="secondary" class="bg-secondary"></a-radio-button>
					<a-radio-button value="success" class="bg-success"></a-radio-button>
					<a-radio-button value="danger" class="bg-danger"></a-radio-button>
					<a-radio-button value="warning" class="bg-warning"></a-radio-button>
					<a-radio-button value="black" class="bg-dark"></a-radio-button>
				</a-radio-group>
			</div>
			<div class="sidenav-type">
				<h6>Sidenav Type</h6>
				<p>Choose between 2 different sidenav types.</p>
				<a-radio-group button-style="solid" v-model="sidebarThemeModel" @change="$emit('updateSidebarTheme', $event.target.value)" defaultValue="primary">
					<a-radio-button value="light">TRANSPARENT</a-radio-button>
					<a-radio-button value="white">WHITE</a-radio-button>
				</a-radio-group>
			</div>
			<div class="navbar-fixed">
				<h6>Navbar Fixed</h6>
				<a-switch default-checked v-model="navbarFixedModel" @change="$emit('toggleNavbarPosition', navbarFixedModel)" />
			</div>
			<div class="download">
				<a-button type="dark" href="https://www.creative-tim.com/product/muse-vue-ant-design-dashboard" block target="_blank">FREE DOWNLOAD</a-button>
				<a-button type="primary" href="https://www.creative-tim.com/product/muse-vue-ant-design-dashboard-pro" block target="_blank">UPGRADE TO PRO</a-button>
				<a-button type="secondary" href="https://demos.creative-tim.com/muse-vue-ant-design-dashboard/documentation" block target="_blank">VIEW DOCUMENTATION</a-button>
			</div>
			<div class="github-stars">
				<gh-btns-star slug="creativetimofficial/muse-vue-ant-design-dashboard" show-count></gh-btns-star>
			</div>
			<div class="sharing">
				<h6>Thank you for sharing!</h6>
				<div class="share-links">
					<a-button type="dark" target="_blank" href="https://twitter.com/intent/tweet?url=https://www.creative-tim.com/product/muse-vue-ant-design-dashboard&text=Check%20Muse%20Vue%20Ant%20Design%20made%20by%20@CreativeTim%20#webdesign%20#dashboard%20#antdesign%20#vue%20https://www.creative-tim.com/product/muse-vue-ant-design-dashboard" size="small">
						<svg width="15px" height="15px" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><path d="M459.37 151.716c.325 4.548.325 9.097.325 13.645 0 138.72-105.583 298.558-298.558 298.558-59.452 0-114.68-17.219-161.137-47.106 8.447.974 16.568 1.299 25.34 1.299 49.055 0 94.213-16.568 130.274-44.832-46.132-.975-84.792-31.188-98.112-72.772 6.498.974 12.995 1.624 19.818 1.624 9.421 0 18.843-1.3 27.614-3.573-48.081-9.747-84.143-51.98-84.143-102.985v-1.299c13.969 7.797 30.214 12.67 47.431 13.319-28.264-18.843-46.781-51.005-46.781-87.391 0-19.492 5.197-37.36 14.294-52.954 51.655 63.675 129.3 105.258 216.365 109.807-1.624-7.797-2.599-15.918-2.599-24.04 0-57.828 46.782-104.934 104.934-104.934 30.213 0 57.502 12.67 76.67 33.137 23.715-4.548 46.456-13.32 66.599-25.34-7.798 24.366-24.366 44.833-46.132 57.827 21.117-2.273 41.584-8.122 60.426-16.243-14.292 20.791-32.161 39.308-52.628 54.253z"/></svg>
						TWEET
					</a-button>
					<a-button type="dark" target="_blank" href="https://www.facebook.com/sharer/sharer.php?u=https://www.creative-tim.com/product/muse-vue-ant-design-dashboard"  size="small">
						<svg width="15px" height="15px" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><path d="M503.691 189.836L327.687 37.851C312.281 24.546 288 35.347 288 56.015v80.053C127.371 137.907 0 170.1 0 322.326c0 61.441 39.581 122.309 83.333 154.132 13.653 9.931 33.111-2.533 28.077-18.631C66.066 312.814 132.917 274.316 288 272.085V360c0 20.7 24.3 31.453 39.687 18.164l176.004-152c11.071-9.562 11.086-26.753 0-36.328z"/></svg>
						SHARE
					</a-button>
				</div>
			</div>
		</div>
		<!-- / Settings Drawer Content -->

	</a-drawer>
	<!-- / Settings Drawer -->

</template>

<script>
	import 'vue-github-buttons/dist/vue-github-buttons.css'; // Stylesheet
	import VueGitHubButtons from 'vue-github-buttons';
	import Vue from 'vue';
	Vue.use(VueGitHubButtons, { useCache: true });

	export default ({
		props: {
			// Settings drawer visiblility status.
			showSettingsDrawer: {
				type: Boolean,
				default: false,
			},
			
			// Main sidebar color.
			sidebarColor: {
				type: String,
				default: "primary",
			},
			
			// Main sidebar theme : light, white, dark.
			sidebarTheme: {
				type: String,
				default: "light",
			},

			// Header fixed status.
			navbarFixed: {
				type: Boolean,
				default: false,
			},

			// Drawer direction.
			rtl: {
				type: Boolean,
				default: false,
			},
		},
		data() {
			return {
				// The wrapper element to attach dropdowns to.
				wrapper: document.body,
				
				// Main sidebar color.
				sidebarColorModel: this.sidebarColor,
				
				// Main sidebar theme : light, white, dark.
				sidebarThemeModel: this.sidebarTheme,

				// Header fixed status.
				navbarFixedModel: this.navbarFixed,
			}
		},
		mounted: function(){
			// Set the wrapper to the proper element, layout wrapper.
			this.wrapper = document.getElementById('layout-dashboard') ;
		},
	})

</script>

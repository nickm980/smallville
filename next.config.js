const isProd = process.env.NODE_ENV === 'production'
console.log(isProd)
const withNextra = require('nextra')({
  theme: 'nextra-theme-docs',
  themeConfig: './theme.config.tsx'
})

module.exports = {
  ...withNextra(),
  images: {
    unoptimized: true,
  },
  assetPrefix: isProd ? '/smallville/' : '',
};

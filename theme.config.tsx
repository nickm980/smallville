import React from 'react'
import { DocsThemeConfig } from 'nextra-theme-docs'

const config: DocsThemeConfig = {
  logo: <span>Smallville</span>,
  project: {
    link: 'https://github.com/nickm980/smallville',
  },
  chat: {
    link: 'https://discord.com/invite/APVSw2DrCX',
  },
  docsRepositoryBase: 'https://github.com/nickm980/smallville/tree/docs',
  footer: {
    text: 'Smallville',
  },
  banner: {
    key: '2.0-release',
    text: (
      <a href="https://github.com/nickm980/smallville" target="_blank">
        🎉 Smallville v2.0.0 is in progress. Read more →
      </a>
    )
  },
}

export default config

import React from 'react'
import { useRouter } from 'next/router'
import { type DocsThemeConfig, useConfig } from 'nextra-theme-docs';

const config: DocsThemeConfig = {
  logo: <span className="logo--small"><img width={30} src="/smallville/logo.png"></img>Smallville</span>,
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
  useNextSeoProps() {
    const { asPath } = useRouter();

    const shared = {
      openGraph: {
        images: [
          {
            url: 'https://nickm980.github.io/smallville/banner.png',
            width: 1328,
            height: 345,
            alt: 'Smallville banner',
            type: 'image/png',
          },
        ],
      },
    };

    if (['/', '/docs'].includes(asPath)) {
      return { ...shared, titleTemplate: 'Smallville' };
    }

    return { ...shared, titleTemplate: `%s | Smallville` };
  },
  head: () => {
    const { asPath } = useRouter();
    const { frontMatter } = useConfig();
    const ogConfig = {
      title: 'Smallville',
      description: 'Generative agents for video games and simulations',
      author: {
        twitter: 'nickm980',
      },
      favicon: '/favicon.svg',
    };
    const favicon = String(ogConfig.favicon);
    const description = String(frontMatter.description || ogConfig.description);
    const canonical = new URL(asPath, 'https://nickm980.github.io/smallville').toString();

    return (
      <>
        <meta property="og:url" content={canonical} />
        <link rel="canonical" href={canonical} />

        <meta name="description" content={description} />
        <meta property="og:description" content={description} />
        <meta name="twitter:site" content={`@${ogConfig.author.twitter}`} />
        <meta name="twitter:creator" content={`@${ogConfig.author.twitter}`} />
        <meta name="twitter:card" content="summary_large_image" />
        <meta name="twitter:image" content="https://million.dev/banner.png" />

        <link rel="shortcut icon" href={favicon} type="image/svg+xml" />
        <link rel="apple-touch-icon" href={favicon} type="image/svg+xml" />
        <meta name="apple-mobile-web-app-title" content={ogConfig.title} />
      </>
    );
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

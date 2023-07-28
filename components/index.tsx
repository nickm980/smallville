function HomePage() {
  return (
    <div>
         <div className="content">
      <div className="hero">
        <div className="circle"></div>
        <div className="hero--content">
          <p className="text label small">Smallville</p>
          <p>Generative Agents For Video Games And Simulations</p>
          <div className="hero--section-buttons">
            <a
              href="/smallville/docs"
            >
              <button className="button primary gradient">
                <i className="fa-solid fa-download"></i> Get Started
              </button>
            </a>
                       <a
              href="https://github.com/nickm980/smallville/releases/download/v1.2.0/smallville-v1.2.0.zip"
            >
              <button className="button outline">
                <i className="fa-solid fa-download"></i> Download v1.2.0
              </button>
            </a>
          </div>
        </div>
      </div>

      <div className="section mt-extra">
        <div className="section--inner">
          <div className="cards">
            <div className="card">
            <p className="text">
                <b>Video Games. </b>
                Develop dynamic and realistic NPC characters
              </p>
            </div>
            <div className="card gradient-border">
            <p className="text">
                <b>Research Simulations. </b>
                 Customize simulations for research without needing to code
              </p>
            </div>
            <div className="card">
            <p className="text">
                <b>Social Robots. </b>
                Create believable social robots for the real world.
              </p>
            </div>
          </div>
        </div>
      </div>
      <div className="section full">
        <div className="section--inner">
          <div className="flex left">
            <div className="flex-image">
              <div className="img__gradient">
                <img className="large rounded wide" src="/smallville/config.png" />
              </div>
            </div>
            <div className="flex-2">
              <h2 className="title">Highly Configurable</h2>
              <p className="text">
                Fine tune configurable controls such as reflection
                weight cutoffs and prompts
              </p>
            </div>
          </div>
        </div>
      </div>
      <div className="section full">
        <div className="section--inner">
          <div className="flex right">
            <div className="flex-2">
              <h2 className="title">Programming Clients</h2>
              <p className="text">
                No programming is needed to configure simulations for research
                purposes, but the clients allow for creating custom simulations
                and for adding agents to video games.
              </p>
              <br />
              <i
                >The Java client will get priority updates so the JavaScript
                client may be outdated.</i
              >
            </div>
            <div className="flex-image">
              <div className="img__gradient">
                <img className="large rounded wide" src="/smallville/javaclient.png" />
              </div>
            </div>
          </div>
        </div>
      </div>
      <div className="section">
        <div className="section--inner">
          <section
            className="faq container"
            aria-label="Frequently Asked Questions"
          >
            <header className="faq_header">
              <h2 className="faq_header-title">Frequently Asked Questions</h2>
            </header>
            <div className="faq__body">
              <details aria-expanded="true" className="faq__panel" open>
                <summary className="faq__label">
                  Can Smallville run locally?
                </summary>
                <div className="faq__panel-body">
                  <p className="text faq__panel-answer">
                    Yes. Running locally can be done by running
                    <a
                      target="_blank"
                      href="https://github.com/go-skynet/LocalAI"
                      >LocalAI</a
                    >
                    and changing the url and model in the config.yaml settings.
                    However local supported is untested and you should test
                    before deploying to production.
                  </p>
                </div>
              </details>
              <details aria-expanded="false" className="faq__panel">
                <summary className="faq__label">
                  What happens if your service goes down?
                </summary>
                <div className="faq__panel-body">
                  <p className="text faq__panel-answer">
                    Smallville is open source and able to be self hosted. In the
                    small chance that our service stopped or repository taken
                    down you will still be able to self host the server on your
                    own machines.
                  </p>
                </div>
              </details>
              <details aria-expanded="false" className="faq__panel">
                <summary className="faq__label">How do I get help?</summary>
                <div className="faq__panel-body">
                  <p className="text faq__panel-answer">
                    You can get help setting up and by visiting our community
                    <a href="https://discord.gg/invite/APVSw2DrCX"
                      >discord server</a
                    >.
                  </p>
                </div>
              </details>
            </div>
          </section>
        </div>
      </div>
    </div>
    </div>
  )
}

export default function MyApp() {
  return <HomePage />
}

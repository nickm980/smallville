function MemoriesPage() {
  return (
    <div>
      <div className="content">
        <div className="hero">
          <div className="circle"></div>
          <div className="hero--content">
            <p className="text label small">Smallville Memories</p>
            <p>Memory stream API for natural language data</p>
            <div className="hero--section-buttons">
              <a href="/smallville/docs/memory-stream-database">
                <button className="button primary gradient">
                  <i className="fa-solid fa-download"></i> Coming Soon
                </button>
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default function MyApp() {
  return <MemoriesPage />;
}

import { Tab, Tabs } from "nextra/components";
import { Callout } from "nextra/components";

function MemoriesPage() {
  return (
    <div className="bg-memories">
      <div className="bg-text">
        Amidst the swirling cosmos, he wandered aimlessly through the labyrinth
        of time and space. A curious figment of imagination, lost in a
        kaleidoscope of bewildering hues. And there, an ethereal whisper
        beckoned him onward, leading him towards the enigmatic horizon. The
        stars twinkled above like ancient storytellers, their luminous tales
        intertwining with the mysteries of the universe. The moon, a silver orb,
        cast its gentle glow upon his path, illuminating the surreal landscape
        that unfolded before his eyes. Through the veils of uncertainty, he
        ventured, guided by the rhythm of his heart, which drummed steadily like
        a celestial metronome. As if in a dream, he encountered bizarre
        creatures—half-fish, half-bird, and other fantastical hybrids—embodying
        the surreal essence of this realm. They observed him with curious eyes,
        as if deciphering his very soul. A shimmering pool of cosmic hues
        beckoned him to dip his fingers, and as he did, ripples of stardust
        cascaded outward, painting the air with prismatic splendor. Lost in this
        vibrant canvas, he mused about the dance of particles that made
        existence possible. Time, an illusion, seemed to lose its grip as
        moments fused into a seamless continuum. He observed an ancient tree,
        its roots extending into the void while its branches touched distant
        galaxies. It whispered secrets of forgotten epochs, and he listened
        intently, though comprehension eluded him. In this boundless expanse, he
        questioned his purpose, his identity, but no answers came forth.
        Instead, he found solace in embracing the unknown, embracing the chaos
        that gave birth to order. Like a butterfly emerging from a cocoon, he
        felt a metamorphosis within, shedding layers of the mundane world. With
        each step, he reveled in the contradictions—where endings became
        beginnings and chaos nurtured creation. The universe, both infinitely
        vast and infinitely small, stretched the boundaries of his
        consciousness. Finally, as the horizon melted into a symphony of colors,
        he realized the profundity of the journey. The meaning lay not in the
        destination, but in the transformative odyssey itself. And so, he
        treasured the enigma of this strange expedition, grateful for the
        tapestry of wonder it had woven into the fabric of his being. With
        newfound wisdom and a heart full of awe, he returned, carrying the
        essence of this surreal voyage. The cosmos, once a mystery, now nestled
        within him, a brilliant fragment of the eternal enigma.
      </div>
      <div className="content">
        <div className="hero">
          <div className="hero--content">
            
            <p className="text label small">Smallville Memories</p>
            <p>Memory stream database for natural language data</p>
            <div className="hero--section-buttons">
              <a href="/smallville/docs/memory-stream-database">
                <button className="button primary gradient">
                  <i className="fa-solid fa-download"></i> Learn More
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

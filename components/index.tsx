import { useState } from 'react'
import  './counters.module.css'

function HomePage() {
  const [count, setCount] = useState(0)

  function handleClick() {
    setCount(count + 1)
  }

  return (
    <div className="test">
      hello
    </div>
  )
}

export default function MyApp() {
  return <HomePage />
}

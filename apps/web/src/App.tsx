import { useCallback, useEffect, useState } from 'react'

type Component = { name: string; status: string; details?: unknown; error?: string }
type StatusResponse = {
  project: string
  milestone: string
  version: string
  status: string
  components: Record<string, Component>
}

const API_URL = import.meta.env.VITE_GAME_API_URL ?? 'http://localhost:8080'

export default function App() {
  const [status, setStatus] = useState<StatusResponse | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  const refresh = useCallback(async () => {
    setLoading(true)
    try {
      const response = await fetch(`${API_URL}/api/system/status`)
      if (!response.ok) throw new Error(`Game server returned ${response.status}`)
      setStatus(await response.json())
      setError(null)
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : 'Unable to load platform status')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { void refresh() }, [refresh])

  return (
    <main>
      <header>
        <p className="eyebrow">AI-FIRST EPISODIC STARSHIP COMMAND</p>
        <h1>Beyond the Signal</h1>
        <p className="subtitle">Milestone 1 establishes the deployable platform foundation for the universe, its crew, and the stories ahead.</p>
      </header>

      <section className="summary">
        <div><span>Platform</span><strong className={`status ${status?.status?.toLowerCase()}`}>{status?.status ?? (loading ? 'CHECKING' : 'UNKNOWN')}</strong></div>
        <div><span>Release</span><strong>{status?.version ?? '0.1.0'}</strong></div>
        <button type="button" onClick={() => void refresh()} disabled={loading}>{loading ? 'Checking…' : 'Refresh status'}</button>
      </section>

      {error && <p className="error">{error}</p>}

      <section className="grid" aria-live="polite">
        {status && Object.entries(status.components).map(([key, component]) => (
          <article key={key}>
            <div className="component-title"><h2>{formatName(key)}</h2><span className={`badge ${component.status.toLowerCase()}`}>{component.status}</span></div>
            <p>{description(key)}</p>
            {component.error && <code>{component.error}</code>}
          </article>
        ))}
        {!status && !error && Array.from({ length: 4 }).map((_, i) => <article className="skeleton" key={i} />)}
      </section>

      <footer>Original science-fiction universe • Server-authoritative foundation • AI isolated from game authority</footer>
    </main>
  )
}

function formatName(value: string) {
  return value.replace(/([A-Z])/g, ' $1').replace(/^./, char => char.toUpperCase())
}

function description(key: string) {
  const descriptions: Record<string, string> = {
    gameServer: 'Authoritative Java 21 and Vert.x platform.',
    database: 'PostgreSQL persistence and Flyway schema history.',
    redis: 'Messaging and temporary coordination foundation.',
    aiService: 'Python FastAPI boundary for future crew intelligence.',
  }
  return descriptions[key] ?? 'Platform component'
}

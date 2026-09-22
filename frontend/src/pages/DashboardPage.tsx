import { useEffect, useState } from 'react'
import { api, ApiError } from '../api'
import { useAuth } from '../auth'

export function DashboardPage() {
  const { token } = useAuth()
  const [data, setData] = useState<Record<string, number> | null>(null)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!token) return
    api.getDashboard(token).then(setData).catch((err) => setError(err instanceof ApiError ? err.message : 'Failed to load dashboard'))
  }, [token])

  return (
    <section>
      <h2>Dashboard</h2>
      {error && <p className="error">{error}</p>}
      {!data && !error && <p>Loading dashboard...</p>}
      {data && (
        <div className="grid">
          {Object.entries(data).map(([key, value]) => (
            <article key={key} className="card">
              <h3>{key}</h3>
              <p>{value}</p>
            </article>
          ))}
        </div>
      )}
    </section>
  )
}

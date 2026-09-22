import { FormEvent, useState } from 'react'
import { api, ApiError } from '../api'
import { useAuth } from '../auth'
import type { Notification } from '../types'

export function NotificationsPage() {
  const { token } = useAuth()
  const [userId, setUserId] = useState('')
  const [notifications, setNotifications] = useState<Notification[]>([])
  const [error, setError] = useState('')

  const load = async (event: FormEvent) => {
    event.preventDefault()
    if (!token) return
    try {
      setNotifications(await api.getNotifications(token, userId))
      setError('')
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed to load notifications')
    }
  }

  return (
    <section>
      <h2>Notifications</h2>
      <form className="row" onSubmit={load}>
        <input placeholder="User ID" value={userId} onChange={(e) => setUserId(e.target.value)} required />
        <button type="submit">Load</button>
      </form>
      {error && <p className="error">{error}</p>}
      <div className="grid">
        {notifications.map((note) => (
          <article key={note.id} className="card">
            <h3>{note.title}</h3>
            <p>{note.message}</p>
            <p>{note.notificationStatus}</p>
          </article>
        ))}
      </div>
    </section>
  )
}

import { FormEvent, useState } from 'react'
import { api, ApiError } from '../api'
import { useAuth } from '../auth'
import type { Customer } from '../types'

export function ProfilePage() {
  const { token } = useAuth()
  const [customerId, setCustomerId] = useState('')
  const [profile, setProfile] = useState<Customer | null>(null)
  const [error, setError] = useState('')

  const load = async (event: FormEvent) => {
    event.preventDefault()
    if (!token) return
    try {
      setProfile(await api.getCustomer(token, customerId))
      setError('')
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed to load profile')
    }
  }

  return (
    <section>
      <h2>Customer Profile</h2>
      <form className="row" onSubmit={load}>
        <input placeholder="Customer ID" value={customerId} onChange={(e) => setCustomerId(e.target.value)} required />
        <button type="submit">Load profile</button>
      </form>
      {error && <p className="error">{error}</p>}
      {profile && (
        <article className="card">
          <h3>
            {profile.firstName} {profile.lastName}
          </h3>
          <p>{profile.email}</p>
          <p>{profile.phone}</p>
          <p>
            {profile.address}, {profile.city}, {profile.state}, {profile.country}
          </p>
        </article>
      )}
    </section>
  )
}

import { useEffect, useState } from 'react'
import { api, ApiError } from '../api'
import { useAuth } from '../auth'
import type { Campaign } from '../types'

export function CampaignsPage() {
  const { token } = useAuth()
  const [campaigns, setCampaigns] = useState<Campaign[]>([])
  const [error, setError] = useState('')

  useEffect(() => {
    if (!token) return
    api.getCampaigns(token)
      .then((page) => setCampaigns(page.content))
      .catch((err) => setError(err instanceof ApiError ? err.message : 'Failed to load campaigns'))
  }, [token])

  return (
    <section>
      <h2>Campaign Management</h2>
      {error && <p className="error">{error}</p>}
      <div className="grid">
        {campaigns.map((campaign) => (
          <article key={campaign.id} className="card">
            <h3>{campaign.name}</h3>
            <p>{campaign.campaignType}</p>
            <p>Status: {campaign.status}</p>
          </article>
        ))}
      </div>
    </section>
  )
}

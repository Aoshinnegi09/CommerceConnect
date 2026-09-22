import { useEffect, useState } from 'react'
import { api, ApiError } from '../api'
import { useAuth } from '../auth'
import type { Promotion } from '../types'

export function PromotionsPage() {
  const { token } = useAuth()
  const [promotions, setPromotions] = useState<Promotion[]>([])
  const [error, setError] = useState('')

  useEffect(() => {
    if (!token) return
    api.getPromotions(token)
      .then((page) => setPromotions(page.content))
      .catch((err) => setError(err instanceof ApiError ? err.message : 'Failed to load promotions'))
  }, [token])

  return (
    <section>
      <h2>Promotions</h2>
      {error && <p className="error">{error}</p>}
      <div className="grid">
        {promotions.map((promotion) => (
          <article key={promotion.id} className="card">
            <h3>{promotion.name}</h3>
            <p>{promotion.discountType} {promotion.discountValue}</p>
            <p>{promotion.active ? 'Active' : 'Inactive'}</p>
          </article>
        ))}
      </div>
    </section>
  )
}

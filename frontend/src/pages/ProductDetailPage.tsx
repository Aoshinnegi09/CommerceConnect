import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { api, ApiError } from '../api'
import { useAuth } from '../auth'
import type { Product } from '../types'

export function ProductDetailPage() {
  const { token } = useAuth()
  const { id = '' } = useParams()
  const [product, setProduct] = useState<Product | null>(null)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!token) return
    api.getProductById(token, id).then(setProduct).catch((err) => setError(err instanceof ApiError ? err.message : 'Failed to load product'))
  }, [id, token])

  if (error) return <p className="error">{error}</p>
  if (!product) return <p>Loading product...</p>

  return (
    <section className="card">
      <h2>{product.name}</h2>
      <p>{product.description}</p>
      <p>SKU: {product.sku}</p>
      <p>Price: ₹{product.price}</p>
    </section>
  )
}

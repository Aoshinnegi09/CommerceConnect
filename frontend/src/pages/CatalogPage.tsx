import { FormEvent, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, ApiError } from '../api'
import { useAuth } from '../auth'
import type { Product } from '../types'

export function CatalogPage() {
  const { token } = useAuth()
  const [products, setProducts] = useState<Product[]>([])
  const [search, setSearch] = useState('')
  const [error, setError] = useState('')

  const load = async (term = '') => {
    if (!token) return
    try {
      const page = await api.getProducts(token, term)
      setProducts(page.content)
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed to load products')
    }
  }

  useEffect(() => {
    load()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token])

  const onSearch = (e: FormEvent) => {
    e.preventDefault()
    void load(search)
  }

  return (
    <section>
      <h2>Product Catalog</h2>
      <form onSubmit={onSearch} className="row">
        <input placeholder="Search by name or SKU" value={search} onChange={(e) => setSearch(e.target.value)} />
        <button type="submit">Search</button>
      </form>
      {error && <p className="error">{error}</p>}
      {products.length === 0 && !error && <p>No products found.</p>}
      <div className="grid">
        {products.map((product) => (
          <article key={product.id} className="card">
            <h3>{product.name}</h3>
            <p>{product.description}</p>
            <p>₹{product.price}</p>
            <Link to={`/catalog/${product.id}`}>View details</Link>
          </article>
        ))}
      </div>
    </section>
  )
}

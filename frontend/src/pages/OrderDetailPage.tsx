import { useParams } from 'react-router-dom'

export function OrderDetailPage() {
  const { id } = useParams()
  return (
    <section className="card">
      <h2>Order Details</h2>
      <p>Use /api/v1/orders/{id} for backend details. Route selected: {id}</p>
    </section>
  )
}

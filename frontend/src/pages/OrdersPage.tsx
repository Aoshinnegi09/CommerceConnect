import { FormEvent, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, ApiError } from '../api'
import { useAuth } from '../auth'
import type { Order } from '../types'

export function OrdersPage() {
  const { token } = useAuth()
  const [customerId, setCustomerId] = useState('')
  const [orders, setOrders] = useState<Order[]>([])
  const [error, setError] = useState('')

  const fetchOrders = async (event: FormEvent) => {
    event.preventDefault()
    if (!token) return
    try {
      const page = await api.getOrdersByCustomer(token, customerId)
      setOrders(page.content)
      setError('')
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed to load orders')
    }
  }

  return (
    <section>
      <h2>Order History</h2>
      <form className="row" onSubmit={fetchOrders}>
        <input placeholder="Customer ID" value={customerId} onChange={(e) => setCustomerId(e.target.value)} required />
        <button type="submit">Load orders</button>
      </form>
      {error && <p className="error">{error}</p>}
      <div className="grid">
        {orders.map((order) => (
          <article className="card" key={order.id}>
            <h3>{order.orderNumber}</h3>
            <p>Status: {order.status}</p>
            <p>Total: ₹{order.totalAmount}</p>
            <Link to={`/orders/${order.id}`}>View details</Link>
          </article>
        ))}
      </div>
    </section>
  )
}

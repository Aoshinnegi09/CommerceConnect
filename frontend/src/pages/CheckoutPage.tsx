import { FormEvent, useState } from 'react'
import { api, ApiError } from '../api'
import { useAuth } from '../auth'

export function CheckoutPage() {
  const { token } = useAuth()
  const [customerId, setCustomerId] = useState('')
  const [productId, setProductId] = useState('')
  const [quantity, setQuantity] = useState(1)
  const [message, setMessage] = useState('')

  const submit = async (event: FormEvent) => {
    event.preventDefault()
    if (!token) return
    try {
      const order = await api.placeOrder(token, {
        customerId: Number(customerId),
        items: [{ productId: Number(productId), quantity }],
      })
      setMessage(`Order ${order.orderNumber} placed with status ${order.status}`)
    } catch (err) {
      setMessage(err instanceof ApiError ? err.message : 'Checkout failed')
    }
  }

  return (
    <section>
      <h2>Checkout</h2>
      <form onSubmit={submit} className="card">
        <input placeholder="Customer ID" value={customerId} onChange={(e) => setCustomerId(e.target.value)} required />
        <input placeholder="Product ID" value={productId} onChange={(e) => setProductId(e.target.value)} required />
        <input type="number" min={1} value={quantity} onChange={(e) => setQuantity(Number(e.target.value))} required />
        <button type="submit">Place order</button>
      </form>
      {message && <p>{message}</p>}
    </section>
  )
}

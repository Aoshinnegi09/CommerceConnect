import type { AuthResponse, Campaign, Customer, Notification, Order, Product, Promotion } from './types'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1'

export class ApiError extends Error {
  status: number
  details: unknown

  constructor(message: string, status = 500, details: unknown = undefined) {
    super(message)
    this.status = status
    this.details = details
  }
}

async function request<T>(path: string, options: RequestInit = {}, token?: string): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `****** } : {}),
      ...(options.headers ?? {}),
    },
  })

  if (!response.ok) {
    let errorBody: any = null
    try {
      errorBody = await response.json()
    } catch {
      // ignore parsing failures
    }
    throw new ApiError(errorBody?.message ?? 'Request failed', response.status, errorBody)
  }

  if (response.status === 204) {
    return undefined as T
  }

  return response.json() as Promise<T>
}

export const api = {
  register: (payload: { firstName: string; lastName: string; email: string; password: string }) =>
    request<AuthResponse>('/auth/register', { method: 'POST', body: JSON.stringify(payload) }),

  login: (payload: { email: string; password: string }) =>
    request<AuthResponse>('/auth/login', { method: 'POST', body: JSON.stringify(payload) }),

  getProducts: (token: string, search = '') =>
    request<{ content: Product[] }>(`/products?search=${encodeURIComponent(search)}`, {}, token),

  getProductById: (token: string, id: string) => request<Product>(`/products/${id}`, {}, token),

  getDashboard: (token: string) => request<Record<string, number>>('/dashboard', {}, token),

  getCustomer: (token: string, id: string) => request<Customer>(`/customers/${id}`, {}, token),

  getOrdersByCustomer: (token: string, customerId: string) =>
    request<{ content: Order[] }>(`/customers/${customerId}/orders`, {}, token),

  placeOrder: (token: string, payload: { customerId: number; items: Array<{ productId: number; quantity: number }>; promotionId?: number }) =>
    request<Order>('/orders', { method: 'POST', body: JSON.stringify(payload) }, token),

  getPromotions: (token: string) => request<{ content: Promotion[] }>('/promotions', {}, token),

  getCampaigns: (token: string) => request<{ content: Campaign[] }>('/campaigns', {}, token),

  getNotifications: (token: string, userId: string) => request<Notification[]>(`/notifications/user/${userId}`, {}, token),
}

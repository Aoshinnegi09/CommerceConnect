export type Role = 'ADMIN' | 'CUSTOMER' | 'MARKETING_MANAGER'

export interface AuthResponse {
  token: string
  email: string
  firstName: string
  lastName: string
  role: Role
}

export interface Product {
  id: number
  name: string
  description: string
  sku: string
  price: number
  active: boolean
  category?: { id: number; name: string; description?: string } | null
}

export interface OrderItem {
  id: number
  productId: number
  productName: string
  quantity: number
  unitPrice: number
  totalPrice: number
}

export interface Order {
  id: number
  orderNumber: string
  customerId: number
  totalAmount: number
  discountAmount: number
  promotionId?: number
  status: string
  paymentStatus: string
  createdAt: string
  items: OrderItem[]
}

export interface Customer {
  id: number
  userId: number
  email: string
  firstName: string
  lastName: string
  phone?: string
  address?: string
  city?: string
  state?: string
  country?: string
}

export interface Promotion {
  id: number
  name: string
  description?: string
  discountType: 'PERCENTAGE' | 'FIXED'
  discountValue: number
  maxDiscountAmount?: number
  active: boolean
}

export interface Campaign {
  id: number
  name: string
  description?: string
  campaignType: string
  status: string
}

export interface Notification {
  id: number
  userId: number
  title: string
  message: string
  notificationType: string
  notificationStatus: string
  read: boolean
  createdAt: string
}

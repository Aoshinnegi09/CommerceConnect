import { createContext, useContext, useMemo, useState } from 'react'
import type { AuthResponse } from './types'

type AuthState = AuthResponse | null

type AuthContextValue = {
  auth: AuthState
  token: string | null
  login: (payload: AuthResponse) => void
  logout: () => void
}

const AUTH_KEY = 'commerceconnect_auth'
const AuthContext = createContext<AuthContextValue | undefined>(undefined)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [auth, setAuth] = useState<AuthState>(() => {
    const raw = localStorage.getItem(AUTH_KEY)
    return raw ? (JSON.parse(raw) as AuthResponse) : null
  })

  const value = useMemo<AuthContextValue>(
    () => ({
      auth,
      token: auth?.token ?? null,
      login: (payload) => {
        localStorage.setItem(AUTH_KEY, JSON.stringify(payload))
        setAuth(payload)
      },
      logout: () => {
        localStorage.removeItem(AUTH_KEY)
        setAuth(null)
      },
    }),
    [auth],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return context
}

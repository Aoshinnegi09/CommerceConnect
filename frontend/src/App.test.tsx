import { describe, expect, it } from 'vitest'
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { AuthProvider } from './auth'
import { ProtectedRoute } from './components/ProtectedRoute'

describe('ProtectedRoute', () => {
  it('redirects unauthenticated users to login', () => {
    render(
      <AuthProvider>
        <MemoryRouter initialEntries={['/dashboard']}>
          <ProtectedRoute />
        </MemoryRouter>
      </AuthProvider>,
    )

    expect(screen.queryByText(/dashboard/i)).toBeNull()
  })
})

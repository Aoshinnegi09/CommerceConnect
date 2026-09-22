import { Link, Outlet } from 'react-router-dom'
import { useAuth } from '../auth'

export function Layout() {
  const { auth, logout } = useAuth()
  const role = auth?.role

  return (
    <div className="app-shell">
      <header>
        <h1>CommerceConnect</h1>
        <nav>
          <Link to="/dashboard">Dashboard</Link>
          <Link to="/catalog">Catalog</Link>
          <Link to="/cart">Cart</Link>
          <Link to="/orders">Orders</Link>
          <Link to="/profile">Profile</Link>
          <Link to="/notifications">Notifications</Link>
          {(role === 'ADMIN' || role === 'MARKETING_MANAGER') && <Link to="/admin/catalog">Admin Catalog</Link>}
          {(role === 'ADMIN' || role === 'MARKETING_MANAGER') && <Link to="/admin/inventory">Admin Inventory</Link>}
          {(role === 'ADMIN' || role === 'MARKETING_MANAGER') && <Link to="/promotions">Promotions</Link>}
          {(role === 'ADMIN' || role === 'MARKETING_MANAGER') && <Link to="/campaigns">Campaigns</Link>}
        </nav>
        <button onClick={logout}>Logout</button>
      </header>
      <main>
        <Outlet />
      </main>
    </div>
  )
}

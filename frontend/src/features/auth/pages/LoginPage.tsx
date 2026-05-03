import { useState } from 'react'
import { Link } from 'react-router-dom'
import { AuthForm } from '../components/AuthForm'
import { useAuth } from '../hooks/useAuth'

export function LoginPage() {
  const { login } = useAuth()
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  return (
    <main className="mx-auto flex min-h-screen w-full max-w-md flex-col justify-center px-4 py-8">
      <p className="text-xs font-semibold uppercase tracking-[0.16em] text-teal-700">
        TranquiloOS
      </p>
      <h1 className="mt-2 text-2xl font-semibold text-slate-950">Log in</h1>
      <p className="mt-2 text-sm leading-6 text-slate-600">
        Access your private independence dashboard.
      </p>
      <section className="mt-6 rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
        <AuthForm
          mode="login"
          isSubmitting={isSubmitting}
          error={error}
          onSubmit={async (values) => {
            setIsSubmitting(true)
            setError(null)
            try {
              await login({ email: values.email, password: values.password })
            } catch {
              setError('Invalid email or password.')
            } finally {
              setIsSubmitting(false)
            }
          }}
        />
      </section>
      <Link className="mt-4 text-sm font-semibold text-teal-700" to="/register">
        Create an account
      </Link>
    </main>
  )
}

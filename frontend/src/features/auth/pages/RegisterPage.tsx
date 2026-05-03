import { useState } from 'react'
import { Link } from 'react-router-dom'
import { AuthForm } from '../components/AuthForm'
import { useAuth } from '../hooks/useAuth'

export function RegisterPage() {
  const { register } = useAuth()
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  return (
    <main className="mx-auto flex min-h-screen w-full max-w-md flex-col justify-center px-4 py-8">
      <p className="text-xs font-semibold uppercase tracking-[0.16em] text-teal-700">
        TranquiloOS
      </p>
      <h1 className="mt-2 text-2xl font-semibold text-slate-950">
        Create account
      </h1>
      <p className="mt-2 text-sm leading-6 text-slate-600">
        Start with a private local user. No social login, no extra ceremony.
      </p>
      <section className="mt-6 rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
        <AuthForm
          mode="register"
          isSubmitting={isSubmitting}
          error={error}
          onSubmit={async (values) => {
            setIsSubmitting(true)
            setError(null)
            try {
              await register(values)
            } catch {
              setError('Could not create the account. Check the email and password.')
            } finally {
              setIsSubmitting(false)
            }
          }}
        />
      </section>
      <Link className="mt-4 text-sm font-semibold text-teal-700" to="/login">
        Already have an account? Log in
      </Link>
    </main>
  )
}

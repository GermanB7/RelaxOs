import { useState } from 'react'

type AuthFormProps = {
  mode: 'login' | 'register'
  isSubmitting: boolean
  error?: string | null
  onSubmit: (values: {
    email: string
    password: string
    displayName?: string
    city?: string
    currency?: string
  }) => Promise<void>
}

export function AuthForm({ mode, isSubmitting, error, onSubmit }: AuthFormProps) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [displayName, setDisplayName] = useState('')
  const [city, setCity] = useState('Bogota')
  const [currency, setCurrency] = useState('COP')

  return (
    <form
      className="grid gap-4"
      onSubmit={(event) => {
        event.preventDefault()
        void onSubmit({ email, password, displayName, city, currency })
      }}
    >
      {mode === 'register' && (
        <>
          <label className="grid gap-1 text-sm font-medium text-slate-700">
            Display name
            <input
              className="rounded-md border border-slate-300 px-3 py-2"
              value={displayName}
              maxLength={120}
              onChange={(event) => setDisplayName(event.target.value)}
            />
          </label>
          <label className="grid gap-1 text-sm font-medium text-slate-700">
            City
            <input
              className="rounded-md border border-slate-300 px-3 py-2"
              value={city}
              maxLength={120}
              onChange={(event) => setCity(event.target.value)}
            />
          </label>
          <label className="grid gap-1 text-sm font-medium text-slate-700">
            Currency
            <input
              className="rounded-md border border-slate-300 px-3 py-2"
              value={currency}
              maxLength={10}
              onChange={(event) => setCurrency(event.target.value)}
            />
          </label>
        </>
      )}

      <label className="grid gap-1 text-sm font-medium text-slate-700">
        Email
        <input
          className="rounded-md border border-slate-300 px-3 py-2"
          type="email"
          value={email}
          required
          onChange={(event) => setEmail(event.target.value)}
        />
      </label>
      <label className="grid gap-1 text-sm font-medium text-slate-700">
        Password
        <input
          className="rounded-md border border-slate-300 px-3 py-2"
          type="password"
          value={password}
          required
          minLength={8}
          onChange={(event) => setPassword(event.target.value)}
        />
      </label>

      {error && <p className="text-sm font-medium text-red-700">{error}</p>}

      <button
        type="submit"
        disabled={isSubmitting}
        className="rounded-md bg-teal-700 px-4 py-2 text-sm font-semibold text-white disabled:cursor-not-allowed disabled:opacity-60"
      >
        {isSubmitting
          ? 'Working...'
          : mode === 'login'
            ? 'Log in'
            : 'Create account'}
      </button>
    </form>
  )
}

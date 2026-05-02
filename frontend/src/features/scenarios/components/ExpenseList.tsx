import { useState } from 'react'
import type {
  ExpenseCategory,
  ScenarioExpense,
} from '../../../shared/types/domain'
import type { ExpensePayload } from '../api/scenariosApi'
import { ExpenseForm } from './ExpenseForm'

type ExpenseListProps = {
  expenses: ScenarioExpense[]
  categories: ExpenseCategory[]
  onUpdate: (expenseId: number, payload: ExpensePayload) => Promise<void>
  onDelete: (expenseId: number) => void
}

export function ExpenseList({
  expenses,
  categories,
  onUpdate,
  onDelete,
}: ExpenseListProps) {
  const [editingExpenseId, setEditingExpenseId] = useState<number | null>(null)

  if (expenses.length === 0) {
    return (
      <section className="rounded-lg border border-dashed border-slate-300 bg-white p-5">
        <h3 className="text-base font-semibold text-slate-950">
          No expenses yet
        </h3>
        <p className="mt-2 text-sm leading-6 text-slate-600">
          Add the first recurring expense to let the backend calculate the
          monthly summary.
        </p>
      </section>
    )
  }

  return (
    <div className="grid gap-3">
      {expenses.map((expense) =>
        editingExpenseId === expense.id ? (
          <ExpenseForm
            key={expense.id}
            categories={categories}
            initialValues={expense}
            submitLabel="Update expense"
            onSubmit={async (payload) => {
              await onUpdate(expense.id, payload)
              setEditingExpenseId(null)
            }}
            onCancel={() => setEditingExpenseId(null)}
          />
        ) : (
          <article
            key={expense.id}
            className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm"
          >
            <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <h3 className="text-base font-semibold text-slate-950">
                  {expense.name}
                </h3>
                <p className="mt-1 text-sm text-slate-600">
                  {expense.categoryName} · {expense.frequency} · monthly{' '}
                  {expense.monthlyEquivalent.toLocaleString()}
                </p>
              </div>
              <div className="flex gap-2">
                <button
                  type="button"
                  onClick={() => setEditingExpenseId(expense.id)}
                  className="rounded-md border border-slate-300 px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-50"
                >
                  Edit
                </button>
                <button
                  type="button"
                  onClick={() => onDelete(expense.id)}
                  className="rounded-md bg-red-700 px-3 py-2 text-sm font-semibold text-white hover:bg-red-800"
                >
                  Delete
                </button>
              </div>
            </div>
          </article>
        ),
      )}
    </div>
  )
}

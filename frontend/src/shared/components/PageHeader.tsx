type PageHeaderProps = {
  title: string
  description: string
}

export function PageHeader({ title, description }: PageHeaderProps) {
  return (
    <header className="max-w-3xl">
      <h2 className="text-2xl font-semibold text-slate-950 sm:text-3xl">
        {title}
      </h2>
      <p className="mt-2 text-sm leading-6 text-slate-600 sm:text-base">
        {description}
      </p>
    </header>
  )
}

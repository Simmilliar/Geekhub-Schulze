export class Page<T> {
  page: number
  perPage: number
  itemsTotal: number
  pagesTotal: number
  items: T[]
}

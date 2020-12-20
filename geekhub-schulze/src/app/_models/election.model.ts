export class ElectionModel {
  author: string
  shareId: string
  title: string
  description: string
  closed: boolean
  candidates: {
    name: string
    description: string
    score: number
  }[]
}

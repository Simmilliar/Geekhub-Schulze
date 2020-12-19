export class NewElectionForm {
  title: string
  description: string
  candidates: {
    name: string,
    description: string
  }[]
}

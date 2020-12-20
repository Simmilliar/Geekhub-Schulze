export class VoteToSubmitModel {
  voteId: string
  leftCandidate: {
    name: string,
    description: string,
    score: string
  }
  rightCandidate: {
    name: string,
    description: string,
    score: string
  }
}

CREATE TABLE votes (
  id TEXT PRIMARY KEY,
  "user" INTEGER NOT NULL REFERENCES users(id),
  pair INTEGER NOT NULL REFERENCES pairs(id),
  vote_result TEXT NOT NULL DEFAULT ('NOT_SUBMITTED')
                   CHECK (vote_result = 'NOT_SUBMITTED' OR vote_result = 'LEFT_IS_BETTER' OR vote_result = 'RIGHT_IS_BETTER')
);
CREATE TABLE elections (
  id SERIAL PRIMARY KEY NOT NULL,
  author INTEGER NOT NULL REFERENCES users(id),
  title TEXT NOT NULL,
  description TEXT NOT NULL,
  is_closed BOOLEAN NOT NULL,
  share_id TEXT UNIQUE NOT NULL
)
CREATE TABLE candidates (
  id SERIAL NOT NULL PRIMARY KEY,
  election INTEGER NOT NULL REFERENCES elections(id),
  name TEXT NOT NULL,
  description TEXT NOT NULL,
  UNIQUE (election, name)
);

CREATE TABLE pairs (
  id SERIAL NOT NULL PRIMARY KEY,
  election INTEGER NOT NULL REFERENCES elections(id),
  left_candidate INTEGER NOT NULL REFERENCES candidates(id),
  right_candidate INTEGER NOT NULL REFERENCES candidates(id) CHECK (left_candidate > right_candidate),
  UNIQUE (left_candidate, right_candidate)
);
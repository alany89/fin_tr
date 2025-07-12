CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS transaction (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    opisaniya VARCHAR(100) NOT NULL,
    sum INTEGER NOT NULL,
    action VARCHAR(10) NOT NULL CHECK (action IN ('income', 'expense')),
    balance_of_traty INTEGER,
    balance_of_raise INTEGER,
    category VARCHAR(20),
    user_id BIGINT NOT NULL REFERENCES users(id)
);
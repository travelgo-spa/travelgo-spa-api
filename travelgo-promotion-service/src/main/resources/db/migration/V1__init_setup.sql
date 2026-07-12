CREATE TABLE promotion (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    code TEXT NOT NULL UNIQUE,
    discount_percentage REAL NOT NULL,
    expiration_date TEXT NOT NULL
);
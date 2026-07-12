CREATE TABLE notification (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    message TEXT NOT NULL,
    sent_at TEXT NOT NULL,
    read_status INTEGER NOT NULL DEFAULT 0
);
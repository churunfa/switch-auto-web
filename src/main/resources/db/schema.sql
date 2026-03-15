CREATE TABLE IF NOT EXISTS model_config (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    model_id VARCHAR(255) NOT NULL,
    model_name VARCHAR(255) NOT NULL,
    api_key VARCHAR(500) NOT NULL,
    base_url VARCHAR(500) NOT NULL,
    model_type VARCHAR(100),
    status INTEGER DEFAULT 1,
    create_time INTEGER NOT NULL,
    update_time INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    username  TEXT    NOT NULL UNIQUE,
    email     TEXT    NOT NULL UNIQUE,
    full_name TEXT    NOT NULL
);


INSERT INTO users (username, email, full_name) VALUES
    ('italo_camp',    'italo@travelgo.cl',    'Italo Campodónico'),
    ('robert_prof',   'roberto@duoc.cl',      'Roberto Profesor'),
    ('m_campodonico', 'marcella@gmail.com',   'Marcella Campodónico'),
    ('user_test',     'test@travelgo.cl',     'Usuario de Pruebas'),
    ('admin_travel',  'admin@travelgo.cl',    'Administrador Global');

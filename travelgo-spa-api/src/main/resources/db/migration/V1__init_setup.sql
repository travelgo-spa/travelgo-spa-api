-- ============================================================
-- V1__init_setup.sql
-- Migración inicial: esquema + datos semilla
-- ============================================================

-- Tabla de paquetes de viaje
CREATE TABLE IF NOT EXISTS travel_package (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    name           TEXT    NOT NULL,
    description    TEXT,
    price          REAL    NOT NULL CHECK (price >= 0),
    duration_days  INTEGER NOT NULL CHECK (duration_days > 0)
);

-- Tabla de destinos (colección de elementos del paquete)
CREATE TABLE IF NOT EXISTS travel_package_destinations (
    travel_package_id INTEGER NOT NULL,
    destinations      TEXT    NOT NULL,
    FOREIGN KEY (travel_package_id) REFERENCES travel_package(id)
);

-- Tabla de usuarios locales del microservicio principal
CREATE TABLE IF NOT EXISTS app_users (
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT    NOT NULL UNIQUE,
    email    TEXT    NOT NULL
);

-- Tabla de reservas
CREATE TABLE IF NOT EXISTS reservation (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id    INTEGER NOT NULL,
    package_id INTEGER NOT NULL,
    date       TEXT    NOT NULL,
    FOREIGN KEY (package_id) REFERENCES travel_package(id)
);

-- ============================================================
-- DATOS SEMILLA
-- ============================================================
INSERT INTO travel_package (name, description, price, duration_days) VALUES
    ('Escapada San Pedro',  'Tour guiado por el Desierto de Atacama',    350000.0, 4),
    ('Carretera Austral',   'Ruta completa por la Patagonia chilena',     890000.0, 7),
    ('Magia de Rapa Nui',   'Paquete todo incluido a Isla de Pascua',    1200000.0, 5),
    ('Lagos del Sur',       'Circuito Lago Llanquihue y Todos los Santos', 620000.0, 5),
    ('Desierto Florido',    'Experiencia única en el Valle de Copiapó',   290000.0, 3);

INSERT INTO travel_package_destinations (travel_package_id, destinations) VALUES
    (1, 'San Pedro de Atacama'),
    (1, 'Valle de la Luna'),
    (1, 'Salar de Atacama'),
    (2, 'Coyhaique'),
    (2, 'Puerto Tranquilo'),
    (2, 'Villa OHiggins'),
    (3, 'Hanga Roa'),
    (3, 'Rano Raraku'),
    (4, 'Puerto Montt'),
    (4, 'Petrohue'),
    (5, 'Copiapó'),
    (5, 'Parque Nacional Llanos de Challe');

INSERT INTO app_users (username, email) VALUES
    ('italo_camp',    'italo@travelgo.cl'),
    ('admin_travel',  'admin@travelgo.cl');

INSERT INTO reservation (user_id, package_id, date) VALUES
    (1, 1, '2026-06-15'),
    (1, 3, '2026-07-20');

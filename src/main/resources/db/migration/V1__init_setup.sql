-- Creación de la tabla de Paquetes de Viaje
CREATE TABLE travel_package (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    price REAL,
    duration_days INTEGER
);

-- Creación de la tabla para los destinos (Colección de elementos del paquete)
CREATE TABLE travel_package_destinations (
    travel_package_id INTEGER,
    destinations TEXT,
    FOREIGN KEY(travel_package_id) REFERENCES travel_package(id)
);

-- Creación de la tabla de Usuarios locales (Obligatoria para que no falle tu filtro JWT actual)
CREATE TABLE app_users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL
);

-- Creación de la tabla de Reservas
CREATE TABLE reservation (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    package_id INTEGER NOT NULL,
    date TEXT NOT NULL
);

-- --- INSERCIÓN DE DATOS INICIALES (MIGRACIÓN / SEEDING) ---
INSERT INTO travel_package (name, description, price, duration_days) VALUES 
('Escapada San Pedro', 'Tour guiado por el Desierto de Atacama', 350000.0, 4),
('Carretera Austral', 'Ruta completa por la Patagonia chilena', 890000.0, 7),
('Magia de Rapa Nui', 'Paquete todo incluido a Isla de Pascua', 1200000.0, 5);

INSERT INTO travel_package_destinations (travel_package_id, destinations) VALUES 
(1, 'San Pedro'), (1, 'Valle de la Luna'),
(2, 'Coyhaique'), (2, 'Puerto Tranquilo'),
(3, 'Hanga Roa');

INSERT INTO app_users (username, email) VALUES 
('italo_camp', 'italo@travelgo.cl'),


INSERT INTO reservation (user_id, package_id, date) VALUES 
(1, 1, '2026-06-15');
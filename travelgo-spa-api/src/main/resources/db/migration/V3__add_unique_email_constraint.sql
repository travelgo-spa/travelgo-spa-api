-- ============================================================
-- V3__add_unique_email_constraint.sql
-- El servicio ya rechaza emails duplicados (BusinessException),
-- pero la restricción no existía a nivel de base de datos.
-- SQLite no soporta ALTER TABLE ... ADD CONSTRAINT, se usa un
-- índice único como equivalente funcional.
-- ============================================================

CREATE UNIQUE INDEX IF NOT EXISTS idx_app_users_email ON app_users(email);

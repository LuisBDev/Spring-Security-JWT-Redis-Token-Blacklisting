-- Roles
INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

-- Permissions
INSERT INTO permissions (name) VALUES ('USER_READ');
INSERT INTO permissions (name) VALUES ('USER_WRITE');
INSERT INTO permissions (name) VALUES ('ADMIN_READ');
INSERT INTO permissions (name) VALUES ('ADMIN_WRITE');

-- ROLE_USER permissions: USER_READ, USER_WRITE
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_USER' AND p.name IN ('USER_READ', 'USER_WRITE');

-- ROLE_ADMIN permissions: all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_ADMIN';

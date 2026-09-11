INSERT INTO menu_items (parent_id, menu_key, icon, route, sort_order)
VALUES (NULL, 'menu.dashboard', 'dashboard', '/dashboard', 0),
       (NULL, 'menu.profile', 'person', '/profile', 1),
       (NULL, 'menu.admin', 'admin_panel_settings', NULL, 8),
       (NULL, 'menu.supervisor', 'store', NULL, 8);

INSERT INTO menu_items (parent_id, menu_key, icon, route, sort_order)
SELECT id, 'menu.admin.supervisors', 'supervisor_account', '/admin/supervisors', 0
FROM menu_items WHERE menu_key = 'menu.admin';

INSERT INTO menu_items (parent_id, menu_key, icon, route, sort_order)
SELECT id, 'menu.admin.loginLogs', 'history', '/admin/login-logs', 1
FROM menu_items WHERE menu_key = 'menu.admin';

INSERT INTO menu_items (parent_id, menu_key, icon, route, sort_order)
SELECT id, 'menu.supervisor.employees', 'groups', '/supervisor/employees', 0
FROM menu_items WHERE menu_key = 'menu.supervisor';

INSERT INTO menu_permissions (menu_item_id, role)
SELECT id, 'ADMIN' FROM menu_items WHERE menu_key = 'menu.dashboard'
UNION ALL
SELECT id, 'SUPERVISOR' FROM menu_items WHERE menu_key = 'menu.dashboard'
UNION ALL
SELECT id, 'EMPLOYEE' FROM menu_items WHERE menu_key = 'menu.dashboard'
UNION ALL
SELECT id, 'ADMIN' FROM menu_items WHERE menu_key = 'menu.profile'
UNION ALL
SELECT id, 'SUPERVISOR' FROM menu_items WHERE menu_key = 'menu.profile'
UNION ALL
SELECT id, 'EMPLOYEE' FROM menu_items WHERE menu_key = 'menu.profile'
UNION ALL
SELECT id, 'ADMIN' FROM menu_items WHERE menu_key = 'menu.admin'
UNION ALL
SELECT id, 'ADMIN' FROM menu_items WHERE menu_key = 'menu.admin.supervisors'
UNION ALL
SELECT id, 'ADMIN' FROM menu_items WHERE menu_key = 'menu.admin.loginLogs'
UNION ALL
SELECT id, 'SUPERVISOR' FROM menu_items WHERE menu_key = 'menu.supervisor'
UNION ALL
SELECT id, 'SUPERVISOR' FROM menu_items WHERE menu_key = 'menu.supervisor.employees';

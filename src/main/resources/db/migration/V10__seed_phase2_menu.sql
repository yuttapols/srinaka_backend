INSERT INTO menu_items (parent_id, menu_key, icon, route, sort_order)
SELECT id, 'menu.supervisor.services', 'pi-briefcase', '/supervisor/services', 1
FROM menu_items WHERE menu_key = 'menu.supervisor';

INSERT INTO menu_items (parent_id, menu_key, icon, route, sort_order)
SELECT id, 'menu.supervisor.promotions', 'pi-percentage', '/supervisor/promotions', 2
FROM menu_items WHERE menu_key = 'menu.supervisor';

INSERT INTO menu_items (parent_id, menu_key, icon, route, sort_order)
SELECT id, 'menu.supervisor.bookings', 'pi-calendar', '/supervisor/bookings', 3
FROM menu_items WHERE menu_key = 'menu.supervisor';

INSERT INTO menu_items (parent_id, menu_key, icon, route, sort_order)
SELECT id, 'menu.supervisor.shopInfo', 'pi-map-marker', '/supervisor/shop-info', 4
FROM menu_items WHERE menu_key = 'menu.supervisor';

INSERT INTO menu_items (parent_id, menu_key, icon, route, sort_order)
VALUES (NULL, 'menu.employee', 'pi-id-card', NULL, 8);

INSERT INTO menu_items (parent_id, menu_key, icon, route, sort_order)
SELECT id, 'menu.employee.bookings', 'pi-calendar', '/employee/bookings', 0
FROM menu_items WHERE menu_key = 'menu.employee';

INSERT INTO menu_permissions (menu_item_id, role)
SELECT id, 'SUPERVISOR' FROM menu_items WHERE menu_key = 'menu.supervisor.services'
UNION ALL
SELECT id, 'SUPERVISOR' FROM menu_items WHERE menu_key = 'menu.supervisor.promotions'
UNION ALL
SELECT id, 'SUPERVISOR' FROM menu_items WHERE menu_key = 'menu.supervisor.bookings'
UNION ALL
SELECT id, 'SUPERVISOR' FROM menu_items WHERE menu_key = 'menu.supervisor.shopInfo'
UNION ALL
SELECT id, 'EMPLOYEE' FROM menu_items WHERE menu_key = 'menu.employee'
UNION ALL
SELECT id, 'EMPLOYEE' FROM menu_items WHERE menu_key = 'menu.employee.bookings';

DELETE FROM ledge_naming_attribute;

DELETE FROM ledge_naming_context;

DELETE FROM ledge_id_table WHERE table_name = 'ledge_naming_context';

INSERT INTO ledge_naming_context (context_id, dn, parent) VALUES(1, 'dc=objectledge,dc=org', -1);

INSERT INTO ledge_naming_context (context_id, dn, parent) VALUES(2, 'ou=people,dc=objectledge,dc=org', 1);

INSERT INTO ledge_id_table (table_name, next_id) VALUES('ledge_naming_context', 3);


DELETE FROM ledge_naming_attribute;

DELETE FROM ledge_naming_context;

INSERT INTO ledge_naming_context (context_id, dn, parent) VALUES(1, 'dc=objectledge,dc=org', -1);

INSERT INTO ledge_naming_context (context_id, dn, parent) VALUES(2, 'ou=people,dc=objectledge,dc=org', 1);

ALTER SEQUENCE ledge_naming_context_seq RESTART WITH 3;

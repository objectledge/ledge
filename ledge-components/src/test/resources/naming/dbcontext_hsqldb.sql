CREATE TABLE db_context
(
    db_context_id BIGINT NOT NULL,
    dn VARCHAR(255) NOT NULL,
    parent INTEGER NOT NULL,

    PRIMARY KEY (db_context_id)
);

-- to powinno robic sie samo
CREATE UNIQUE INDEX db_context_id_pk ON db_context (db_context_id);
CREATE UNIQUE INDEX db_context_dn ON db_context (dn);

-- a to nie
CREATE INDEX db_context_parent ON db_context (parent);

-- #####################################################

CREATE TABLE db_context_attr
(
    db_context_id BIGINT NOT NULL,
    name VARCHAR(64) NOT NULL,
    value VARCHAR(64) NOT NULL,

    PRIMARY KEY (db_context_id, name, value),
    FOREIGN KEY (db_context_id) REFERENCES db_context
);

-- to powinno samo sie zrobic
CREATE INDEX db_context_attr_pk ON db_context_attr (db_context_id, name, value);

-- a to nie
CREATE INDEX db_context_id_fk ON db_context_attr (db_context_id);

-- ####################################################

-- value nie mo¿e byæ NULL ale mo¿e byæ puste
-- 0 jako parent oznacza ¿e to root

-- #####################################################

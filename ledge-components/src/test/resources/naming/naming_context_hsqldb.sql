CREATE TABLE naming_context
(
    context_id BIGINT NOT NULL,
    dn VARCHAR(255) NOT NULL,
    parent BIGINT NOT NULL,
    PRIMARY KEY (context_id)
);

-- to powinno robic sie samo
CREATE UNIQUE INDEX context_id_pk ON naming_context (context_id);
CREATE UNIQUE INDEX context_dn ON naming_context (dn);

-- #####################################################

CREATE TABLE naming_attribute
(
    context_id BIGINT NOT NULL,
    name VARCHAR(64) NOT NULL,
    value VARCHAR(64) NOT NULL,

    PRIMARY KEY (context_id, name, value),
    FOREIGN KEY (context_id) REFERENCES naming_context
);

-- to powinno samo sie zrobic
CREATE INDEX naming_attribute_pk ON naming_attribute (context_id, name, value);

-- a to nie
CREATE INDEX naming_context_id_fk ON naming_attribute (context_id);

CREATE TABLE ledge_naming_context
(
    context_id BIGINT NOT NULL,
    dn VARCHAR(255) NOT NULL,
    parent BIGINT NOT NULL,
    PRIMARY KEY (context_id)
);

-- to powinno robic sie samo
CREATE UNIQUE INDEX ledge_naming_context_pk ON ledge_naming_context (context_id);
CREATE UNIQUE INDEX ledge_naming_context_dn ON ledge_naming_context (dn);

-- #####################################################

CREATE TABLE ledge_naming_attribute
(
    context_id BIGINT NOT NULL,
    name VARCHAR(64) NOT NULL,
    value VARCHAR(64) NOT NULL,

    PRIMARY KEY (context_id, name, value),
    FOREIGN KEY (context_id) REFERENCES ledge_naming_context
);

-- to powinno samo sie zrobic
CREATE INDEX ledge_naming_attribute_pk ON ledge_naming_attribute (context_id, name, value);

-- a to nie
CREATE INDEX ledge_naming_context_id_fk ON ledge_naming_attribute (context_id);

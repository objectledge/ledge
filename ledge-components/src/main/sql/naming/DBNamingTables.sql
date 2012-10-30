CREATE TABLE ledge_naming_context
(
    context_id BIGINT NOT NULL,
    dn VARCHAR(255) NOT NULL,
    parent BIGINT NOT NULL,
    PRIMARY KEY (context_id)
);

CREATE UNIQUE INDEX ledge_naming_context_dn_idx ON ledge_naming_context (dn);

CREATE SEQUENCE ledge_naming_context_seq;

CREATE TABLE ledge_naming_attribute
(
    context_id BIGINT NOT NULL,
    name VARCHAR(64) NOT NULL,
    value VARCHAR(64) NOT NULL,

    PRIMARY KEY (context_id, name, value),
    CONSTRAINT ledge_naming_attribute_context_id_fkey FOREIGN KEY (context_id) REFERENCES ledge_naming_context(context_id)
);

CREATE INDEX ledge_naming_context_id_idx ON ledge_naming_attribute (context_id);

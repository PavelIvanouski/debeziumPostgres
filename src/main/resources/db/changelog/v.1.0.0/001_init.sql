CREATE TABLE TEST_TABLE
(
    id            bigint    NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    name          varchar   NOT NULL,
    code          varchar,
    CONSTRAINT test_table_pkey PRIMARY KEY (id)
);

CREATE TABLE CHANGE
(
    id bigint    NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    name          varchar ,
    description varchar ,
    operation varchar,
    table_name varchar,
    record_id bigint,
    change_content jsonb,
    CONSTRAINT change_table_pkey PRIMARY KEY (id)
);



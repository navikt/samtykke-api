DO
$$
BEGIN
    IF EXISTS
        (select 1 from pg_roles where rolname = 'cloudsqliamuser')
    THEN
        grant all on all tables in schema public to cloudsqliamuser;
    END IF;
END
$$;

CREATE TABLE consent(
    id BIGSERIAL CONSTRAINT consent_pk PRIMARY KEY,
    title TEXT,
    responsible_group TEXT,
    purpose TEXT,
    total_involved BIGINT,
    expiration DATE,
    code TEXT
);
-- Add references to candidate and employee

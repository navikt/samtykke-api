DO
$$
BEGIN
    IF EXISTS
        (select 1 from pg_roles where rolname = 'cloudsqliamuser')
    THEN
        grant all privileges on all tables in schema public to cloudsqliamuser;
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
    code TEXT UNIQUE
);

CREATE TYPE STATUS AS ENUM ('ACCEPTED', 'WITHDRAWN');
CREATE TABLE candidate(
    id BIGSERIAL CONSTRAINT candidate_pk PRIMARY KEY,
    name TEXT,
    email TEXT,
    status STATUS,
    consented DATE,
    audio_recording BOOLEAN,
    store_info BOOLEAN,
    consent_id BIGINT CONSTRAINT candidate_consent_id_fk REFERENCES consent ON DELETE CASCADE
);

CREATE TABLE employee(
    id TEXT UNIQUE CONSTRAINT employee_pk PRIMARY KEY,
    firstname TEXT,
    lastname TEXT,
    email TEXT,
    consent_id BIGINT CONSTRAINT employee_consent_id_fk REFERENCES consent ON DELETE CASCADE
);

CREATE TABLE citizen(
    id TEXT UNIQUE CONSTRAINT citizen_pk PRIMARY KEY,
    candidate_id BIGINT CONSTRAINT citizen_candidate_id_fk REFERENCES candidate ON DELETE CASCADE
);

CREATE TABLE message(
    id BIGSERIAL CONSTRAINT message_pk PRIMARY KEY,
    timestamp DATE,
    title TEXT,
    description TEXT,
    read BOOLEAN,
    ref TEXT,
    employee_id TEXT CONSTRAINT message_employee_id_fk REFERENCES employee ON DELETE CASCADE
);
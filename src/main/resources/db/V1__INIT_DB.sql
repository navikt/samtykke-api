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
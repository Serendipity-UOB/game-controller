# Game Controller

A simple web app used for testing early Hacker Hunt game mechanics.

To run the PostgreSQL server:
```bash
postgres -D /usr/local/var/postgres
```

And create the correct database and access:
```bash
psql
CREATE ROLE serendipity WITH LOGIN PASSWORD 'serendipity';
ALTER ROLE serendipity CREATEDB;
CREATE DATABASE serendipitydb;
GRANT ALL PRIVILEGES ON DATABASE serendipitydb TO serendipity;
```

# Hacker Hunt Server

A Spring server for the Hacker Hunt game to be used by both iOS and Andriod app.

The app is currently hosted on Heroku:

https://serendipity-game-controller.herokuapp.com/

To run the server locally you need to first configure the database:

1. Run the PostgreSQL server:
```bash
postgres -D /usr/local/var/postgres
```

2. Create the database and access rights:
```bash
psql
CREATE ROLE serendipity WITH LOGIN PASSWORD 'serendipity';
ALTER ROLE serendipity CREATEDB;
CREATE DATABASE serendipitydb;
GRANT ALL PRIVILEGES ON DATABASE serendipitydb TO serendipity;
```

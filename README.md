# Game Controller

A simple web app used for testing early Hacker Hunt game mechanics.

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

### Entity Relation Diagram

![entity-relation](/docs/entity-relation.png)

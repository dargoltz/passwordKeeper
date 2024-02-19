This is PasswordKeeper - Json Rest Api

This API allows you:
- Perform CRUD operations on your password records.
- Search records by username or part of the username.
- View the complete history of record changes.
- Search the record history for a specific record.
- Import/export records from/to CSV.

Stack:
- Scala 2.13.12
- Play Framework v
- Slick 5.2.0
- Postgresql 42.5.4

Installing:
- Create postgres db with docker-compose.yml
- Run conf/db/migration/Init_CreateTable.sql after connecting database
- sbt run

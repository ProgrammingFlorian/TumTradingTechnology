 # Database

## Setup

- Install MySQL / MariaDB
- Run following commands to create user and database:
```mysql
CREATE DATABASE ttt;

CREATE USER 'lkw'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON ttt.* TO 'lkw'@'localhost';
FLUSH PRIVILEGES;
```
 
## Configuration

Port: `3306` \
Database Name: `ttt` \
Username: `lkw` \
Password: `password`

See [application.properties](../backend/src/main/resources/application.properties)
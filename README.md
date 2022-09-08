# TUMTradingTechnology

TUM Student project by "Die harmonischen LKWs".

## Documentation

Documentation can be found in the [documentation folder](documentation). Project structure is documented along with uniform coding style. 

[Problem Statement](documentation/problem_statement.md) \
[Frontend](documentation/frontend.md) \
[Backend](documentation/backend.md) \
[Database](documentation/database.md) \
[Code Style](documentation/codestyle.md)

## Setup

- Generate JWT Keys: _private.key_ and _public.pub_ in [backend/src/main/resources](backend/src/main/resources):
```shell
# In /backend/src/main/resources
# Leave passphrase empty
openssl genrsa -out private.key.pkcs1 4096
openssl rsa -in private.key.pkcs1 -pubout -outform PEM -out public.pub
openssl pkcs8 -topk8 -inform PEM -outform PEM -in private.key.pkcs1 -out private.key -nocrypt
rm private.key.pkcs1
```
- Insert API Key in [TODO]
- Setup [database](documentation/database.md)
- Run ``gradle build``

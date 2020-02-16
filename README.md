# Exchanger

Exchanger is a REST application that imitates simple money transfer service.

#### Building
`mvn clean package`

Target jar is `exchanger-<version>.jar`

#### Testing
`mvn test`

Please note that some integration tests may fail if run from IDE.
This happens due to firewall / antivirus policies.

#### Running
`java -jar exchanger-<version>.jar`

#### REST requests
Base URL: http://localhost:7000/exchanger/

| Method | URL                  | Body                           | Description                                             | Success Response |
|--------|----------------------|--------------------------------|---------------------------------------------------------|------------------|
| POST   | /account/create      | {"id":2,"balance":1000}        | Creates an account with specified balance, 0 if omitted | Status: 200      |
| GET    | /account/balance/:id |                                | Get balance for an account with specified id            | Body: "1000"     |
| POST   | /account/withdraw    | {"id":2,"amount":500}          | Decrease balance of specified account                   | Status: 200      |
| POST   | /account/deposit     | {"id":2,"amount":500}          | Increase balance of specified account                   | Status: 200      |
| POST   | /transfer            | {"from":1,"to":2,"amount":500} | Transfer money from one account to another              | Status: 200      |

In case of any incorrect API usage (e.g. one tries to create an account with some ID that already exists)
client will get 400 status code with error details in body.

**Example:**
```
Request
method: POST
url:    http://localhost:7000/exchanger/account/create
body:   {"id":-2,"balance":1000}   

Response
status: 40
body:   Account id should be > 0, but was -2```
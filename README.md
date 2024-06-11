# SimpleOnlineStore v1.0.0

 blah blah blah

## Requirements
- Java Version 22
- Maven  (3.9.6*)

## Lifecycle Scripts
Script names self explanatory, both need to run at ```~Backend\simpleonlinestore```
- ```Backend\simpleonlinestore\build.bash```
  - Jar is placed in ```Backend\simpleonlinestore\target\simpleonlinestore-[version].jar```
- ```Backend\simpleonlinestore\test.bash```

## Routes
```
  /v1
  |-- /auth
  |   |-- /sigin
  |   |-- /signup/customer
  |   |-- /user/update
  |   |-- /delete
  |   `-- /signout
  |         `-- /all
  |-- /product
  |   |-- /get
  |   |   |-- /all
  |   |   `-- /{productId}
  |   |-- /update
  |   |-- /add
  |   `-- /delete
  |-- /customer
  |   |-- /details
  |   `-- /update
  |-- /order
  |   |-- /create
  |   |   `-- /simple
  |   |-- /admin
  |   |   |-- /create
  |   |   `-- /update
  |   |       `-- /status
  |   |-- /update
  |   `-- /get
  |       |-- /active
  |       |-- /all
  |       `-- /{orderId}
  `-- /hello
      |-- /user
      `-- /admin
```
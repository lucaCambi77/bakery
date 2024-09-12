
## Task ##

Given a customer order you are required to determine the cost and pack breakdown for each product.
To save on shipping space each order should contain the minimal number of packs.
See pdf in /pdf/Hexad-Backend_Assignment_Bakery.pdf

## Install and run the application
To use the application you need java 8 installed in your machine. This is a spring boot application with embedded tomcat

There is a default profiles with which you can run the application

To install 

```bash
- ./gradlew clean install
```

To run  

```
- java -jar build/libs/bakery.jar
```

After the application is running, order api is exposed :

## (POST) http://localhost:8080/order
To request an order to the application with following json as sample (application/json is required as content type in header request) :

{"items":{"CF":13,"MB11":14,"VS5":10}}

If the order is completed, you will receive a report with all the information about prices and packaging


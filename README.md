# OuraCloud

## Interface Specification

https://docs.google.com/document/d/1NtmBGcGM9pmhsX2WteN_0kgNoV0U1xnG9tBz5rIKXQY/edit

## Generating Groovy Doc

Run the following command from the root directory to generate Groovy doc:

```
groovydoc --destdir web-app/docs grails-app/**/**/**/**/**/*.groovy grails-app/**/**/**/**/*.groovy grails-app/**/**/**/*.groovy grails-app/**/**/*.groovy grails-app/**/*.groovy
```

## Running Locally with other Grails application

For the development purpose, if this Ouracloud application and the other Grails application are running locally which
involves the testing of OAuth2 authentication then make sure that both the application should be running over the 
different domain like one is running on `localhost` and other should be `127.0.0.1`.

If we don't follow this, when we start the authentication with Ouracloud, the session of the client application will 
expire after authentication because the cookie of client application will now be of the Ouracloud application. 
This is because, Grails creates the domain less `JSESSIONID` cookie for maintaining the session and if both the 
application will be running on the same domain and the different port, then one of them will override the other's 
cookie. 

Source code for OURA cloud originally created by We Are Curious.

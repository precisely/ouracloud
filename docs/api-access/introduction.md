# Getting Started With The Oura API

## Overview

Operations are allowed via the Oura API endpoints:

1. Oauth2 authentication
2. Creating/managing/deleting the user account
3. Sending summary events data in JSON format 
4. Creating, updating and deleting summary event data

## Documentation Overview
 
* All the examples given in this doc use the **CURL** tool to make the requests
* All the examples use the `localhost` for API calls, replace them with the actual server URL
* All the examples use the Oura client app ID as `ouracloud`. Replace it with actual client app ID.

## Access Tokens / Authentication

To access various endpoints, the user or app needs to obtain an access token which provides a temporary and secure access to the Oura APIs or to make API calls. When you obtain the token it includes some additional information about when the token will expire.

Following are the different types of access tokens to support different use cases:

### 1. User Access Token

This token is used when interacting with the data API like creating, updating or sending the summary event data etc. User access token can be obtained in two different types to support two different use cases:

#### 1.1 User Password Credentials Grant Based Token

This type of authentication requires user's username and password of Oura platform to obtain an access token. This authentication should be used to authorize the user via Oura mobile application when they enter the username and password in the Oura mobile app.

Access token can be obtained in a single step using this authentication type:

**Request Method:** POST

```shell

curl -X POST \
    -d "client_id=ouracloud" \
    -d "grant_type=password" \
    -d "username=<users-username>" \
    -d "password=<users-password>" \
    -d "scope=read"		http://localhost:8080/oauth/token

```

**Response**

```json
{
    "access_token": "1d49fc35-2af6-477e-8fd4-ab0353a4a76f",
    "token_type": "bearer",
    "refresh_token": "4996ba33-be3f-4555-b3e3-0b094a4e60c0",
    "expires_in": 43199,
    "scope": "read"
}
```

#### 1.2 Authorization Code Grant Based Token

This type of authentication uses the OAuth2 (2-legged OAuth) to obtain an access token. This authentication should be used when a user wants to give access to third-party apps like Curious. This authentication can be initiated by directing user to the authorization endpoint:

```
http://localhost:8080/oauth/authorize?response_type=code&client_id=ouracloud&scope=read
```

The user will be redirected to the login page of Oura platform. After logging in, the user will be prompted to confirm or authenticate the request. Doing so will redirect the user to the configured redirect URL (in the client app) with an authorization code included in the query.

```
http://example.com/?code=139R59
```

This authorization code can be further exchanged for an access token via the token endpoint:

**Request Method:** POST

```shell
curl -X POST \
    -d "client_id=ouracloud" \
    -d "grant_type=authorization_code" \
    -d "code=139R59"		http://localhost:8080/oauth/token
```

**Response**

```json
{
    "access_token": "1d49fc35-2af6-477e-8fd4-ab0353a4a76f",
    "token_type": "bearer",
    "refresh_token": "4996ba33-be3f-4555-b3e3-0b094a4e60c0",
    "expires_in": 43199,
    "scope": "read"
}
```

### 2. Client Access Token

The client access token authentications are used to access Oura APIs on behalf of a client app rather than a user. This authentication can be used to make API calls for user/account management endpoints. For example: Creating a new user account from Oura mobile API.

**Request Method:** POST

```shell
curl -X POST \
     -d "client_id=ouracloud" \
     -d "grant_type=client_credentials" \
     -d "scope=read"		http://localhost:8080/oauth/token
```

**Response:**

```json
{
    "access_token": "1d49fc35-2af6-477e-8fd4-ab0353a4a76f",
    "token_type": "bearer",
    "expires_in": 43199,
    "scope": "read"
}
```

## Using Protected Resources/Endpoints

As described in the above section, an access token is used to make Oura API calls. After obtaining an access token from any of the above described way, every resource API call must include the access token in the `Authorization` header as described below:

```
Authorization:       Bearer 1d49fc35-2af6-477e-8fd4-ab0353a4a76f
```

For example, to get all **sleep** event data: 

```shell
curl -X GET -h "Authorization: Bearer 1d49fc35-2af6-477e-8fd4-ab0353a4a76f" http://localhost:8080/api/sleep
```

## Using Resource API

To get the detail of various available API endpoints, read the Groovy documentation for `DataController` doc.
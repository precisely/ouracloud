# First Time Installation

To start development on this repository here are some steps which needs be taken to run the application.

## MongoDB Installation

1. Install the [MongoDB](http://docs.mongodb.org/manual/installation/) 3.x database.
2. After successful installation, log in to the MongoDB shell `$ mongo`
3. Create the various databases (giving example for development database): `use ouracloud_dev`
4. Add the user to it:

```javascript
db.createUser({
    user: "ouracloud",
    pwd: "<my-password>",
    roles: ["dbAdmin", "readWrite"]
});
```
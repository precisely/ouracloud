package us.wearecurio.users

import org.bson.types.ObjectId

class OuraShopPassword {

    static constraints = {
        password(blank: false)
        user(index: true, indexAttributes: [unique: true])
    }

    ObjectId id
    User user
    String password
}
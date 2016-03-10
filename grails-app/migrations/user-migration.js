//mongeez formatted javascript

// changeset Shashank:username-email-12282015

db.user.find().snapshot().forEach(function (userInstance) {
    db.user.update({_id: userInstance._id}, {$set: {username: userInstance.email}});
});

// changeset Shashank:adding-admin-client-role-02262016

var emails = ["sfkate@mac.com", "mitsu@syntheticzero.com", "mitsu@wearecurio.us", "mitsu@ministryofthought.com",
        "teemu.kurppa@gmail.com", "oura@causecode.com"];

var roleClientManager = db.role.findOne({authority: "ROLE_CLIENT_MANAGER"});
var roleAdmin = db.role.findOne({authority: "ROLE_ADMIN"});

var dbRef = db;
function counterId() {
    // Every domain has a subsequent domain suffixed with "next_id" to maintain next identifier.
    var ret = dbRef.userRole.next_id.findAndModify({query: {_id: "userRole"}, update: {$inc: {next_id: 1}}, new: true, upsert: true});
    return ret.next_id;
}

db.user.find({email: {$in: emails}}).snapshot().forEach(function(userInstance) {
    db.userRole.insert([{
        _id: NumberLong(counterId()),
        role: roleClientManager._id,
        user: userInstance._id
    }, {
        _id: NumberLong(counterId()),
        role: roleAdmin._id,
        user: userInstance._id
    }]);
});

// changeset Shashank:remove-beta-params-session-key-02-10-2016
db.persistentSessionAttribute.remove({name: "DISPLAY_SIGNUP_FORM"});
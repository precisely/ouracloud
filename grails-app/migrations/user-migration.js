//mongeez formatted javascript

// changeset Shashank:username-email-12282015

db.user.find().snapshot().forEach(function (userInstance) {
    db.user.update({_id: userInstance._id}, {$set: {username: userInstance.email}});
});

//mongeez formatted javascript

// changeset Shashank:cleanup-timezone-12282015

db.summaryData.update({timeZone: "null"}, {$set: {timeZone: ""}}, {multi: true});
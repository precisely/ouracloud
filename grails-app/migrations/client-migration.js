//mongeez formatted javascript

// changeset Shashank:add-environment-01-19-2015

var production = 1, development = 2;
db.client.update({_id: {$in: [1, 3]}}, {$set: {environment: production, accessTokenValiditySeconds: 0}}, {multi: true});
db.client.update({_id: 4}, {$set: {environment: development}});
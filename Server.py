import pyrebase
from flask import Flask
from flask_restful import Api, Resource, reqparse

#Firebase
firebaseConfig = {"apiKey": "AIzaSyB6wGev6Pt1EfDvUkl-5EjwDtZwRlPHduc",
                  "authDomain": "enigma-350909.firebaseapp.com",
                  "databaseURL": "https://enigma-350909-default-rtdb.europe-west1.firebasedatabase.app",
                  "projectId": "enigma-350909",
                  "storageBucket": "enigma-350909.appspot.com",
                  "messagingSenderId": "8984037607",
                  "appId": "1:8984037607:web:21a9620b78aca906337a42"}
firebase = pyrebase.initialize_app(firebaseConfig)
db = firebase.database()



#request codes and parser arguments

#get constants
SEARCH_FRIEND = 0
GET_ID = 1
GET_USER_INFORMATION = 2
GET_FRIENDS_LIST = 3
GET_PENDING_FRIENDS_REQUEST = 4
GET_USER_EXIST = 10

#put constants
PUT_NEW_USER = 11

#post constants
SEND_FRIEND_REQUEST = 5
CHANGE_NAME = 6
ACCEPT_FRIEND_REQUEST = 8

#delete constants
DELETE_FRIEND = 7
REJECT_FRIEND_REQUEST = 9

#get parser
getParser = reqparse.RequestParser()
getParser.add_argument('req', type = int, required = True)
getParser.add_argument('userId', type = str, required = False)
getParser.add_argument('friendId', type = str, required = False)

#post parser
postParser = reqparse.RequestParser()
postParser.add_argument('req', type = int, required = True)
postParser.add_argument('userId', type = str, required = False)
postParser.add_argument('friendId', type = str, required = False)
postParser.add_argument('newName', type = str, required = False)

#put parser
putParser = reqparse.RequestParser()
putParser.add_argument('req', type = int, required = True)
putParser.add_argument('userId', type = str, required = False)
putParser.add_argument('friendId', type = str, required = False)
putParser.add_argument('username', type = str, required = False)
putParser.add_argument('id', type = str, required = False)



#delete parser
deleteParser = reqparse.RequestParser()
deleteParser.add_argument('req', type = int, required = True)
deleteParser.add_argument('userId', type = str, required = False)
deleteParser.add_argument('friendId', type = str, required = False)

# Api call handler
class EnigmaServer(Resource):
    
    def __init__(self):
        self.serverUtils = EnigmaServerUtils()
    
    def get(self):    
        args = getParser.parse_args()
        users = db.child("Users").get().val()
        req = args['req']
        userIdValue = args['userId']
        friendId = args['friendId']
        
        #get friends list input(req, userId, friendid)
        if req == SEARCH_FRIEND:
            if db.child("Users").child(userIdValue).get().val()["id"] != friendId:
                for user in users:
                    friend = db.child("Users").child(user).child("id").get().val()
                    if friendId == friend:
                        friendIdValue = user
                        if self.serverUtils.checkIfAlreadyAdded(userIdValue, friendId):
                            return {"Message": "friends already added", "status": "alreadyAdded", "error": False}
                        elif self.serverUtils.checkifAlreadySent(userIdValue, friendIdValue):
                            return {"Message": "friend request already sent", "status": "alreadySent", "error": False}
                        else:
                            return {"Message": "friend found", "status": "found", "error": False}
            else:
                return {"message": "can't add yourself", "status": "yourself", "error": False}
            return {"message": "user not found", "status": "notFound", "error": False}
        
        #get last id input(req)
        if req == GET_ID:
            lastId = db.child("UserLastId").get().val()
            newId = self.serverUtils.createId(lastId)
            db.update({"UserLastId": newId})
            return {"message": "last user Id", "newId": newId, "error": False}
        
        #get user information input(req, userId)
        if req == GET_USER_INFORMATION:
            username = db.child("Users").child(userIdValue).get().val()["username"]
            userId = db.child("Users").child(userIdValue).get().val()["id"]
            return {"message": "username and password of the current user", "username": username, "id": userId, "error": False}
        
        #get friends input(req, userId)
        if req == GET_FRIENDS_LIST:
            friendsList = db.child("Users").child(userIdValue).child("friends").get().val()
            return {"message": "obtaining friends list of the current user", "friends": friendsList, "error": False}
        
        #get pending friend requests list (pending) input(req, userId)
        if req == GET_PENDING_FRIENDS_REQUEST:
            pendingFriendRequests = db.child("Users").child(userIdValue).child("pendingFriendRequests").get().val()
            return{"message": "obtaining pending friend requests of the current user", 
                   "pendingFriendRequests": pendingFriendRequests, 
                   "error": False}
        
        #get return if the current user exist input(req, userId)
        if req == GET_USER_EXIST:
            for user in users:
                if user == userIdValue:
                    return{"message": "user already exist", 
                           "exist": True, 
                           "error": False}
            return{"message": "user not exist", 
                   "exist": False, 
                   "error": False}
                    
                    
        #TODO
        #get inviti
        #get utenti lobby
        return {"message": "get request failed", "error": True}
      
    def put(self):
        args = putParser.parse_args()
        users = db.child("Users").get().val()
        req = args['req']
        userId = args['userId']
        username = args['username']
        userIdField = args['id']
        #put create new user input(req, userId, usrname, id)
        if req == PUT_NEW_USER:
            db.child("Users").child(userId).set({"username": username, "id": userIdField})
            return {"message": "user created", "error": False}
        #lobby creation
        return {"message": "put request failed", "error": True}
    
    def post(self):
        args = postParser.parse_args()
        users = db.child("Users").get().val()
        req = args['req']
        userIdValue = args['userId']
        friendId = args['friendId']
        newName = args["newName"]
        
        #friend request input(req, userId, friendid)
        if req == SEND_FRIEND_REQUEST:
            for user in users:
                friendIdField = db.child("Users").child(user).get().val()["id"]
                if friendId == friendIdField:
                    newPendingFriendRequestsList = db.child("Users").child(user).child("pendingFriendRequests").get().val()
                    if newPendingFriendRequestsList == None:
                        newPendingFriendRequestsList = []   
                        
                    newUserPending = db.child("Users").child(userIdValue).get().val()
                    newPendingFriendRequestsList.append({"id": newUserPending["id"], "username": newUserPending["username"]})
                    db.child("Users").child(user).update({"pendingFriendRequests": newPendingFriendRequestsList})
                    
                    pendingFriendRequests = db.child("Users").child(user).child("pendingFriendRequests").get().val()
                    return{"message": "friend request send and added to pending friend requests", 
                           "pendingFriendRequests": pendingFriendRequests, 
                           "error": False}
        
        #change username input(req, userId, newName)
        if req == CHANGE_NAME:
            db.child("Users").child(userIdValue).update({"username": newName})
            return {"message": "username changed", "username": newName, "error": False}
            
        #accept friend request(req, userId, friendId)
        if req == ACCEPT_FRIEND_REQUEST:
            pendingFriendRequestsList = db.child("Users").child(userIdValue).child("pendingFriendRequests").get().val()
            friendsList = db.child("Users").child(userIdValue).child("friends").get().val()
            if friendsList == None:
                friendsList = []
            newPendingFriendRequestsList = []
            for pendingFriend in pendingFriendRequestsList:
                pendingFriendTmp = {"id": pendingFriend["id"], "username": pendingFriend["username"]}
                if pendingFriend["id"] == friendId:
                    friendsList.append(pendingFriendTmp)
                    continue
                newPendingFriendRequestsList.append(pendingFriendTmp)
                                
            for user in users:
                if db.child("Users").child(user).get().val()["id"] == friendId:
                    friendSender = user
                    
            senderFriendsList = db.child("Users").child(friendSender).child("friends").get().val()
            if senderFriendsList == None:
                senderFriendsList = []
            newSenderFriend = db.child("Users").child(userIdValue).get().val()
            senderFriendsList.append({"id": newSenderFriend["id"], "username": newSenderFriend["username"]})
            db.child("Users").child(friendSender).update({"friends": senderFriendsList})
            
            db.child("Users").child(userIdValue).update({"friends": friendsList, "pendingFriendRequests": newPendingFriendRequestsList})
            return {"message": "friend added to friends list from pending friends requests",
                    "friendsList": friendsList,
                    "pendingFriendRequests": newPendingFriendRequestsList,
                    "error": False}
            
        
        #entra in lobby
        #invita alla lobby
        #cambia squadra lobby
        #cambia stato (online, offline)?
        return {"message": "post request failed", "error": True}
    
    def delete(self):
        args = deleteParser.parse_args()
        req = args['req']
        userIdValue = args['userId']
        friendId = args['friendId']
        
        #delete friend input(req, userIdValue, friendId)
        if req == DELETE_FRIEND:
            friendsList = db.child("Users").child(userIdValue).child("friends").get().val()
            newFriendsList = []
            for friend in friendsList:
                if friend["id"] != friendId:
                    newFriendsList.append(friend)
            db.child("Users").child(userIdValue).update({"friends": newFriendsList})
            return {"message": "friend removed", "newFriendsList": newFriendsList, "error": False}
            
        #reject friend request input(req, userIdValue, friendId)
        if req == REJECT_FRIEND_REQUEST:
            pendingFriendRequestsList = db.child("Users").child(userIdValue).child("pendingFriendRequests").get().val()
            newPendingFriendRequestsList = []
            for pendingFriendRequest in pendingFriendRequestsList:
                if pendingFriendRequest["id"] != friendId:
                    newPendingFriendRequestsList.append(pendingFriendRequest)
            db.child("Users").child(userIdValue).update({"pendingFriendRequests": newPendingFriendRequestsList})
            return {"message": "pending friend request removed", "newPendingFriendRequests": newPendingFriendRequestsList, "error": False}
                
                
        
        #delete lobby
        #abbandona lobby
        #accetta invito lobby
        #rimuovi invito lobby
        return {"message": "delete request failed", "error": True}


# Class that contains utility functions
class EnigmaServerUtils():
    
    def checkifAlreadySent(self, userIdValue, friendIdValue):
        userId = db.child("Users").child(userIdValue).get().val()["id"]
        pendingFriendRequestsList = db.child("Users").child(friendIdValue).child("pendingFriendRequests").get().val()
        if pendingFriendRequestsList != None:
            for friendRequest in pendingFriendRequestsList:
                requestUserId = friendRequest["id"]
                if userId == requestUserId:
                    return True
        return False
    
    def checkIfAlreadyAdded(self, userIdValue, friendId):
        friendList = db.child("Users").child(userIdValue).child("friends").get().val()
        if friendList != None:
            for friend in friendList:
                userFriendId = friend["id"]
                if friendId == userFriendId:
                    return True
        return False
    
    def createId(self, id):
        output = ""
        carryover = True
        if id != None:
            for i in range(len(id) - 1, 0, -1):
                c = str(id[i])
                if carryover:
                    n = self.decodeId(c[0]) + 1
                    c = "0"
                    if n < 62:
                        c = self.encodeId(n)
                        carryover = False
                output += c
        output += "#"
        return output[::-1]

    def decodeId(self, c):
        hashcode = ord(c)
        n = 0
        if hashcode in range(48, 58, 1): # numbers
            n = hashcode - 48
    
        if hashcode in range(97, 123, 1): # lowercase
            n = hashcode - 97 + 10
    
        if hashcode in range(65, 91, 1): # uppercase
            n = hashcode - 65 + 10 + 26
        return n
    
    def encodeId(self, n):
        index = 0
        x = ""
        if n < 10: # numbers
            index = n + 48
            x = str(chr(index))
    
        if n in range (10, 10+26, 1): # lowercase
            index = n - 10 + 97
            x = str(chr(index))
    
        if n >= 10 +26: # uppercase
            index = n - (10 + 26) + 65
            x = str(chr(index))
        return x

#Flask
app = Flask(__name__)
api = Api(app)
api.add_resource(EnigmaServer, "/")



if __name__ == '__main__':
    print("Starting api...")
    app.run(host = "0.0.0.0")
    
import random
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

# GET
GET_USERNAME = "get0"
GET_USER_EXIST = "get1"
GET_USER = "get2"
GET_PENDING_REQUESTS = "get3"
GET_SEARCH_FRIEND = "get4"

# PUT
PUT_NEW_USER = "put0"

# POST
POST_CHANGE_NAME = "post0"
POST_ACCEPT_FRIEND_REQUEST = "post1"

# DELETE
DELETE_REMOVE_FRIEND ="delete0"
DELETE_REMOVE_FRIEND_REQUEST ="delete1"

#get parser
parser = reqparse.RequestParser()
parser.add_argument('req', type = str, required = True)
parser.add_argument('userId', type = str, required = False)
parser.add_argument('googleUserId', type = str, required = False)
parser.add_argument('friendId', type = str, required = False)
parser.add_argument('lobbyId', type = str, required = False)
parser.add_argument('newName', type = str, required = False)
parser.add_argument('username', type = str, required = False)

# Api call handler
class EnigmaServer(Resource):
    
    def __init__(self):
        self.serverUtils = EnigmaServerUtils()
        # self.users = db.child("Users").get().val()
        
        args = parser.parse_args()
        self.req = args['req']
        self.userId = args['userId']
        self.googleUserId = args["googleUserId"]
        self.friendId = args['friendId']
        self.lobbyId = args['lobbyId']
        self.newName = args['newName']
        self.username = args['username']
    
    def get(self):       
         
        #0 return the username given the user id. Input(req, userId)
        if self.req == GET_USERNAME:
            username = ""
            if db.child("Users").child(self.userId).get().val() != None:
                username = db.child("Users").child(self.userId).get().val()["username"]
            return {"message": "get username", "username": username, "error": False}
        
        #1 return true if the user already exist, false otherwise. Input(req, googleUserId)
        if self.req == GET_USER_EXIST:
            googleUsersIds = db.child("GoogleUserIds").get().val()
            if googleUsersIds != None:
                for googleUserId in googleUsersIds:
                    if googleUserId == self.googleUserId:
                        return {"message": "user already exist", "exist": True, "error": False}
            return {"message": "user not exist", "exist": False, "error": False}
        
        #2 return al the user information. Input(req, userId)
        if self.req == GET_USER:
            return db.child("Users").child(self.userId).get().val()
        
        #3 return number of pending request. Input(req, userId)
        if self.req == GET_PENDING_REQUESTS:
            pendingRequests = db.child("Users").child(self.userId).child("pendingFriendRequests").get().val()
            return {"requests": pendingRequests}
        
        #4 return if a friend request is sendable. Input(userId, friendId)
        if self.req == GET_SEARCH_FRIEND:
            if self.userId == self.friendId:
                return {"message": "you can't add yourself", "status": "yourself", "error": False}
            pendingFriendList = db.child("Users").child(self.userId).child("friends").get().val()
            friendList = db.child("Users").child(self.userId).child("friends").get().val()
            pendingReceiverFriendList = db.child("Users").child(self.friendId).child("friends").get().val()
            if pendingFriendList == None:
                pendingFriendList = []
            if friendList == None:
                friendList = []
            if pendingReceiverFriendList == None:
                pendingReceiverFriendList = []
            for pending in pendingFriendList:
                if pending["userId"] == self.friendId:
                    return {"message": "user in pending list", "status": "inPending", "error": False}
            for friend in friendList:
                if friend["userId"] == self.friendId:
                    return {"message": "user in friend list", "status": "alreadyAdded", "error": False}
            for pending in pendingReceiverFriendList:
                if pending["userId"] == self.friendId:
                    return {"message": "user request already sent", "status": "alreadySent", "error": False}
            if db.child("Users").child(self.friendId).get().val() == None:
                return {"message": "friend not found", "status": "notFound", "error": False}
            return {"message": "friend found", "status": "found", "error": False}
        
        return {"message": "get request failed", "error": True}
      
    def put(self):
        
        #0 create new user. Input(req, googleUserId, username)
        if self.req == PUT_NEW_USER:
            userId = self.serverUtils.createId()
            username = self.username
            if username == None:
                username = ""
            db.child("Users").child(userId).set({"username": username, "userId": userId})
            db.child("GoogleUserIds").set({self.googleUserId: userId})
            return {"message": "user created", "userId": userId, "error": False}
        
        return {"message": "put request failed", "error": True}
    
    def post(self):
        
        #0 update the username. Input(req, userId, newName)
        if self.req == POST_CHANGE_NAME:
            newName = self.newName
            if newName == None:
                newName = ""
            db.child("Users").child(self.userId).update({"username": newName})
            for user in db.child("Users").get().val():
                if user != self.userId:
                    friends = db.child("Users").child(user).child("friends").get().val()
                    if friends != None:
                        for i in range(len(friends)):
                            if friends[i]["userId"] == self.userId:
                                db.child("Users").child(user).child("friends").child(str(i)).update({"username": newName})
                    requests = db.child("Users").child(user).child("pendingFriendRequests").get().val()
                    if requests != None:
                        for i in range(len(requests)):
                            if requests[i]["userId"] == self.userId:
                                db.child("Users").child(user).child("pendingFriendRequests").child(str(i)).update({"username": newName})
            
            return {"message": "post name changed", "error": False}
        
        #1 accept friend request. Input(req, userId, friendId)
        if self.req == POST_ACCEPT_FRIEND_REQUEST:
            #user that accept
            pendingFriendRequestsList = db.child("Users").child(self.userId).child("pendingFriendRequests").get().val()
            friendsList = db.child("Users").child(self.userId).child("friends").get().val()
            if friendsList == None:
                friendsList = []
            if pendingFriendRequestsList == None:
                pendingFriendRequestsList = []
            newPendingFriendRequestsList = []
            for pendingFriend in pendingFriendRequestsList:
                if pendingFriend["userId"] != self.friendId:
                    newPendingFriendRequestsList.append(pendingFriend)
                else:
                    friendsList.append(pendingFriend)
            db.child("Users").child(self.userId).update({"pendingFriendRequests": newPendingFriendRequestsList})
            db.child("Users").child(self.userId).update({"friends": friendsList})
            #user accepted
            friendsList = db.child("Users").child(self.friendId).child("friends").get().val()
            user = db.child("Users").child(self.userId).get().val()
            if friendsList == None:
                friendsList = []
            friendsList.append({"username": user["username"], "userId": user["userId"]})
            db.child("Users").child(self.friendId).update({"friends": friendsList})
            return {"message": "friend added to friends list from pending friends requests", "error": False}
        
        return {"message": "post request failed", "error": True}
    
    def delete(self):
        
        #0 delete friend. Input(req, userId, friendId)
        if self.req == DELETE_REMOVE_FRIEND:
            #user that remove
            friendsList = db.child("Users").child(self.userId).child("friends").get().val()
            if friendsList == None:
                friendsList = []
            newFriendsList = []
            for friend in friendsList:
                if friend["userId"] != self.friendId:
                    newFriendsList.append(friend)
            db.child("Users").child(self.userId).update({"friends": newFriendsList})
            #user removed
            friendsList = db.child("Users").child(self.friendId).child("friends").get().val()
            if friendsList == None:
                friendsList = []
            newFriendsList = []
            for friend in friendsList:
                if friend["userId"] != self.userId:
                    newFriendsList.append(friend)
            db.child("Users").child(self.friendId).update({"friends": newFriendsList})
            return {"message": "friend removed", "error": False}
    
        #1 delete a friend request. Input(req, userId, friendId)
        if self.req == DELETE_REMOVE_FRIEND_REQUEST:
            pendingList = db.child("Users").child(self.userId).child("pendingFriendRequests").get().val()
            if pendingList == None:
                pendingList = []
            newPendingList= []
            for request in pendingList:
                if request["userId"] != self.friendId:
                    newPendingList.append(request)
            db.child("Users").child(self.userId).update({"pendingFriendRequests": newPendingList})
            return {"message": "friend request removed", "error": False}
        
        return {"message": "delete request failed", "error": True}


# Class that contains utility functions
class EnigmaServerUtils():
    
    def checkifAlreadySent(self, userIdValue, friendIdValue):
        userId = db.child("Users").child(userIdValue).get().val()["id"]
        pendingFriendRequestsList = db.child("Users").child(friendIdValue).child("pendingFriendRequests").get().val()
        myPendingFriendRequestsList = db.child("Users").child(userIdValue).child("pendingFriendRequests").get().val()
        if myPendingFriendRequestsList != None:
            for myPending in myPendingFriendRequestsList:
                if myPending["uid"] == friendIdValue:
                    return 0 # Already in pending
        if pendingFriendRequestsList != None:
            for friendRequest in pendingFriendRequestsList:
                requestUserId = db.child("Users").child(friendRequest["uid"]).get().val()["id"]
                if userId == requestUserId:
                    return 1 # Already sent
        return 2 # Found
    
    def checkIfAlreadyAdded(self, userIdValue, friendId):
        friendList = db.child("Users").child(userIdValue).child("friends").get().val()
        if friendList != None:
            for friend in friendList:
                userFriendId = db.child("Users").child(friend["uid"]).get().val()["id"]
                if friendId == userFriendId:
                    return True
        return False
    
    def createId(self):
        output = ""
        for i in range(7):
            output += self.encodeId(random.randrange(0, 9+26+26))
        if db.child("Users").get().val() != None:
            for user in db.child("Users").get().val():
                if user == output:
                    output = self.createId()
        return output

    # def decodeId(self, c):
    #     hashcode = ord(c)
    #     n = 0
    #     if hashcode in range(48, 58, 1): # numbers
    #         n = hashcode - 48
    
    #     if hashcode in range(97, 123, 1): # lowercase
    #         n = hashcode - 97 + 10
    
    #     if hashcode in range(65, 91, 1): # uppercase
    #         n = hashcode - 65 + 10 + 26
    #     return n
    
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
    
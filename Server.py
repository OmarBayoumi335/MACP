import random
import pyrebase
import time
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
GET_SEARCH_FRIEND = 0
GET_USER_INFORMATION = 1
GET_FRIENDS_LIST = 2
GET_PENDING_FRIENDS_REQUEST = 3
GET_USER_EXIST = 9
GET_NUM_PENDING_FRIENDS_REQUEST = 11
GET_LOBBY_INVITES = 16
GET_NUM_LOBBY_INVITES = 17
GET_LOBBY_USERS = 18

# PUT
PUT_NEW_USER = 10
PUT_NEW_LOBBY = 12

# POST
POST_SEND_FRIEND_REQUEST = 4
POST_CHANGE_NAME = 5
POST_ACCEPT_FRIEND_REQUEST = 7
POST_SEND_LOBBY_INVITE = 13
POST_ENTER_IN_LOBBY = 15

# DELETE
DELETE_REMOVE_FRIEND = 6
DELETE_REJECT_FRIEND_REQUEST = 8
DELETE_LOBBY_INVITE = 14


#get parser
getParser = reqparse.RequestParser()
getParser.add_argument('req', type = int, required = True)
getParser.add_argument('userId', type = str, required = False)
getParser.add_argument('friendId', type = str, required = False)
getParser.add_argument('lobbyId', type = str, required = False)
getParser.add_argument('googleUserId', type = str, required = False)

#post parser
postParser = reqparse.RequestParser()
postParser.add_argument('req', type = int, required = True)
postParser.add_argument('userId', type = str, required = False)
postParser.add_argument('friendId', type = str, required = False)
postParser.add_argument('newName', type = str, required = False)
postParser.add_argument('lobbyId', type = str, required = False)

#put parser
putParser = reqparse.RequestParser()
putParser.add_argument('req', type = int, required = True)
putParser.add_argument('userId', type = str, required = False)
putParser.add_argument('friendId', type = str, required = False)
putParser.add_argument('username', type = str, required = False)
putParser.add_argument('id', type = str, required = False)
putParser.add_argument('lobbyId', type = str, required = False)
putParser.add_argument('googleUserId', type = str, required = False)



#delete parser
deleteParser = reqparse.RequestParser()
deleteParser.add_argument('req', type = int, required = True)
deleteParser.add_argument('userId', type = str, required = False)
deleteParser.add_argument('friendId', type = str, required = False)
deleteParser.add_argument('lobbyId', type = str, required = False)

# Api call handler
class EnigmaServer(Resource):
    
    def __init__(self):
        self.serverUtils = EnigmaServerUtils()
    
    def get(self):    
        args = getParser.parse_args()
        users = db.child("Users").get().val()
        req = args['req']
        userId = args['userId']
        googleUserId = args["googleUserId"]
        friendId = args['friendId']
        lobbyId = args['lobbyId']
        
        #get friends list input(req, userId, friendid)
        if req == GET_SEARCH_FRIEND:
# =============================================================================
#             start = time.time()
#             for i in range(5):
#                 db.child("Users").child("vediamoSeFunge").get().val()
#             end = time.time()
#             
#             start1 = time.time()
#             for i in range(5):
#                 for user in users:
#                     if db.child("Users").child(user).get().val()["id"] == friendId:
#                         a = 1 + 1
#                         break
#             end1 = time.time()
#             
#             return {"metodo1": end - start, "metodo2": end1 - start1}
# =============================================================================
            if db.child("Users").child(userIdValue).get().val()["id"] != friendId:
                for user in users:
                    friend = db.child("Users").child(user).child("id").get().val()
                    if friendId == friend:
                        friendIdValue = user
                        if self.serverUtils.checkIfAlreadyAdded(userIdValue, friendId):
                            return {"message": "friends already added", "status": "alreadyAdded", "error": False}
                        elif self.serverUtils.checkifAlreadySent(userIdValue, friendIdValue) == 1:
                            return {"message": "friend request already sent", "status": "alreadySent", "error": False}
                        elif self.serverUtils.checkifAlreadySent(userIdValue, friendIdValue) == 0:
                            return {"message": "friend in your pending list", "status": "inPending", "error": False}
                        else:
                            return {"message": "friend found", "status": "found", "error": False}
            else:
                return {"message": "can't add yourself", "status": "yourself", "error": False}
            return {"message": "user not found", "status": "notFound", "error": False}
        
        #get user information input(req, userId)
        if req == GET_USER_INFORMATION:
            username = db.child("Users").child(userId).get().val()["username"]
            return {"message": "username and password of the current user", "username": username, "error": False}
        
        #get friends input(req, userId)
        if req == GET_FRIENDS_LIST:
            friendsList = db.child("Users").child(userIdValue).child("friends").get().val()
            if friendsList == None:
                friendsList = []
            outFriendsList = []
            for friend in friendsList:
                friendInfo = db.child("Users").child(friend["uid"]).get().val()
                outFriendsList.append({"id": friendInfo["id"], "username": friendInfo["username"]})
            return {"message": "obtaining friends list of the current user", "friends": outFriendsList, "error": False}
        
        #get pending friend requests list (pending) input(req, userId)
        if req == GET_PENDING_FRIENDS_REQUEST:
            pendingFriendRequests = db.child("Users").child(userIdValue).child("pendingFriendRequests").get().val()
            if pendingFriendRequests == None:
                pendingFriendRequests = []
            outPendingList = []
            for pending in pendingFriendRequests:
                pendingInfo = db.child("Users").child(pending["uid"]).get().val()
                outPendingList.append({"id": pendingInfo["id"], "username": pendingInfo["username"]})
            return{"message": "obtaining pending friend requests of the current user", 
                   "pendingFriendRequests": outPendingList, 
                   "error": False}
        
        #get return if the current user exist input(req, googleUserId)
        if req == GET_USER_EXIST:
            for user in db.child("GoogleUserIds").get().val():
                if user == googleUserId:
                    return{"message": "user already exist", 
                           "exist": True, 
                           "error": False}
            return{"message": "user not exist", 
                   "exist": False, 
                   "error": False}
        
        #get number of pending invites input(req, userId)
        if req == GET_NUM_PENDING_FRIENDS_REQUEST:
            pendingRequests = db.child("Users").child(userIdValue).child("pendingFriendRequests").get().val()
            if pendingRequests == None:
                pendingRequests = []
            return {"message": "number of pending friend requests", "pendingRequestsNum": len(pendingRequests), "error": False}
            
        #get lobby invites input(req, userId)
        if req == GET_LOBBY_INVITES:
            lobbyInvites = db.child("Users").child(userIdValue).child("lobbyInvites").get().val()
            if lobbyInvites == None:
                lobbyInvites = []
            return {"message": "lobby invites", "lobbyInvites": lobbyInvites, "error": False}
        
        #get number of lobby invites input(req, userId)
        if req == GET_NUM_LOBBY_INVITES:
            lobbyInvites = db.child("Users").child(userIdValue).child("lobbyInvites").get().val()
            if lobbyInvites == None:
                lobbyInvites = []
            return {"message": "number of lobby invites", "lobbyInvites": len(lobbyInvites), "error": False}
        
        #get lobby users input(req, lobbyId)
        if req == GET_LOBBY_USERS:
            team1 = db.child("Lobbies").child(lobbyId).child("lobbyUsers").child("team1").get().val()
            team2 = db.child("Lobbies").child(lobbyId).child("lobbyUsers").child("team2").get().val()
            if team2 == None:
                team2 = []
            lobbyUsers = []
            for user in team1:
                lobbyUsers.append(user)    
            lobbyUsers += team2 
            #lobbyUsers = db.child("Lobbies").child(lobbyId).child("lobbyUsers").get().val()
            return {"message": "current users in lobby", "lobbyUsers": lobbyUsers , "team1": team1, "team2": team2 ,"error": False}
        
        return {"message": "get request failed", "error": True}
      
    def put(self):
        args = putParser.parse_args()
        users = db.child("Users").get().val()
        req = args['req']
        #userId = args['userId']
        username = args['username']
        lobbyId = args['lobbyId']
        googleUserId = args["googleUserId"]
        
        #put create new user input(req, googleUserId, username)
        if req == PUT_NEW_USER:
            newId = self.serverUtils.createId()
            db.child("Users").child(newId).set({"username": username})
            db.child("googleUsersIds").set({googleUserId: newId})
            return {"message": "user created", "error": False}
        
        #lobby creation input(req, userId, lobbyId)
        if req == PUT_NEW_LOBBY:
            masterUser = db.child("Users").child(userId).get().val()
            masterUsername = masterUser["username"]
            masterId = masterUser["id"]
            db.child("Lobbies").child(lobbyId).child("roomMaster").set({"username":masterUsername, "id": masterId})
            db.child("Lobbies").child(lobbyId).child("lobbyUsers").child("team1").set([{"username":masterUsername, "id": masterId}])
            lobby = db.child("Lobbies").child(lobbyId).get().val()
            return {"message": "lobby created", "lobby": lobby, "error": False}
        
        return {"message": "put request failed", "error": True}
    
    def post(self):
        args = postParser.parse_args()
        users = db.child("Users").get().val()
        req = args['req']
        userIdValue = args['userId']
        friendId = args['friendId']
        newName = args["newName"]
        lobbyId = args["lobbyId"]
        
        #friend request input(req, userId, friendid)
        if req == POST_SEND_FRIEND_REQUEST:
            for user in users:
                friendIdField = db.child("Users").child(user).get().val()["id"]
                if friendId == friendIdField:
                    newPendingFriendRequestsList = db.child("Users").child(user).child("pendingFriendRequests").get().val()
                    if newPendingFriendRequestsList == None:
                        newPendingFriendRequestsList = []   
                        
                    # newUserPending = db.child("Users").child(userIdValue).get().val()
                    newPendingFriendRequestsList.append({"uid": userIdValue})
                    db.child("Users").child(user).update({"pendingFriendRequests": newPendingFriendRequestsList})
                    
                    pendingFriendRequests = db.child("Users").child(user).child("pendingFriendRequests").get().val()
                    return{"message": "friend request send and added to pending friend requests", 
                           "pendingFriendRequests": pendingFriendRequests, 
                           "error": False}
        
        #change username input(req, userId, newName)
        if req == POST_CHANGE_NAME:
            db.child("Users").child(userIdValue).update({"username": newName})
            return {"message": "username changed", "username": newName, "error": False}
            
        #accept friend request(req, userId, friendId)
        if req == POST_ACCEPT_FRIEND_REQUEST:
            pendingFriendRequestsList = db.child("Users").child(userIdValue).child("pendingFriendRequests").get().val()
            friendsList = db.child("Users").child(userIdValue).child("friends").get().val()
            if friendsList == None:
                friendsList = []
            newPendingFriendRequestsList = []
            for pendingFriend in pendingFriendRequestsList:
                pendingFriendTmp = {"uid": pendingFriend["uid"]}
                if db.child("Users").child(pendingFriend["uid"]).get().val()["id"] == friendId:
                    friendsList.append(pendingFriendTmp)
                    continue
                newPendingFriendRequestsList.append(pendingFriendTmp)
                                
            for user in users:
                if db.child("Users").child(user).get().val()["id"] == friendId:
                    friendSender = user
                    
            senderFriendsList = db.child("Users").child(friendSender).child("friends").get().val()
            if senderFriendsList == None:
                senderFriendsList = []
            # newSenderFriend = db.child("Users").child(userIdValue).get().val()
            senderFriendsList.append({"uid": userIdValue})
            db.child("Users").child(friendSender).update({"friends": senderFriendsList})
            
            db.child("Users").child(userIdValue).update({"friends": friendsList, "pendingFriendRequests": newPendingFriendRequestsList})
            return {"message": "friend added to friends list from pending friends requests",
                    "friendsList": friendsList,
                    "pendingFriendRequests": newPendingFriendRequestsList,
                    "error": False}
            
        #send lobby invite input(req, userId, friendId, lobbyId)
        if req == POST_SEND_LOBBY_INVITE:
            sender = db.child("Users").child(userIdValue).get().val()
            senderName = sender["username"]
            senderId = sender["id"]
            for user in users:
                if db.child("Users").child(user).get().val()["id"] == friendId:
                    receiver = user
            currentlobbyInvites = db.child("Users").child(receiver).child("lobbyInvites").get().val()
            if currentlobbyInvites == None:
                currentlobbyInvites = []
            else:
                for invite in currentlobbyInvites:
                    if invite["lobbyId"] == lobbyId:
                        return {"message": "user already invited", "status": "alreadyInvited", "error": False}
            newLobbyInvites = currentlobbyInvites
            newLobbyInvites.append({"lobbyId": lobbyId, "id": senderId, "username": senderName})
            db.child("Users").child(receiver).update({"lobbyInvites": newLobbyInvites})
            return {"message": "user invited to lobby", "status": "notInvited", "error": False}
        
        #enter into lobby input(req, userId, lobbyId)
        if req == POST_ENTER_IN_LOBBY:
            lobby = db.child("Lobbies").child(lobbyId).get().val()
            userUsername = db.child("Users").child(userIdValue).get().val()["username"]
            userId = db.child("Users").child(userIdValue).get().val()["id"]
            team1Members = db.child("Lobbies").child(lobbyId).child("lobbyUsers").child("team1").get().val()
            team2Members = db.child("Lobbies").child(lobbyId).child("lobbyUsers").child("team2").get().val()
            if team2Members == None:
                team2Members = []
            lessMembers = team1Members
            teamWithLessMembers = "team1"
            if len(team1Members) > len(team2Members):
                lessMembers = team2Members
                teamWithLessMembers = "team2"
            newLobbyUsers = []
            for user in lessMembers:
                newLobbyUsers.append(user)
            newLobbyUsers.append({"username":userUsername, "id":userId })
            db.child("Lobbies").child(lobbyId).child("lobbyUsers").update({teamWithLessMembers: newLobbyUsers})
            return {"message": "connected to lobby", "error": False}

        #cambia squadra lobby
        #cambia stato (online, offline)?
        return {"message": "post request failed", "error": True}
    
    def delete(self):
        args = deleteParser.parse_args()
        req = args['req']
        userIdValue = args['userId']
        friendId = args['friendId']
        lobbyId = args['lobbyId']
        
        
        #delete friend input(req, userIdValue, friendId)
        if req == DELETE_REMOVE_FRIEND:
            friendsList = db.child("Users").child(userIdValue).child("friends").get().val()
            newFriendsList = []
            for friend in friendsList:
                if db.child("Users").child(friend["uid"]).get().val()["id"] != friendId:
                    newFriendsList.append(friend)
            db.child("Users").child(userIdValue).update({"friends": newFriendsList})
            return {"message": "friend removed", "newFriendsList": newFriendsList, "error": False}
            
        #reject friend request input(req, userIdValue, friendId)
        if req == DELETE_REJECT_FRIEND_REQUEST:
            pendingFriendRequestsList = db.child("Users").child(userIdValue).child("pendingFriendRequests").get().val()
            newPendingFriendRequestsList = []
            for pendingFriendRequest in pendingFriendRequestsList:
                if db.child("Users").child(pendingFriendRequest["uid"]).get().val()["id"] != friendId:
                    newPendingFriendRequestsList.append(pendingFriendRequest)
            db.child("Users").child(userIdValue).update({"pendingFriendRequests": newPendingFriendRequestsList})
            return {"message": "pending friend request removed", "newPendingFriendRequests": newPendingFriendRequestsList, "error": False}
        
        #accept lobby invite (req, userId, lobbyId)
        if req == DELETE_LOBBY_INVITE:
            lobbyInvites = db.child("Users").child(userIdValue).child("lobbyInvites").get().val()
            newLobbyInvites = []
            for invite in lobbyInvites:
                if invite["lobbyId"] != lobbyId:
                    newLobbyInvites.append(invite)
            db.child("Users").child(userIdValue).update({"lobbyInvites": newLobbyInvites})
            return{"message": "lobby invite removed", "error": False}
        
        #delete lobby
        #abbandona lobby
        
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
    
    # def createId(self, id):
    #     output = ""
    #     carryover = True
    #     if id != None:
    #         for i in range(len(id) - 1, 0, -1):
    #             c = str(id[i])
    #             if carryover:
    #                 n = self.decodeId(c[0]) + 1
    #                 c = "0"
    #                 if n < 62:
    #                     c = self.encodeId(n)
    #                     carryover = False
    #             output += c
    #     output += "#"
    #     return output[::-1]
    
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
    
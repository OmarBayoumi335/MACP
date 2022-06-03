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
MAX_LOBBY_MEMBERS = 16

# GET
GET_USERNAME = "get0"
GET_USER_EXIST = "get1"
GET_USER = "get2"
GET_SEARCH_FRIEND = "get3"
GET_INVITABLE_USER = "get4"
GET_LOBBY = "get5"

# PUT
PUT_NEW_USER = "put0"
PUT_NEW_LOBBY = "put1"

# POST
POST_CHANGE_NAME = "post0"
POST_ACCEPT_FRIEND_REQUEST = "post1"
POST_SEND_FRIEND_REQUEST = "post2"
POST_SEND_LOBBY_INVITE = "post3"
POST_ACCEPT_LOBBY_INVITE = "post4"
POST_CHANGE_TEAM = "post5"
POST_SEND_MESSAGE = "post6"
POST_PROVA = "prova"

# DELETE
DELETE_REMOVE_FRIEND ="delete0"
DELETE_REMOVE_FRIEND_REQUEST ="delete1"
DELETE_LEAVE_LOBBY = "delete2"
DELETE_LOBBY_INVITE = "delete3"


# parser
parser = reqparse.RequestParser()
parser.add_argument('req', type = str, required = True)
parser.add_argument('userId', type = str, required = False)
parser.add_argument('googleUserId', type = str, required = False)
parser.add_argument('friendId', type = str, required = False)
parser.add_argument('lobbyId', type = str, required = False)
parser.add_argument('lobbyName', type = str, required = False)
parser.add_argument('newName', type = str, required = False)
parser.add_argument('username', type = str, required = False)
parser.add_argument('chatText', type = str, required = False)

# Api call handler
class EnigmaServer(Resource):
    
    def __init__(self):
        self.serverUtils = EnigmaServerUtils()
        # self.users = db.child("Users").get().val()
        
        args = parser.parse_args()
        self.req = args['req']
        self.userId = args['userId']
        self.googleUserId = args['googleUserId']
        self.friendId = args['friendId']
        self.lobbyId = args['lobbyId']
        self.lobbyName = args['lobbyName']
        self.newName = args['newName']
        self.username = args['username']
        self.chatText = args['chatText']
    
    def get(self):       
         
        #0 return the username given the user id. Input(req, userId)
        if self.req == GET_USERNAME:
            userId = "" if self.userId == None else self.userId
            username = ""
            if db.child("Users").child(userId).get().val() != None:
                username = db.child("Users").child(userId).get().val()["username"]
            return {"message": "get username", "username": username, "error": False}
        
        #1 return true if the user already exist, false otherwise. Input(req, googleUserId)
        if self.req == GET_USER_EXIST:
            userId = db.child("GoogleUserIds").child(self.googleUserId).get().val()
            if userId != None:
                return {"message": "user already exist", "exist": True, "userId": userId, "error": False}
            return {"message": "user not exist", "exist": False, "userId": "", "error": False}
        
        #2 return all user information. Input(req, userId)
        if self.req == GET_USER:
            return db.child("Users").child(self.userId).get().val()
        
        #3 return if a friend request is sendable. Input(req, userId, friendId)
        if self.req == GET_SEARCH_FRIEND:
            if self.userId == self.friendId:
                return {"message": "you can't add yourself", "status": "yourself", "error": False}
            pendingFriendList = db.child("Users").child(self.userId).child("pendingFriendRequests").get().val()
            friendList = db.child("Users").child(self.userId).child("friends").get().val()
            pendingReceiverFriendList = db.child("Users").child(self.friendId).child("pendingFriendRequests").get().val()
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
                if pending["userId"] == self.userId:
                    return {"message": "user request already sent", "status": "alreadySent", "error": False}
            if db.child("Users").child(self.friendId).get().val() == None:
                return {"message": "friend not found", "status": "notFound", "error": False}
            return {"message": "friend found", "status": "found", "error": False}
        
        #4 return a list of invitable user. Input(req, userId, lobbyId) 
        if self.req == GET_INVITABLE_USER:
            user = db.child("Users").child(self.userId).get().val()
            friendList = db.child("Users").child(self.userId).child("friends").get().val()
            lobby = db.child("Lobbies").child(self.lobbyId).get().val()
            team1 = db.child("Lobbies").child(self.lobbyId).child("team1").get().val()
            team2 = db.child("Lobbies").child(self.lobbyId).child("team2").get().val()
            team1 = [] if team1 == None else team1
            team2 = [] if team2 == None else team2 
            userFields = {"username": user["username"], "userId": user["userId"], "lobbyId": lobby["lobbyId"], "lobbyName": lobby["lobbyName"]}
            invitableUsers = []
            if friendList != None:
                for friend in friendList:
                    friendPendingRequestList = db.child("Users").child(friend["userId"]).child("pendingInviteRequests").get().val()
                    friendPendingRequestList = [] if friendPendingRequestList == None else friendPendingRequestList
                    if userFields not in friendPendingRequestList:
                        invitableUsers.append({"username":friend["username"], "userId": friend["userId"], "status": "invitable"})
                    else:
                        invitableUsers.append({"username":friend["username"], "userId": friend["userId"], "status": "alreadyInvited"})
                for user in invitableUsers:
                    if user["status"] == "invitable":
                        checkUser = {"username": user["username"], "userId": user["userId"]}
                        if checkUser in team1 or checkUser in team2:
                            user["status"] = "inLobby"
            return {"message":"list of invitable users", "userInvitableList": {"userList": invitableUsers}, "error": False}
        
        #5 return all lobby information. Input(req, lobbyId)
        if self.req == GET_LOBBY:
            lobby = db.child("Lobbies").child(self.lobbyId).get().val()
            chat = db.child("Lobbies").child(self.lobbyId).child("chat").get().val()
            chat = [] if chat == None else chat
            lobby["chat"] = chat
            return lobby
        
        return {"message": "get request failed", "error": True}
        
      
    def put(self):
        
        #0 create new user. Input(req, googleUserId, username)
        if self.req == PUT_NEW_USER:
            userId = self.serverUtils.createId()
            username = self.username
            if username == None:
                username = ""
            db.child("Users").child(userId).set({"username": username, "userId": userId})
            db.child("GoogleUserIds").update({self.googleUserId: userId})
            return {"message": "user created", "userId": userId, "error": False}

        #1 create new lobby. Input(req, userId, lobbyName)
        if self.req == PUT_NEW_LOBBY:
            lobbyId = self.serverUtils.createIdLobby()
            user = db.child("Users").child(self.userId).get().val()
            lobby = {"lobbyId": lobbyId,
                        "lobbyName": self.lobbyName,
                        "team1": [{"username": user["username"],
                                   "userId": user["userId"]}],
                        "team2": [],
                        "chat": []}
         
            db.child("Lobbies").child(lobbyId).set(lobby)
            return {"message": "lobby created", "lobby": lobby, "error": False}
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
                    invites = db.child("Users").child(user).child("pendingInviteRequests").get().val()
                    if invites != None:
                        for i in range(len(invites)):
                            if invites[i]["userId"] == self.userId:
                                db.child("Users").child(user).child("pendingInviteRequests").child(str(i)).update({"username": newName})
                    lobbies = db.child("Lobbies").get().val()
                    if lobbies != None:
                        for lobby in lobbies:
                            team1 = db.child("Lobbies").child(lobby).child("team1").get().val()
                            team1 = [] if team1 == None else team1
                            team2 = db.child("Lobbies").child(lobby).child("team2").get().val()
                            team2 = [] if team2 == None else team2
                            for i in range(len(team1)):
                                if team1[i]["userId"] == self.userId:
                                    db.child("Lobbies").child(lobby).child("team1").child(str(i)).update({"username": newName})
                            for i in range(len(team2)):
                                if team2[i]["userId"] == self.userId:
                                    db.child("Lobbies").child(lobby).child("team2").child(str(i)).update({"username": newName})
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
        
        #2 send a friend request. Input(req, userId, friendId)
        if self.req == POST_SEND_FRIEND_REQUEST:
            pendingReceiverFriendRequestsList = db.child("Users").child(self.friendId).child("pendingFriendRequests").get().val()
            sender = db.child("Users").child(self.userId).get().val()
            if pendingReceiverFriendRequestsList == None:
                pendingReceiverFriendRequestsList = []
            pendingReceiverFriendRequestsList.append({"userId": sender["userId"], "username": sender["username"]})
            db.child("Users").child(self.friendId).update({"pendingFriendRequests": pendingReceiverFriendRequestsList})
            return{"message": "friend request sent and added to pending friend requests", "error": False}
        
        #3 send invite to lobby. Input(req, userId, username, friendId, lobbyId, lobbyName)
        if self.req == POST_SEND_LOBBY_INVITE:
            team1 = db.child("Lobbies").child(self.lobbyId).child("team1").get().val()
            team1 = [] if team1 == None else team1
            team2 = db.child("Lobbies").child(self.lobbyId).child("team2").get().val()
            team2 = [] if team2 == None else team2
            if len(team1) + len(team2) >= MAX_LOBBY_MEMBERS:
                return {"message": "lobby full", "status": "lobbyFull", "error": False}
            username = self.username
            if username == None:
                username = ""
            friendCheckInvited = {"username": username, "userId": self.userId, "lobbyId": self.lobbyId, "lobbyName": self.lobbyName}
            friendPendingInviteRequests = db.child("Users").child(self.friendId).child("pendingInviteRequests").get().val()
            if friendPendingInviteRequests == None:
                friendPendingInviteRequests = []
            friendPendingInviteRequests.append(friendCheckInvited)
            db.child("Users").child(self.friendId).update({"pendingInviteRequests":friendPendingInviteRequests})
            return {"message": "user invited", "status": "invited", "error": False}
            
        #4 accept lobby invite and enter into it. Input(req, userId, lobbyId)
        if self.req == POST_ACCEPT_LOBBY_INVITE:
            pendingInviteRequests = db.child("Users").child(self.userId).child("pendingInviteRequests").get().val()
            user = db.child("Users").child(self.userId).get().val()
            userValue = {"username": user["username"], "userId": user["userId"]}
            team1Members = db.child("Lobbies").child(self.lobbyId).child("team1").get().val()
            team2Members = db.child("Lobbies").child(self.lobbyId).child("team2").get().val()
            team1Members = [] if team1Members == None else team1Members
            team2Members = [] if team2Members == None else team2Members
            
            if len(team1Members) + len(team2Members) >= MAX_LOBBY_MEMBERS:
                return {"message": "lobby full", "error": False}
            
            if len(team1Members) > len(team2Members):
                team2Members.append(userValue)
                db.child("Lobbies").child(self.lobbyId).update({"team2": team2Members})
            else:
                team1Members.append(userValue)
                db.child("Lobbies").child(self.lobbyId).update({"team1": team1Members})
           
            newPending = []
            for pending in pendingInviteRequests:
                if pending["lobbyId"] != self.lobbyId:
                    newPending.append(pending)
            db.child("Users").child(self.userId).update({"pendingInviteRequests": newPending})
            lobby = db.child("Lobbies").child(self.lobbyId).get().val()
            chat = db.child("Lobbies").child(self.lobbyId).child("chat").get().val()
            chat = [] if chat == None else chat
            lobby["chat"] = chat
            return {"message": "connected to lobby", "lobby":lobby, "error": False}
        
        #5 change team. Input(req, userId, lobbyId)
        if self.req == POST_CHANGE_TEAM:
            user = db.child("Users").child(self.userId).get().val()
            username = "" if user["username"] == None else user["username"]
            userFields = {"username": user["username"], "userId": user["userId"]}
            team1 = db.child("Lobbies").child(self.lobbyId).child("team1").get().val()
            team1 = [] if team1 == None else team1
            team2 = db.child("Lobbies").child(self.lobbyId).child("team2").get().val()
            team2 = [] if team2 == None else team2
            if userFields in team1 and len(team2) < MAX_LOBBY_MEMBERS/2:
                team1.remove(userFields)
                team2.append(userFields)
            elif userFields in team2 and len(team1) < MAX_LOBBY_MEMBERS/2:
                team1.append(userFields)
                team2.remove(userFields)
            else:
                return{"message": "the other team is full", "status": "fullTeam", "error": False}
            db.child("Lobbies").child(self.lobbyId).update({"team1": team1})
            db.child("Lobbies").child(self.lobbyId).update({"team2": team2})
            return{"message": "team changed", "status": "notFullTeam", "error": False}
        
        #6 send message to display in the chat. Input(req, userId, lobbyId, username, chatText)
        if self.req == POST_SEND_MESSAGE:
            username = "" if self.username == None else self.username
            chat = db.child("Lobbies").child(self.lobbyId).child("chat").get().val()
            chat = [] if chat == None else chat
            message = {"user":{"userId": self.userId, "username": username}, "text": self.chatText}
            chat.append(message)
            db.child("Lobbies").child(self.lobbyId).update({"chat": chat})
            return {"message": "message sent", "error": False}
        
        if self.req == POST_PROVA:
            team1 = db.child("Lobbies").child(self.lobbyId).child("team1").get().val()
            for i in range(7):
                obj = {"userId":self.userId + str(i), "username": self.username + str(i)}
                team1.append(obj)
            db.child("Lobbies").child(self.lobbyId).update({"team1": team1})
            return "ok"
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
        
        #2 user leaves lobby and if there are no users the lobby will be removed. Input(req, userId, lobbyId)
        if self.req == DELETE_LEAVE_LOBBY:
            inTeam1 = False
            team1 = db.child("Lobbies").child(self.lobbyId).child("team1").get().val()
            team2 = db.child("Lobbies").child(self.lobbyId).child("team2").get().val()
            chat = db.child("Lobbies").child(self.lobbyId).child("chat").get().val()
            if team1 == None:
                team1 = []
            if team2 == None:
                team2 = []
            if chat == None:
                chat = []
            #user leaves lobby
            for user in team1:
                if user["userId"] == self.userId:
                    inTeam1 = True
                    team1.remove(user)
                    break 
            if not inTeam1:
                for user in team2:
                    if user["userId"] == self.userId:
                        team2.remove(user)
                        break
       
            #last user leaves lobby
            if len(team1) + len(team2) == 0:
                
                db.child("Lobbies").child(self.lobbyId).remove()
                return {"message": "lobby deleted", "error": False}
            
            db.child("Lobbies").child(self.lobbyId).update({"team1": team1})
            db.child("Lobbies").child(self.lobbyId).update({"team2": team2})
            return{"message": "user leave lobby", "error": False}
        
        #3 user decline a lobby invite. Input(req, userId, lobbyId)
        if self.req == DELETE_LOBBY_INVITE:
            pendingInviteRequests = db.child("Users").child(self.userId).child("pendingInviteRequests").get().val()
            pendingInviteRequests = [] if pendingInviteRequests == None else pendingInviteRequests
            newPendingInviteRequests = []
            for pending in pendingInviteRequests:
                if pending["lobbyId"] != self.lobbyId:
                    newPendingInviteRequests.append(pending)
            db.child("Users").child(self.userId).update({"pendingInviteRequests":newPendingInviteRequests})
            return{"message": "invites removed", "error": False}
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
    
    def createIdLobby(self):
        output = ""
        for i in range(7):
            output += self.encodeId(random.randrange(0, 9+26+26))
        if db.child("Lobbies").get().val() != None:
            for lobby in db.child("Lobbies").get().val():
                if lobby == output:
                    output = self.createIdLobby()
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
    
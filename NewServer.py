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
MIN_START_MEMBER = 1
MAX_HINT = 3
TEAM_RED = "Team Red"
TEAM_GEEN = "Team Green"

# GET
GET_USERNAME = "get0"
GET_USER_EXIST = "get1"
GET_USER = "get2"
GET_SEARCH_FRIEND = "get3"
GET_INVITABLE_USER = "get4"
GET_GAME_LOBBY_NUMBER_OF_MEMBERS = "get5"
GET_ALL_READY_GAME = "get6"
GET_GAME_INFORMATION = "get7"

# PUT
PUT_NEW_USER = "put0"
PUT_NEW_LOBBY = "put1"
PUT_NEW_GAME_LOBBY = "put2"

# POST
POST_CHANGE_NAME = "post0"
POST_ACCEPT_FRIEND_REQUEST = "post1"
POST_SEND_FRIEND_REQUEST = "post2"
POST_SEND_LOBBY_INVITE = "post3"
POST_ACCEPT_LOBBY_INVITE = "post4"
POST_CHANGE_TEAM = "post5"
POST_SEND_MESSAGE = "post6"
POST_CHANGE_READY_STATUS = "post7"
POST_JOIN_GAME_LOBBY = "post8"
POST_SEND_CLUE = "post9"
POST_VOTE = "post10"
POST_READY = "post11"
POST_SEND_MESSAGE_GAMELOBBY = "post12"
POST_PROVA = "prova"

# DELETE
DELETE_REMOVE_FRIEND ="delete0"
DELETE_REMOVE_FRIEND_REQUEST ="delete1"
DELETE_LEAVE_LOBBY = "delete2"
DELETE_LOBBY_INVITE = "delete3"
DELETE_LOBBY = "delete4"
DELETE_LEAVE_GAMELOBBY = "delete5"


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
parser.add_argument('team', type = str, required = False)
parser.add_argument('words', type = str, required = False)
parser.add_argument('turn', type = str, required = False)
parser.add_argument('gameLobbyId', type = str, required = False)
parser.add_argument('captainIndex1', type = str, required = False)
parser.add_argument('captainIndex2', type = str, required = False)
parser.add_argument('clue', type = str, required = False)
parser.add_argument('voteIndex', type = str, required = False)

# Api call handler
class EnigmaServer(Resource):
    
    def __init__(self):
        args = parser.parse_args()
        self.serverUtils = EnigmaServerUtils()
        self.req = args['req']
        self.userId = args['userId']
        self.googleUserId = args['googleUserId']
        self.friendId = args['friendId']
        self.lobbyId = args['lobbyId']
        self.lobbyName = args['lobbyName']
        self.newName = args['newName']
        self.username = args['username']
        self.chatText = args['chatText']
        self.team = args["team"]
        self.words = args["words"]
        self.turn = args["turn"]
        self.gameLobbyId = args["gameLobbyId"]
        self.captainIndex1 = args["captainIndex1"]
        self.captainIndex2 = args["captainIndex2"]
        self.clue = args["clue"]
        self.voteIndex = args["voteIndex"]
    
    def get(self):       
         
        #0 return the username given the user id. Input(req, userId)
        if self.req == GET_USERNAME:
            userId = "" if self.userId == None else self.userId
            username = db.child("Users").child(userId).child("username").get().val()
            username = "" if username == None else username
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
                        checkUser1 = {"username": user["username"], "userId": user["userId"], "ready": False}
                        checkUser2 = {"username": user["username"], "userId": user["userId"], "ready": True}
                        if checkUser1 in team1 or checkUser1 in team2 or checkUser2 in team1 or checkUser2 in team2:
                            user["status"] = "inLobby"
            return {"message":"list of invitable users", "userInvitableList": {"userList": invitableUsers}, "error": False}
        
        #5 return the number of member joined in game. Input(req, gameLobbyId)
        if self.req == GET_GAME_LOBBY_NUMBER_OF_MEMBERS:
            members = db.child("GameLobbies").child(self.gameLobbyId).child("members").get().val()
            members = [] if members == None else members
            return {"message": "number of members", "number": len(members), "error": False}
        
        #6 return true if all member are joined, false otherwise. Input(req, gameLobbyId)
        if self.req == GET_ALL_READY_GAME:
            members = db.child("GameLobbies").child(self.gameLobbyId).child("members").get().val()
            for member in members:
                if member["ready"] == False:
                    return {"message": "not everyone is ready to start", "allReady": False, "error": False}
            return {"message": "all are ready to start", "allReady": True, "error": False}
        
        #7 returns information on: game lobby and the 'user game' that called this API. Input(req, userId, gameLobbyId)
        if self.req == GET_GAME_INFORMATION:
            gameLobby = db.child("GameLobbies").child(self.gameLobbyId).get().val()
            userGame = {}
            for member in gameLobby["members"]:
                if member["userId"] == self.userId:
                    userGame = member
                    break
            return {"message": "returned the game lobby and user game informations", 
                    "userGame": userGame, 
                    "gameLobby": gameLobby, 
                    "error": False}
        
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
                                "userId": user["userId"],
                                "ready": False}],
                     "team2": [],
                     "chat": [],
                     "start": False}         
            db.child("Lobbies").child(lobbyId).set(lobby)
            return {"message": "lobby created", "lobby": lobby, "error": False}
        
        #2 create new lobby for the game. Input(req, gameLobbyId, words, turn, captainIndex1, captainIndex2)
        if self.req == PUT_NEW_GAME_LOBBY:
            words = []
            for word in self.words.split("--"):
                turned = True if word.split("_")[3].lower() == "true" else False
                words.append(
                    {"text": word.split("_")[0], 
                     "color": word.split("_")[1], 
                     "direction": word.split("_")[2], 
                     "turned": turned})
            gameLobby = {"lobbyId": self.gameLobbyId, 
                         "members": [],
                         "chatTeam1": [],
                         "chatTeam2": [],
                         "turn": self.turn,
                         "turnPhase": 0,
                         "words": words,
                         "captainIndex1": self.captainIndex1,
                         "captainIndex2": self.captainIndex2,
                         "hint1": MAX_HINT,
                         "hint2": MAX_HINT,
                         "clue": {"text": "", "number": 0, "directions": []},
                         "winner": "no"}
            db.child("GameLobbies").child(self.gameLobbyId).set(gameLobby)
            return {"message": "new game lobby created", "error": False}
            
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
            pendingInviteRequests = [] if pendingInviteRequests == None else pendingInviteRequests
            user = db.child("Users").child(self.userId).get().val()
            userValue = {"username": user["username"], "userId": user["userId"], "ready": False}
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
            team1 = db.child("Lobbies").child(self.lobbyId).child("team1").get().val()
            team1 = [] if team1 == None else team1
            team2 = db.child("Lobbies").child(self.lobbyId).child("team2").get().val()
            team2 = [] if team2 == None else team2
            inTeam1 = False
            for member in team1:
                if member["userId"] == self.userId:
                    if len(team2) < MAX_LOBBY_MEMBERS/2:
                        team1.remove(member)
                        team2.append(member)
                        inTeam1 = True
                        break
                    else:
                        return{"message": "the other team is full", "status": "fullTeam", "error": False}
            if not inTeam1:
                for member in team2:
                    if member["userId"] == self.userId:
                        if len(team1) < MAX_LOBBY_MEMBERS/2:
                            team2.remove(member)
                            team1.append(member)
                            break
                        else:
                            return{"message": "the other team is full", "status": "fullTeam", "error": False}
            teams = team1 + team2
            canStart = True
            for member in teams:
                if not member["ready"]:
                    canStart = False
                    break
            db.child("Lobbies").child(self.lobbyId).update({"team1": team1})
            db.child("Lobbies").child(self.lobbyId).update({"team2": team2})
            if canStart and len(team1) >= MIN_START_MEMBER and len(team2) >= MIN_START_MEMBER:
                db.child("Lobbies").child(self.lobbyId).update({"start": True})
            return{"message": "team changed", "status": "notFullTeam", "start": canStart, "error": False}
        
        #6 send message to display in the chat. Input(req, userId, lobbyId, username, chatText)
        if self.req == POST_SEND_MESSAGE:
            username = "" if self.username == None else self.username
            chat = db.child("Lobbies").child(self.lobbyId).child("chat").get().val()
            chat = [] if chat == None else chat
            message = {"user":{"userId": self.userId, "username": username}, "text": self.chatText}
            chat.append(message)
            db.child("Lobbies").child(self.lobbyId).update({"chat": chat})
            return {"message": "message sent", "error": False}
        
        #7 change the ready status of a user. Input(req, userId, lobbyId)
        if self.req == POST_CHANGE_READY_STATUS:
            user = db.child("Users").child(self.userId).get().val()
            username = "" if user["username"] == None else user["username"]
            team1 = db.child("Lobbies").child(self.lobbyId).child("team1").get().val()
            team1 = [] if team1 == None else team1
            team2 = db.child("Lobbies").child(self.lobbyId).child("team2").get().val()
            team2 = [] if team2 == None else team2
            inTeam1 = False
            for i in range(len(team1)):
                if team1[i]["userId"] == self.userId:
                    inTeam1 = True
                    team1[i]["ready"] = not team1[i]["ready"]
                    db.child("Lobbies").child(self.lobbyId).child("team1").child(str(i)).update({"ready": team1[i]["ready"]})
                    break
            if not inTeam1:
                for i in range(len(team2)):
                    if team2[i]["userId"] == self.userId:
                        team2[i]["ready"] = not team2[i]["ready"]
                        db.child("Lobbies").child(self.lobbyId).child("team2").child(str(i)).update({"ready": team2[i]["ready"]})
                        break
            teams = team1 + team2
            for member in teams:
                if not member["ready"]:
                    return {"message": "ready status changed", "start": False, "error": False}
            if len(team1) >= MIN_START_MEMBER and len(team2) >= MIN_START_MEMBER:
                db.child("Lobbies").child(self.lobbyId).update({"start": True})
                return {"message": "all are ready", "start": True, "error": False}
            return {"message": "all are ready but not enough users", "start": False, "error": False}
            
        #8 join the game lobby. Input(req, userId, team, gameLobbyId)
        if self.req == POST_JOIN_GAME_LOBBY:
            user = db.child("Users").child(self.userId).get().val()
            userGame = {"userId": self.userId,
                        "username": user["username"],
                        "team": self.team,
                        "ready": False,
                        "vote": 100}
            members = db.child("GameLobbies").child(self.gameLobbyId).child("members").get().val()
            members = [] if members == None else members
            members.append(userGame)
            db.child("GameLobbies").child(self.gameLobbyId).update({"members": members})
            return {"message": "user joined in the game lobby", "error": False}
        
        #9 captain send clue. Input(req, userId, gamelobby, clue)
        if self.req == POST_SEND_CLUE:
            clue = db.child("GameLobbies").child(self.gameLobbyId).child("clue").get().val()
            clue = "" if db.child("GameLobbies").child(self.gameLobbyId).child("clue").get().val() == None else clue
            captainteam1 = db.child("GameLobbies").child(self.gameLobbyId).child("captainIndex1").get().val()
            listClue = self.clue.split("-")
            clue["text"] = listClue[0].upper()
            clue["number"] = int(listClue[1])
            directionsList =  [] if listClue[2] == "[]" else listClue[2].split(",")
            newDirectionsList = []
            for direction in directionsList:
                if direction[0] == "[" or direction[0] == " ":
                  direction = direction[1:]
                if direction[-1] == "]":
                  direction = direction[:-1]
                newDirectionsList.append(direction)
            clue["directions"] = newDirectionsList
            db.child("GameLobbies").child(self.gameLobbyId).update({"clue": clue})
            if captainteam1 == self.userId:
                currentHint = db.child("GameLobbies").child(self.gameLobbyId).child("hint1").get().val()
                newHint = currentHint - len(directionsList)
                db.child("GameLobbies").child(self.gameLobbyId).update({"hint1": newHint})
            else:
                currentHint = db.child("GameLobbies").child(self.gameLobbyId).child("hint2").get().val()
                newHint = currentHint - len(directionsList)
                db.child("GameLobbies").child(self.gameLobbyId).update({"hint2": newHint})
            db.child("GameLobbies").child(self.gameLobbyId).update({"turnPhase": 1})
            return {"message": "clue sended", "error": False}
            
        #10 vote a card or pass. Input(req, userId, gameLobbyId, voteIndex, clue, team)
        if self.req == POST_VOTE:
            members = db.child("GameLobbies").child(self.gameLobbyId).child("members").get().val()
            turnPhase = db.child("GameLobbies").child(self.gameLobbyId).child("turnPhase").get().val()
            captainIndex1 = db.child("GameLobbies").child(self.gameLobbyId).child("captainIndex1").get().val()
            captainIndex2 = db.child("GameLobbies").child(self.gameLobbyId).child("captainIndex2").get().val()
            for i, member in enumerate(members):
                if member["userId"] == self.userId:
                    member["vote"] = int(self.voteIndex)
                    db.child("GameLobbies").child(self.gameLobbyId).child("members").child(str(i)).update({"vote": int(self.voteIndex)})
                    break
            for member in members:
                if (member["vote"] == 100 and self.team == member["team"] and member["userId"] != captainIndex1 and member["userId"] != captainIndex2):
                    return {"message": "vote inserted", "error": False}
            votes = [0]*17
            for member in members:
                if (self.team == member["team"] and member["userId"] != captainIndex1 and member["userId"] != captainIndex2):
                    votes[member["vote"]] += 1
            passVotes = votes[-1]
            otherVotes = sum(votes[:-1])
            if passVotes > otherVotes:
                cardToTurn = 16
            else:
                cardToTurn = votes.index(max(votes[:-1]))
            changeTurn = False
            if cardToTurn != 16:
                colorCard = db.child("GameLobbies").child(self.gameLobbyId).child("words").child(str(cardToTurn)).child("color").get().val()
                db.child("GameLobbies").child(self.gameLobbyId).child("words").child(str(cardToTurn)).update({"turned": True})
                if colorCard == "black":
                    winner = "Team Red" if self.team == "Team Green" else "Team Green"
                    db.child("GameLobbies").child(self.gameLobbyId).update({"winner": winner})
                    return {"message": "game ended", "error": False}
                else:
                    if self.serverUtils.checkWinner(self.gameLobbyId):
                        return {"message": "game ended", "error": False}
                    if colorCard == "gray":
                        changeTurn = True
                    elif colorCard == "red":
                        changeTurn = True if self.team == "Team Green" else False
                    elif colorCard == "green":
                        changeTurn = True if self.team == "Team Red" else False
            else: 
                changeTurn = True
            if int(self.clue.split("-")[1]) > int(turnPhase) and not changeTurn:
                db.child("GameLobbies").child(self.gameLobbyId).update({"turnPhase": turnPhase + 1})
            else:
                turn = db.child("GameLobbies").child(self.gameLobbyId).child("turn").get().val()
                newTurn = "Team Red" if turn == "Team Green" else "Team Green"
                db.child("GameLobbies").child(self.gameLobbyId).update({"turn": newTurn})
                db.child("GameLobbies").child(self.gameLobbyId).update({"turnPhase": 0})
            for i, member in enumerate(members):
                if (self.team == member["team"]):
                    db.child("GameLobbies").child(self.gameLobbyId).child("members").child(str(i)).update({"vote": 100})            
            return {"message": "vote inserted and turn/phase changed", "error": False}
        
        #11 set ready for the game. Input(req, userId, gameLobbyId)
        if self.req == POST_READY:
            members = db.child("GameLobbies").child(self.gameLobbyId).child("members").get().val()
            me = 0
            for i, member in enumerate(members):
                if member["userId"] == self.userId:
                    me = i
                    break
            db.child("GameLobbies").child(self.gameLobbyId).child("members").child(str(me)).update({"ready": True})
            return {"message": "ready status changed", "error": False}
        
        
        #12 send message to display in the gamelobby chat. Input(req, userId, gameLobbyId, username, chatText)
        if self.req == POST_SEND_MESSAGE_GAMELOBBY:
            usersGameLobby = db.child("GameLobbies").child(self.gameLobbyId).child("members").get().val()
            user = {}
            for userGame in usersGameLobby:
                if userGame["userId"] == self.userId:
                    user = userGame
            username = "" if self.username == None else self.username
            chatTeam1 = db.child("GameLobbies").child(self.gameLobbyId).child("chatTeam1").get().val()
            chatTeam2 = db.child("GameLobbies").child(self.gameLobbyId).child("chatTeam2").get().val()
            chatTeam1 = [] if chatTeam1 == None else chatTeam1
            chatTeam2 = [] if chatTeam2 == None else chatTeam2
            message = {"user":{"userId": self.userId, "username": username}, "text": self.chatText}
            if user["team"] == "Team Green":
                chatTeam1.append(message)
                db.child("GameLobbies").child(self.gameLobbyId).update({"chatTeam1": chatTeam1})
            else:
                chatTeam2.append(message)
                db.child("GameLobbies").child(self.gameLobbyId).update({"chatTeam2": chatTeam2})
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
                users = db.child("Users").get().val()
                for user in users:
                    pendingInvites = db.child("Users").child(user).child("pendingInviteRequests").get().val()
                    pendingInvites = [] if pendingInvites == None else pendingInvites
                    newPendingList = []
                    for pending in pendingInvites:
                        if self.lobbyId != pending["lobbyId"]:
                            newPendingList.append(pending)
                    db.child("Users").child(user).update({"pendingInviteRequests": newPendingList})
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
        
        #4 delete the party lobby and set ready for the game lobby. Input(req, lobbyId)
        if self.req == DELETE_LOBBY:
            db.child("Lobbies").child(self.lobbyId).remove()
            return {"message": "party lobby deleted", "error": False}
        
        #5 user leaves lobby and if there are no users the lobby will be removed. Input(req, userId, gameLobbyId)
        if self.req == DELETE_LEAVE_GAMELOBBY:
            members = db.child("GameLobbies").child(self.gameLobbyId).child("members").get().val()
            usergame = {}
            for member in members:
                if member["userId"] == self.userId:
                    usergame = member
                    break
            winner = db.child("GameLobbies").child(self.gameLobbyId).child("winner").get().val()
            captainIndex1 = db.child("GameLobbies").child(self.gameLobbyId).child("captainIndex1").get().val()
            captainIndex2 = db.child("GameLobbies").child(self.gameLobbyId).child("captainIndex2").get().val()
            if self.userId == captainIndex1:
                for member in members:
                    if member["userId"] != usergame["userId"] and member["team"] == usergame["team"]:
                        db.child("GameLobbies").child(self.gameLobbyId).update({"captainIndex1": member["userId"]})
                        break
            if self.userId == captainIndex2:
                for member in members:
                    if member["userId"] != usergame["userId"] and member["team"] == usergame["team"]:
                        db.child("GameLobbies").child(self.gameLobbyId).update({"captainIndex2": member["userId"]})
                        break
            members.remove(usergame)
            db.child("GameLobbies").child(self.gameLobbyId).update({"members": members})
            
            if len(members) == 0 and winner != "no":
                db.child("GameLobbies").child(self.gameLobbyId).remove()
                return {"message": "game lobby deleted", "winner": True, "error": False}
            
            if len(members) == 0 and winner == "no":
                db.child("GameLobbies").child(self.gameLobbyId).remove()
                return {"message": "game lobby deleted", "winner": False, "error": False}
            
            return {"message": "user left gamelobby", "error": False}
        return {"message": "delete request failed", "error": True}


# Class that contains utility functions
class EnigmaServerUtils():
    
    def checkWinner(self, gameLobbyId):
        cards = db.child("GameLobbies").child(gameLobbyId).child("words").get().val()
        green = 0
        red = 0
        for card in cards:
            if card["color"] == "green" and card["turned"]:
                green += 1
            if card["color"] == "red" and card["turned"]:
                red += 1
        if green == 6:
            db.child("GameLobbies").child(gameLobbyId).update({"winner": "Team Green"})
            return True
        if red == 6:
            db.child("GameLobbies").child(gameLobbyId).update({"winner": "Team Red"})
            return True
        return False
    
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
    
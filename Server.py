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

# Api call handler
class EnigmaServer(Resource):
    def get(self):
        return {"Messaggio": "Mocc a mammt", "Users": db.child("Users").get().val()}

#Flask
app = Flask(__name__)
api = Api(app)
api.add_resource(EnigmaServer, "/")

parser = reqparse.RequestParser()

if __name__ == '__main__':
    print("Starting api...")
    app.run(host = "0.0.0.0")
    
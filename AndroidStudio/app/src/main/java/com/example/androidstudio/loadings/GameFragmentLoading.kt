package com.example.androidstudio.loadings

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.androidstudio.R
import com.example.androidstudio.classes.types.*
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.classes.utils.ServerHandler
import com.example.androidstudio.game.GameActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.io.IOException
import kotlin.properties.Delegates

class GameFragmentLoading : Fragment() {

    private lateinit var serverHandler: ServerHandler
    private var starter by Delegates.notNull<Boolean>()
    private lateinit var gameLobbyId: String
    private lateinit var userId: String
    private lateinit var lobbyId: String
    private var lobbyGameMembers by Delegates.notNull<Int>()
    private lateinit var captainIndex1: String
    private lateinit var captainIndex2: String
    private lateinit var myTeam: String
    private lateinit var words: MutableList<Word>
    private lateinit var lobby: Lobby

    private lateinit var user: User
    private lateinit var gameLobby: GameLobby

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_game_loading, container, false)
//        findNavController().navigate(R.id.action_gameFragmentLoading_to_gameFragment)
//        val gameActivity: GameActivity = requireActivity() as GameActivity
//        gameActivity.setViewVisible()
        serverHandler = ServerHandler(requireContext())

        val lobbyString = requireActivity().intent.extras?.getString("lobby")!!
        val gson = Gson()
        lobby = gson.fromJson(lobbyString, Lobby::class.java)

        // If i click ready last
        starter = requireActivity().intent.extras?.getBoolean("starter")!!

        // Game lobby id
        gameLobbyId = requireActivity().intent.extras?.getString("gameLobbyId")!!
        var gameLobbyIds = gameLobbyId.split("-")
        gameLobbyIds = gameLobbyIds.sorted()
        gameLobbyId = gameLobbyIds.joinToString("")

        // User ID
        userId = requireActivity().intent.extras?.getString("userId")!!

        // Lobby ID
        lobbyId = requireActivity().intent.extras?.getString("lobbyId")!!

        // Number of players
        lobbyGameMembers = requireActivity().intent.extras?.getInt("lobbyGameMembers")!!
        captainIndex1 = requireActivity().intent.extras?.getString("captainIndex1")!!
        captainIndex2 = requireActivity().intent.extras?.getString("captainIndex2")!!

        // My team
        myTeam = requireActivity().intent.extras?.getString("team")!!

        // Words in game
        words = getWords(requireContext())
//        Log.i(Config.LOADING_GAME_TAG, words.toString())

        joinLobbyGame("getUser")
        return rootView
    }

    private fun getWords(context: Context): MutableList<Word> {
        lateinit var jsonString: String
        try {
            jsonString = context.assets.open("namesForTest.json")
                .bufferedReader()
                .use { it.readText() }
        } catch (ioException: IOException) {
            Log.i(Config.LOADING_GAME_TAG, ioException.toString())
        }
        val listOfWords = object : TypeToken<MutableList<String>>() {}.type

        var allWords: MutableList<String> = Gson().fromJson(jsonString, listOfWords)
        allWords = allWords.shuffled().toMutableList()

        val directions = mutableListOf(
            resources.getString(R.string.north),
            resources.getString(R.string.south),
            resources.getString(R.string.east),
            resources.getString(R.string.west))

        val selectedWords = mutableListOf<String>()
        var i = 0
        val words = mutableListOf<Word>()
        while (true) {
            val randomElement = allWords.random()
            if (randomElement !in selectedWords) {
                i += 1
                selectedWords.add(randomElement)
                val color: String = if (i <= 6) {
                    "red"
                } else if (i <= 12) {
                    "green"
                } else if (i <= 15){
                    "gray"
                } else {
                    "black"
                }
                words.add(Word(randomElement, color, directions.random(), false))
                if (i == 16) {
                    break
                }
            }
        }
        return words.shuffled().toMutableList()
    }

    private fun joinLobbyGame(pollingPhase: String, numberJoined: Int = 0, team: String = resources.getString(R.string.team1)) {
        Log.i(Config.LOADING_GAME_TAG, "Polling cycle start: $pollingPhase")
        when (pollingPhase) {
            "getUser" -> {
                serverHandler.apiCall(
                    Config.GET,
                    Config.GET_USER,
                    userId = userId,
                    callBack = object : ServerHandler.VolleyCallBack {
                        override fun onSuccess(reply: JSONObject?) {
                            val userJsonString = reply.toString()
                            val gson = Gson()
                            user = gson.fromJson(userJsonString, User::class.java)
                            Handler(Looper.getMainLooper()).postDelayed({
                                if (starter) {
                                    joinLobbyGame("createGameLobby")
                                } else {
                                    joinLobbyGame("waiting")
                                }
                            },
                                Config.POLLING_PERIOD)
                        }
                    })
            }
            "createGameLobby" -> {
                var toStringWords = ""
                for (word in words) {
                    toStringWords = toStringWords.plus("--$word")
                }
                toStringWords = toStringWords.substring(2)
                var turns = mutableListOf(resources.getString(R.string.team1), resources.getString(R.string.team2))
                turns = turns.shuffled().toMutableList()
                serverHandler.apiCall(
                    Config.PUT,
                    Config.PUT_NEW_GAME_LOBBY,
                    gameLobbyId = gameLobbyId,
                    words = toStringWords,
                    turn = turns[0],
                    captainIndex1 = captainIndex1,
                    captainIndex2 = captainIndex2,
                    callBack = object : ServerHandler.VolleyCallBack {
                        override fun onSuccess(reply: JSONObject?) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                joinLobbyGame("makeJoinMember")
                            },
                                Config.POLLING_PERIOD)
                        }
                    })
            }
            "makeJoinMember" -> {
                val teamList: MutableList<UserLobby> = if (team == resources.getString(R.string.team1)) {
                    lobby.team1
                } else {
                    lobby.team2
                }
                serverHandler.apiCall(
                    Config.POST,
                    Config.POST_JOIN_GAME_LOBBY,
                    userId = teamList[numberJoined].userId,
                    team = team,
                    gameLobbyId = gameLobbyId,
                    callBack = object : ServerHandler.VolleyCallBack {
                        override fun onSuccess(reply: JSONObject?) {
                            var newNumberJoined = numberJoined + 1
                            var newTeam = team
                            if (teamList.size == newNumberJoined && newTeam != resources.getString(R.string.team2)) {
                                newNumberJoined = 0
                                newTeam = resources.getString(R.string.team2)
                            }
//                            Log.i(Config.LOADING_GAME_TAG, "$newTeam  $newNumberJoined ${teamList.size}")
                            if(newTeam == resources.getString(R.string.team2) && newNumberJoined == teamList.size) {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    joinLobbyGame("deleteLobby")
                                },
                                    Config.POLLING_PERIOD)
                            } else {
                                Handler(Looper.getMainLooper()).postDelayed(
                                    {
                                        joinLobbyGame("makeJoinMember", newNumberJoined, newTeam)
                                    },
                                    Config.POLLING_PERIOD
                                )
                            }
                        }
                    })
            }
            "deleteLobby" -> {
                serverHandler.apiCall(
                    Config.DELETE,
                    Config.DELETE_LOBBY,
                    lobbyId = lobbyId,
                    callBack = object : ServerHandler.VolleyCallBack {
                        override fun onSuccess(reply: JSONObject?) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                joinLobbyGame("waiting")
                            },
                                Config.POLLING_PERIOD)
                        }
                    }
                )
            }
            "waiting" -> {
                serverHandler.apiCall(
                    Config.GET,
                    Config.GET_GAME_LOBBY_NUMBER_OF_MEMBERS,
                    gameLobbyId = gameLobbyId,
                    callBack = object : ServerHandler.VolleyCallBack {
                        override fun onSuccess(reply: JSONObject?) {
                            val number = reply?.getInt("number")
                            if (number == lobbyGameMembers) {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    joinLobbyGame("allReady")
                                },
                                    Config.POLLING_PERIOD)
                            } else {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    joinLobbyGame("waiting")
                                },
                                    Config.POLLING_PERIOD)
                            }
                        }
                    }
                )
            }
            "allReady" -> {
                serverHandler.apiCall(
                Config.GET,
                Config.GET_GAME_INFORMATION,
                userId = userId,
                gameLobbyId = gameLobbyId,
                callBack = object : ServerHandler.VolleyCallBack {
                    override fun onSuccess(reply: JSONObject?) {
                        val userGameJsonString = reply?.get("userGame").toString()
                        val gameLobbyJsonString = reply?.get("gameLobby").toString()
                        val bundle = Bundle()
                        bundle.putString("userGame", userGameJsonString)
                        bundle.putString("gameLobby", gameLobbyJsonString)
                        findNavController().navigate(R.id.action_gameFragmentLoading_to_gameFragment, bundle)
                        val gameActivity: GameActivity = requireActivity() as GameActivity
                        gameActivity.setViewVisible()
                        Handler(Looper.getMainLooper()).postDelayed({
                            joinLobbyGame("pollingEnd")
                        },
                            Config.POLLING_PERIOD)
                    }
                })
            }
            "pollingEnd" -> {
                Log.i(Config.LOADING_GAME_TAG, "Polling ended")
            }
        }
    }
}
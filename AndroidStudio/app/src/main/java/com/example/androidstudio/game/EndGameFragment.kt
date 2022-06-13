package com.example.androidstudio.game

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.androidstudio.R
import com.example.androidstudio.classes.types.GameLobby
import com.example.androidstudio.classes.types.UserGame
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.classes.utils.ServerHandler
import com.example.androidstudio.home.MenuActivity
import org.json.JSONObject

class EndGameFragment(private val iWon: Boolean, private val userGame: UserGame, private val gameLobby: GameLobby) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val serverHandler = ServerHandler(requireContext())
        val rootView = inflater.inflate(R.layout.fragment_end_game, container, false)
        val titleTextView = rootView.findViewById<TextView>(R.id.title_end_game_textview)
        titleTextView.text = if(iWon) {
            resources.getString(R.string.you_win)
        } else {
            resources.getString(R.string.you_lose)
        }

        isCancelable = false

        val buttonEnd = rootView.findViewById<Button>(R.id.go_to_home_end_game_button)
        buttonEnd.setOnClickListener{
            serverHandler.apiCall(
                Config.GET,
                Config.GET_USER,
                userId = userGame.userId,
                callBack = object : ServerHandler.VolleyCallBack {
                    override fun onSuccess(reply: JSONObject?) {
                        val userJsonString = reply.toString()
                        val intent = Intent(requireContext(), MenuActivity::class.java)
                        intent.putExtra("user", userJsonString)
                        startActivity(intent)
                        requireActivity().overridePendingTransition(0, 0);
                        requireActivity().finish()
                        serverHandler.apiCall(
                            Config.DELETE,
                            Config.DELETE_LEAVE_GAMELOBBY,
                            userId = userGame.userId,
                            gameLobbyId = gameLobby.lobbyId
                        )
                    }
                })
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set the fragment dimension to 90% of the device size
        val w = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val h = (resources.displayMetrics.heightPixels * 0.90).toInt()
        val viewResize: View = view.findViewById(R.id.end_game_fragment)
        val layoutParams: ViewGroup.LayoutParams? = viewResize.layoutParams
        layoutParams?.width = w
        layoutParams?.height = h
        viewResize.layoutParams = layoutParams
    }
}
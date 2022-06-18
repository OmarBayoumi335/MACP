package com.example.androidstudio.game

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.androidstudio.R
import com.example.androidstudio.classes.types.GameLobby
import com.example.androidstudio.classes.types.UserGame
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.classes.utils.ServerHandler
import com.example.androidstudio.home.MenuActivity
import kotlinx.android.synthetic.main.fragment_end_game.*
import org.json.JSONObject

class EndGameFragment(private val iWon: Boolean, private val userGame: UserGame, private val gameLobby: GameLobby) : DialogFragment() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val serverHandler = ServerHandler(requireContext())
        val rootView = inflater.inflate(R.layout.fragment_end_game, container, false)
        val titleTextView = rootView.findViewById<TextView>(R.id.title_end_game_textview)
        val image = rootView.findViewById<ImageView>(R.id.end_game_image)
        if(iWon) {
            titleTextView.text = resources.getString(R.string.you_win)
            image.background = ContextCompat.getDrawable(requireContext(), R.drawable.win)
        } else {
            titleTextView.text = resources.getString(R.string.you_lose)
            image.background = ContextCompat.getDrawable(requireContext(), R.drawable.lose)
        }
        isCancelable = false

        val buttonEnd = rootView.findViewById<Button>(R.id.go_to_home_end_game_button)
        buttonEnd.setOnTouchListener { v, event ->
            val scaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)
            val scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> v?.startAnimation(scaleDown)
                MotionEvent.ACTION_UP -> {
                    v?.startAnimation(scaleUp)
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
            }
            v?.onTouchEvent(event) ?: true
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
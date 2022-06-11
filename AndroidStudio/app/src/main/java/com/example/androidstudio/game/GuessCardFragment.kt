package com.example.androidstudio.game

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.androidstudio.R
import com.example.androidstudio.classes.types.Card
import com.example.androidstudio.classes.types.GameLobby
import com.example.androidstudio.classes.types.UserGame
import com.example.androidstudio.game.views.GameView
import com.example.androidstudio.game.views.GuessCardView

class GuessCardFragment(
    private val card: Card,
    private val userGame: UserGame,
    private val gameLobby: GameLobby) : DialogFragment() {

    private lateinit var guessCardView: GuessCardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        guessCardView = GuessCardView(requireContext())
        guessCardView.card = card
        guessCardView.guessCardFragment = this
        guessCardView.requireContext = requireContext()
        guessCardView.userGame = userGame
        guessCardView.gameLobby = gameLobby
        return guessCardView
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the fragment dimension to 90% of the device size
        val w = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val h = (resources.displayMetrics.heightPixels * 0.90).toInt()
        val layoutParams: ViewGroup.LayoutParams? = guessCardView.layoutParams
        layoutParams?.width = w
        layoutParams?.height = h
        guessCardView.layoutParams = layoutParams
    }
}
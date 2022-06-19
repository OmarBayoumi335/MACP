package com.example.androidstudio.loadings

import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.androidstudio.R
import com.example.androidstudio.classes.types.Lobby
import com.example.androidstudio.classes.utils.ServerHandler
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.home.MenuActivity
import com.google.gson.Gson
import org.json.JSONObject

class PartyCreationLoadingFragment : Fragment() {

    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_party_creation_loading, container, false)
        val serverHandler = ServerHandler(requireContext())

        val menuActivity: MenuActivity = requireActivity() as MenuActivity
        menuActivity.setProfileImageButtonVisibility(View.GONE)
        user = menuActivity.getUser()
        menuActivity.setProfileImageButtonVisibility(View.GONE)

        // Android back button
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        val lobbyName = arguments?.getString("lobbyName").toString()

        serverHandler.apiCall(
            Config.PUT,
            Config.PUT_NEW_LOBBY,
            userId = user.userId,
            lobbyName = lobbyName,
            callBack = object: ServerHandler.VolleyCallBack {
                override fun onSuccess(reply: JSONObject?) {
                    val lobbyJsonString = reply?.get("lobby").toString()
                    val gson = Gson()
                    val lobby = gson.fromJson(lobbyJsonString, Lobby::class.java)
                    serverHandler.apiCall(
                        Config.POST,
                        Config.POST_SETUP_INVITABLE,
                        userId = user.userId,
                        lobbyId = lobby.lobbyId,
                        callBack = object : ServerHandler.VolleyCallBack {
                            override fun onSuccess(reply: JSONObject?) {
                                val bundle = bundleOf("lobby" to lobbyJsonString)
                                findNavController().navigate(R.id.action_partyCreationLoadingFragment_to_createPartyFragment, bundle)
                            }
                        }
                    )
                }
            })
        return rootView
    }
}
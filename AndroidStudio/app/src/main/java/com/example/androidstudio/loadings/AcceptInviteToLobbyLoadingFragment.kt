package com.example.androidstudio.loadings

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
import com.example.androidstudio.classes.utils.ServerHandler
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.home.MenuActivity
import org.json.JSONObject

class AcceptInviteToLobbyLoadingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_accept_invite_to_lobby_loading, container, false)

        val serverHandler = ServerHandler(requireContext())

        val menuActivity: MenuActivity = requireActivity() as MenuActivity
        menuActivity.setProfileImageButtonVisibility(View.GONE)
        val user = menuActivity.getUser()

        // Android back button
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        val lobbyId = arguments?.getString("lobbyId").toString()

        serverHandler.apiCall(
            Config.POST,
            Config.POST_ACCEPT_LOBBY_INVITE,
            userId = user.userId,
            lobbyId = lobbyId,
            callBack = object: ServerHandler.VolleyCallBack {
                override fun onSuccess(reply: JSONObject?) {
                    Log.i(Config.LOBBY_TAG, reply.toString())
                    val lobbyJsonString = reply?.get("lobby").toString()
                    val bundle = bundleOf("lobby" to lobbyJsonString)
                    findNavController().navigate(R.id.action_acceptInviteToLobbyLoadingFragment_to_createPartyFragment, bundle)
                }
            })
        return rootView
    }
}
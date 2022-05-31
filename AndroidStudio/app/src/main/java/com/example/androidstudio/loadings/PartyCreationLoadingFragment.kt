package com.example.androidstudio.loadings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.androidstudio.R
import com.example.androidstudio.classes.ServerHandler
import com.example.androidstudio.classes.types.User
import com.example.androidstudio.classes.utils.Config
import com.example.androidstudio.home.MenuActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class PartyCreationLoadingFragment : Fragment() {

    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_party_creation_loading, container, false)
        val serverHandler: ServerHandler = ServerHandler(requireContext())

        val menuActivity: MenuActivity = requireActivity() as MenuActivity
        menuActivity.setProfileImageButtonVisibility(View.GONE)
        user = menuActivity.getUser()

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
                    Log.i(Config.LOBBYTAG, reply.toString())
                    val lobbyJsonString = reply?.get("lobby").toString()
                    val bundle = bundleOf("lobby" to lobbyJsonString)
                    findNavController().navigate(R.id.action_partyCreationLoadingFragment_to_createPartyFragment, bundle)
                }
        })
        return rootView
    }
}
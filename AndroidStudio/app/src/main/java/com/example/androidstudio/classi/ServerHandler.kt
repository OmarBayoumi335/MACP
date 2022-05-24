package com.example.androidstudio.classi

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import org.json.JSONObject

class ServerHandler(context: Context) {
    private val url = Config.SERVER_BASE_URL
    private val pollingPeriod = Config.POLLING_PERIOD
    private val queue = Volley.newRequestQueue(context)

    private var h = Handler(Looper.myLooper()!!)
    private lateinit var r : Runnable

    fun poll(request: String, callBack: VolleyCallBack) {
        r = Runnable{
            get(request, callBack)
            poll(request, callBack)
        }
        h.postDelayed(r,
            this.pollingPeriod)
    }

    fun stopPoll() {
        h.removeCallbacks(r)
    }

    fun get(request: String, callBack: VolleyCallBack) {
        if (request == "0"){
            getUseless(callBack)
        }
    }

    private fun getUseless(callBack: VolleyCallBack) {
        val stringRequest = StringRequest(
            Request.Method.GET, url,{
                    response ->
                // Display the first 500 characters of the response string.
                val reply = JSONObject(response.toString())
//                Log.i(Config.API, "GetUseless response: ")
                callBack.onSuccess(reply)
            }, {
                    error: VolleyError? ->
                Log.e(Config.API, "GetUseless error: " + error.toString())
            })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    interface VolleyCallBack {
        fun onSuccess(reply: JSONObject)
    }
}
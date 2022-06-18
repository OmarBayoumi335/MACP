package com.example.androidstudio.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.androidstudio.R

class ChooseDirectionFragment(remainingHint: Int, team: String) : DialogFragment(), View.OnTouchListener{

    private var remainingHint: Int
    private var team: String
    init {
        this.remainingHint = remainingHint
        this.team = team
    }
    private lateinit var buttonDirection1: Button
    private lateinit var buttonDirection2: Button
    private lateinit var buttonDirection3: Button
    private lateinit var buttonCancel: Button
    private lateinit var buttonConfirm: Button
    private lateinit var textViewNorth: TextView
    private lateinit var textViewWest: TextView
    private lateinit var textViewEast: TextView
    private lateinit var textViewSouth: TextView

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_choose_direction, container, false)

        val view = rootView.findViewById<ConstraintLayout>(R.id.choose_direction_fragment)
        if (team == resources.getString(R.string.team1)) {
            view.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_game_team1)
        } else {
            view.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_game_team2)
        }

        val remainingHintTextView = rootView.findViewById<TextView>(R.id.remaining_hints_textview_choose_direction)
        remainingHintTextView.text = "${resources.getString(R.string.remaining_hints)} $remainingHint"

        buttonDirection1 = rootView.findViewById(R.id.direction1_button_choose_direction)
        buttonDirection1.setOnClickListener{
            deleteDirection(0)
        }
        buttonDirection2 = rootView.findViewById(R.id.direction2_button_choose_direction)
        buttonDirection2.setOnClickListener{
            deleteDirection(1)
        }
        buttonDirection3 = rootView.findViewById(R.id.direction3_button_choose_direction)
        buttonDirection3.setOnClickListener{
            deleteDirection(2)
        }

        buttonCancel = rootView.findViewById(R.id.cancel_button_choose_direction)
        buttonCancel.setOnTouchListener(this)

        buttonConfirm = rootView.findViewById(R.id.confirm_button_choose_direction)
        buttonConfirm.setOnTouchListener(this)

        textViewNorth = rootView.findViewById(R.id.textview_north_choose_direction)
        textViewNorth.setOnTouchListener(this)

        textViewWest = rootView.findViewById(R.id.textview_west_choose_direction)
        textViewWest.setOnTouchListener(this)

        textViewEast = rootView.findViewById(R.id.textview_east_choose_direction)
        textViewEast.setOnTouchListener(this)

        textViewSouth = rootView.findViewById(R.id.textview_south_choose_direction)
        textViewSouth.setOnTouchListener(this)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the fragment dimension to 90% of the device size
        val w = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val h = (resources.displayMetrics.heightPixels * 0.90).toInt()
        val viewResize: View = view.findViewById(R.id.choose_direction_fragment)
        val layoutParams: ViewGroup.LayoutParams? = viewResize.layoutParams
        layoutParams?.width = w
        layoutParams?.height = h
        viewResize.layoutParams = layoutParams
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val scaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)
        val scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> v?.startAnimation(scaleDown)
            MotionEvent.ACTION_UP -> {
                v?.startAnimation(scaleUp)
                when (v?.id) {
//                    R.id.direction1_button_choose_direction -> deleteDirection(0)
//                    R.id.direction2_button_choose_direction -> deleteDirection(1)
//                    R.id.direction3_button_choose_direction -> deleteDirection(2)
                    R.id.textview_north_choose_direction -> addDirection(resources.getString(R.string.north))
                    R.id.textview_west_choose_direction -> addDirection(resources.getString(R.string.west))
                    R.id.textview_east_choose_direction -> addDirection(resources.getString(R.string.east))
                    R.id.textview_south_choose_direction -> addDirection(resources.getString(R.string.south))
                    R.id.confirm_button_choose_direction -> confirmDirections()
                    R.id.cancel_button_choose_direction -> dismiss()
                }
            }
        }
        return true
    }

    private fun deleteDirection(direction: Int){
        val directions = mutableListOf(buttonDirection1, buttonDirection2, buttonDirection3)
        var i = direction
        while(i<2){
            if (directions[i+1].visibility == View.VISIBLE){
                directions[i].text = directions[i+1].text
                i += 1
            }else{
                break
            }
        }
        directions[i].visibility = View.GONE
    }

    private fun addDirection(direction: String){
        val directions = mutableListOf(buttonDirection1, buttonDirection2, buttonDirection3)
        for (i in 0 until remainingHint){
            if (directions[i].visibility == View.GONE){
                directions[i].text = direction
                directions[i].visibility = View.VISIBLE
                break
            }
        }
    }

    private fun confirmDirections(){
        val gameActivity = requireActivity() as GameActivity
        val buttonDirection = gameActivity.findViewById<TextView>(R.id.game_direction_hint)
        var text = ""
        val directions = mutableListOf(buttonDirection1, buttonDirection2, buttonDirection3)
        for (direction in directions){
            if (direction.visibility == View.VISIBLE){
                text = text.plus(" " + direction.text.toString())
            }
        }
        text = if (text == ""){
            "-"
        } else {
            text.substring(1)
        }
        buttonDirection.text = text
        dismiss()

    }

}
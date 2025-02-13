package com.example.schoolerp.Fragments
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.schoolerp.R


class Messages : Fragment() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_messages, container, false)

        // Access views using the 'view' reference
        val writeMessage: TextView = view.findViewById(R.id.writeMessage)
        val layoutSendMessage: LinearLayout = view.findViewById(R.id.layoutSendmessage)

        // Set the click listener for 'writeMessage' TextView
        writeMessage.setOnClickListener {
            // Toggle visibility of layout_sendmessage
            layoutSendMessage.visibility = if (layoutSendMessage.visibility == View.GONE) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        return view
    }


}
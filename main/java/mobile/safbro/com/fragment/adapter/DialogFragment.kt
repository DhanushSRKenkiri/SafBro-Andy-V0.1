package mobile.safbro.com.fragment.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import mobile.safbro.com.R

class BottomAudioPlayerFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.audio_popup_layout_in_app, container, false)

        // Initialize your audio controls and set click listeners

        return view
    }
}

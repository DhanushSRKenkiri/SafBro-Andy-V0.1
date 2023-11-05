package mobile.safbro.com.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import mobile.safbro.com.R
import mobile.safbro.com.databinding.AudioPopupLayoutInAppBinding

class BottomSheetFragment : DialogFragment() {

    private var binding: AudioPopupLayoutInAppBinding? = null

    companion object {
        private const val ARG_IMAGE_URL = "image_url"
        private const val ARG_TITLE = "title"

        fun newInstance(imageUrl: String, title: String): BottomSheetFragment {
            val fragment = BottomSheetFragment()
            val args = Bundle()
            args.putString(ARG_IMAGE_URL, imageUrl)
            args.putString(ARG_TITLE, title)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AudioPopupLayoutInAppBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve image URL and title from arguments
        val imageUrl = arguments?.getString(ARG_IMAGE_URL)
        val title = arguments?.getString(ARG_TITLE)

        // Find the ImageView views without nullable modifier
        val bottomSheetCoverArt2 = view.findViewById<ImageView>(R.id.bottomSheetCoverArt2)
        val bottomSheetTitle2 = view.findViewById<TextView>(R.id.textViewTitle)

        imageUrl?.let {
            Glide.with(requireContext())
                .load(it)
                .into(bottomSheetCoverArt2)
        }
        bottomSheetTitle2?.text = title

        // Add playback controls and functionality here
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

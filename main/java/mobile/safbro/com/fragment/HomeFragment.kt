package mobile.safbro.com.fragment

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import mobile.safbro.com.R
import mobile.safbro.com.fragment.adapter.ImageAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HomeFragment : Fragment() {

    private val storage = Firebase.storage
    private lateinit var recyclerView: RecyclerView
    private val imageList = mutableListOf<String>() // List to store image URLs
    private val titleList = mutableListOf<String>() // List to store titles
    private val audioFileNames = mutableListOf<String>() // List to store audio file names
    private val adapter = ImageAdapter(imageList, titleList)
    private var isLoading = false
    private var isLastPage = false
    private var currentPage = 1

    private lateinit var database: FirebaseDatabase
    private lateinit var imageRef: DatabaseReference

    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var isSeekBarTracking = false

    private lateinit var bottomAudioPlayer: View
    private lateinit var playPauseButton: ImageButton
    private lateinit var seekBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the Firebase Realtime Database reference
        database = FirebaseDatabase.getInstance()
        imageRef = database.getReference("UserUploads")
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Set the item click listener for the adapter
        adapter.setOnItemClickListener(object : ImageAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // Handle item click here, e.g., play audio
                playAudioForImage(position)
            }

            override fun onPlayPauseButtonClick(position: Int) {
                // Handle play/pause button click here
                onPlayPauseButtonClick()
            }
        })

        // Find the bottom audio player view
        bottomAudioPlayer = view.findViewById(R.id.bottomAudioPlayer)

        // Find audio player UI components
        playPauseButton = bottomAudioPlayer.findViewById(R.id.playPauseButton)
        seekBar = bottomAudioPlayer.findViewById(R.id.seekBar)

        // Initialize audio player UI components and controls
        playPauseButton.setOnClickListener {
            onPlayPauseButtonClick()
        }

        // Set up SeekBar listener for user interaction
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    // Update MediaPlayer's playback position when SeekBar is dragged
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isSeekBarTracking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isSeekBarTracking = false
            }
        })

        // Load the initial batch of images and titles
        loadImagesAndTitlesFromDatabase()

        // Implement pagination for loading more images as the user scrolls
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                    ) {
                        currentPage++
                        loadImagesAndTitlesFromDatabase()
                    }
                }
            }
        })

        return view
    }

    private fun loadImagesAndTitlesFromDatabase() {
        isLoading = true

        // Replace "images" with your Firebase Storage folder name
        val storageRef = storage.reference.child("images")

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val result = storageRef.listAll().await()
                val items = result.items
                val start = (currentPage - 1) * PAGE_SIZE
                val end = kotlin.math.min(currentPage * PAGE_SIZE, items.size)

                for (i in start until end) {
                    val imageRef = items[i]
                    val imageName = imageRef.name.split('.')[0]
                    val url = imageRef.downloadUrl.await()
                    val titleRef = database.reference.child("UserUploads/$imageName/title")
                    val titleSnapshot = titleRef.get().await()
                    val title = titleSnapshot.getValue(String::class.java) ?: ""

                    imageList.add(url.toString())
                    titleList.add(title)
                    audioFileNames.add(imageName)
                    adapter.notifyItemInserted(imageList.size - 1)
                }

                isLoading = false
                if (end >= items.size) {
                    isLastPage = true
                }
            } catch (exception: Exception) {
                // Handle any errors here
                exception.printStackTrace()
            }
        }
    }

    private fun playAudioForImage(position: Int) {
        if (position >= 0 && position < audioFileNames.size) {
            // Stop any currently playing audio
            mediaPlayer?.stop()
            mediaPlayer?.reset()

            // Replace "audios" with your Firebase Storage folder name for audio files
            val audioStorageRef = storage.reference.child("audios")
            val imageName = audioFileNames[position]
            // Display a Toast when an item is clicked
            val toastMessage = "Clicked on item: $imageName"
            Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()

            val audioName = "$imageName.mp3" // Modify the image name to match audio name

            if (isPlaying) {
                playPauseButton.setImageResource(R.drawable.baseline_pause_24)
            } else {
                playPauseButton.setImageResource(R.drawable.baseline_play_arrow_24)
            }

            if (mediaPlayer != null) {
                if (isPlaying) {
                    // If MediaPlayer is playing, pause it
                    mediaPlayer?.pause()
                } else {
                    // If MediaPlayer is paused, resume playback
                    mediaPlayer?.start()
                    startUpdatingSeekBar()
                }
                isPlaying = !isPlaying
                updatePlayPauseButton()
            }

            // Check if the corresponding audio file exists
            audioStorageRef.child(audioName).downloadUrl
                .addOnSuccessListener { audioUri ->
                    try {
                        // Initialize and start the MediaPlayer
                        mediaPlayer = MediaPlayer().apply {
                            setAudioAttributes(AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build())
                            setDataSource(audioUri.toString())
                            prepare()
                            start()
                        }

                        // Log success message
                        Log.d("MediaPlayer", "Audio playback started")

                        // Update the bottom player's ImageView and TextView
                        val imageUrl = imageList[position]
                        val title = titleList[position]
                        updateBottomPlayerViews(imageUrl, title)
                    } catch (e: Exception) {
                        // Handle MediaPlayer initialization error
                        val errorMessage = "Error initializing MediaPlayer: ${e.message}"
                        // Log error message
                        Log.e("MediaPlayer", errorMessage)
                        // Handle error here
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle audio retrieval failure
                    val errorMessage = "Error playing audio: ${exception.message}"
                    // Log error message
                    Log.e("MediaPlayer", errorMessage)
                    // Handle error here
                }
        } else {
            // Handle invalid position
            Toast.makeText(requireContext(), "Invalid item position", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateBottomPlayerViews(imageUrl: String, title: String) {
        // Find the ImageView and TextView in the bottom player layout
        val bottomSheetCoverArt2 = bottomAudioPlayer.findViewById<ImageView>(R.id.bottomSheetCoverArt2)
        val bottomSheetTitle2 = bottomAudioPlayer.findViewById<TextView>(R.id.textViewTitle)

        // Load the image using Glide
        Glide.with(requireContext())
            .load(imageUrl)
            .into(bottomSheetCoverArt2)

        // Set the title in the TextView
        bottomSheetTitle2.text = title
    }


    private fun onPlayPauseButtonClick() {
        // Handle play/pause button click here
        if (mediaPlayer != null) {
            if (isPlaying) {
                // If MediaPlayer is playing, pause it
                mediaPlayer?.pause()
            } else {
                // If MediaPlayer is paused, resume playback
                mediaPlayer?.start()
                startUpdatingSeekBar()
            }
            isPlaying = !isPlaying
            updatePlayPauseButton()
        }
    }

    private fun updatePlayPauseButton() {
        if (isPlaying) {
            playPauseButton.setImageResource(R.drawable.baseline_pause_24)
        } else {
            playPauseButton.setImageResource(R.drawable.baseline_play_arrow_24)
        }
    }

    // Start a periodic task to update SeekBar position
    private fun startUpdatingSeekBar() {
        val updateSeekBarTask = object : Runnable {
            override fun run() {
                if (mediaPlayer != null && !isSeekBarTracking) {
                    updateSeekBarAndPosition()
                }
                // Delayed execution to update every 1 second (adjust as needed)
                seekBar.postDelayed(this, 1000)
            }
        }
        updateSeekBarTask.run()
    }

    // Update the SeekBar position and other UI elements
    private fun updateSeekBarAndPosition() {
        val duration = mediaPlayer?.duration ?: 0
        val currentPosition = mediaPlayer?.currentPosition ?: 0

        // Update SeekBar progress
        seekBar.max = duration
        seekBar.progress = currentPosition

        // You can also update the remaining time or other UI elements as needed
        // Example: Update remaining time TextView
        val remainingTimeTextView = view?.findViewById<TextView>(R.id.remainingTimeTextView)
        val remainingTime = duration - currentPosition
        if (remainingTimeTextView != null) {
            remainingTimeTextView.text = formatTime(remainingTime)
        }
    }

    // Function to format time in minutes and seconds
    private fun formatTime(milliseconds: Int): String {
        val seconds = milliseconds / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove SeekBar updates
        seekBar.removeCallbacks(null)

        // Release the MediaPlayer when the fragment is destroyed
        mediaPlayer?.release()
        mediaPlayer = null
    }

    companion object {
        private const val PAGE_SIZE = 10 // Adjust the batch size as needed
    }
}

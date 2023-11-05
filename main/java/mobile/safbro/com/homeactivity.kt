package mobile.safbro.com


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import mobile.safbro.com.fragment.HomeFragment

class homeactivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homeactivity)

        auth = Firebase.auth

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val fragmentHome = findViewById<FrameLayout>(R.id.fragment_home)
        val fragmentLibrary = findViewById<FrameLayout>(R.id.fragment_library)
        val fragmentSearch = findViewById<FrameLayout>(R.id.fragment_search)
        val fragmentFeedback = findViewById<FrameLayout>(R.id.fragment_feedback)
        val profileImageView = findViewById<ImageView>(R.id.profileImageView)

        // Initialize the Home fragment as the default fragment
        supportFragmentManager.beginTransaction().replace(R.id.fragment_home, HomeFragment()).commit()

        // Select the corresponding menu item to indicate that HomeFragment is active
        bottomNavigation.selectedItemId = R.id.navigation_home

        // Apply a custom style to set text color to white
        bottomNavigation.itemTextColor = resources.getColorStateList(R.color.white)
        bottomNavigation.itemIconTintList = resources.getColorStateList(R.color.white)

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    fragmentHome.visibility = View.VISIBLE
                    fragmentLibrary.visibility = View.GONE
                    fragmentSearch.visibility = View.GONE
                    fragmentFeedback.visibility = View.GONE
                    true
                }
                R.id.navigation_dashboard -> {
                    fragmentHome.visibility = View.GONE
                    fragmentLibrary.visibility = View.VISIBLE
                    fragmentSearch.visibility = View.GONE
                    fragmentFeedback.visibility = View.GONE
                    true
                }
                R.id.navigation_notifications -> {
                    fragmentHome.visibility = View.GONE
                    fragmentLibrary.visibility = View.GONE
                    fragmentSearch.visibility = View.VISIBLE
                    fragmentFeedback.visibility = View.GONE
                    true
                }

                R.id.navigation_feedback -> {
                    fragmentHome.visibility = View.GONE
                    fragmentLibrary.visibility = View.GONE
                    fragmentSearch.visibility = View.GONE
                    fragmentFeedback.visibility = View.VISIBLE
                    true
                }
                else -> false
            }
        }

        // Load the user's profile image if signed in
        loadProfileImage()
    }

    private fun loadProfileImage() {
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            // Retrieve the user's profile image URL from Firebase
            val photoUrl = currentUser.photoUrl
            if (photoUrl != null) {
                // Load the profile image into the ImageView using Glide
                val profileImageView = findViewById<ImageView>(R.id.profileImageView)
                Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.logo) // You can set a default image
                    .into(profileImageView)
            }
        }
    }
}

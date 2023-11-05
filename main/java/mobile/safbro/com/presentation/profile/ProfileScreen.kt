package mobile.safbro.com

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@Composable
fun ProfileScreen(
    userId: String?,
    onSignOut: () -> Unit,
    // Remove the NavController parameter
) {
    val context = LocalContext.current
    val rootView = LayoutInflater.from(context).inflate(R.layout.user_inputs, null)
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    // Use userId in the Firebase Realtime Database reference path
    val databaseReference: DatabaseReference = database.getReference("userchecklists/$userId/selectedChecklists")

    // Get a reference to the "Go" button
    val goButton = rootView.findViewById<Button>(R.id.enter_button)

    var isUploadSuccessful by remember { mutableStateOf(false) }

    // Set an OnClickListener for the "Go" button
    goButton.setOnClickListener {
        // Initialize a list to store selected checklists
        val selectedChecklists = mutableListOf<String>()

        // Get references to all the checkboxes
        val rockCheckBox = rootView.findViewById<CheckBox>(R.id.checkbox_audiolectures)
        val popCheckBox = rootView.findViewById<CheckBox>(R.id.checkbox_podcasts)
        val hipHopCheckBox = rootView.findViewById<CheckBox>(R.id.checkbox_audiobooks)
        val jazzCheckBox = rootView.findViewById<CheckBox>(R.id.checkbox_sciencenigga)
        val countryCheckBox = rootView.findViewById<CheckBox>(R.id.checkbox_meditateman)
        val country2CheckBox = rootView.findViewById<CheckBox>(R.id.checkbox_documentriesaregood)
        val country3CheckBox = rootView.findViewById<CheckBox>(R.id.checkbox_thesesispluralforthesis)
        val country23CheckBox = rootView.findViewById<CheckBox>(R.id.checkbox_journals)

        // Check which checkboxes are selected and add them to the list
        if (rockCheckBox.isChecked) selectedChecklists.add("1")
        if (popCheckBox.isChecked) selectedChecklists.add("2")
        if (hipHopCheckBox.isChecked) selectedChecklists.add("3")
        if (jazzCheckBox.isChecked) selectedChecklists.add("4")
        if (countryCheckBox.isChecked) selectedChecklists.add("5")
        if (country2CheckBox.isChecked) selectedChecklists.add("6")
        if (country3CheckBox.isChecked) selectedChecklists.add("7")
        if (country23CheckBox.isChecked) selectedChecklists.add("8")

        // Send the selected checklists to Firebase Realtime Database
        databaseReference.setValue(selectedChecklists)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isUploadSuccessful = true
                    // Start the "homeactivity" activity here using an explicit intent
                    val intent = Intent(context, homeactivity::class.java)
                    context.startActivity(intent)
                } else {
                    isUploadSuccessful = false
                    // Handle upload failure if needed
                }
            }
    }

    AndroidView(
        factory = { context ->
            rootView
        },
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    )
}

package mobile.safbro.com

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.Identity
import com.plcoding.composegooglesignincleanarchitecture.presentation.sign_in.GoogleAuthUiClient

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.launcher_activity)  // Set your splash screen layout here

        // Simulate app loading or initialization
        simulateAppLoading()
    }

    private fun simulateAppLoading() {
        // Simulate app loading process using a background thread or AsyncTask
        object : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void): Boolean {
                // Simulate loading by adding a delay
                try {
                    Thread.sleep(3000)  // Adjust the delay to 3 seconds (3000 milliseconds)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                // Check if the user is already logged in
                val googleAuthUiClient = GoogleAuthUiClient(
                    context = applicationContext,
                    oneTapClient = Identity.getSignInClient(applicationContext)
                )

                return googleAuthUiClient.getSignedInUser() != null
            }

            override fun onPostExecute(isLoggedIn: Boolean) {
                super.onPostExecute(isLoggedIn)

                // Redirect the user based on their login status
                if (isLoggedIn) {
                    val intent = Intent(this@LauncherActivity, homeactivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@LauncherActivity, MainActivity::class.java)
                    startActivity(intent)
                }

                finish()
            }
        }.execute()
    }
}

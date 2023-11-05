package mobile.safbro.com.presentation.sign_in

import android.view.LayoutInflater
import android.widget.Button
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import mobile.safbro.com.R
import mobile.safbro.com.presentation.sign_in.SignInState

@Composable
fun SignInScreen(
    state: SignInState,
    onSignInClick: () -> Unit
) {
    val context = LocalContext.current

    // Inflating the XML layout
    val rootView = LayoutInflater.from(context).inflate(R.layout.sign_in_layout, null)

    // Finding the sign-in button and setting click listener
    val signInButton = rootView.findViewById<Button>(R.id.signInButton)
    signInButton.setOnClickListener {
        onSignInClick()
    }

    // Wrapping the inflated View in Compose
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { rootView }
    )
}

package com.vhennus

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.cloudinary.android.MediaManager
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.ktx.storage
import com.vhennus.auth.data.AuthViewModel
import com.vhennus.chat.data.ChatViewModel
import com.vhennus.feed.data.FeedViewModel
import com.vhennus.profile.data.ProfileViewModel
import com.vhennus.trade.data.OrderViewModel
import com.vhennus.trivia.data.TriviaViewModel
import com.vhennus.ui.theme.AmorgensTheme
import com.vhennus.wallet.data.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.sentry.SentryLevel
import io.sentry.android.core.SentryAndroid


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        FirebaseApp.initializeApp(this) ?: throw IllegalStateException("FirebaseApp initialization failed")
//        Log.d("FirebaseInit", "ApplicationId: ${BuildConfig.APPLICATION_ID}")

//        val storage = Firebase.storage

        firebaseAnalytics = Firebase.analytics
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                Log.d("FCM", "FCM Token: $token")
            }



        SentryAndroid.init(this) { options ->
            options.dsn = "https://e5aa33217ae0e9e465a28c7f3cbc0a45@o4507910790119424.ingest.us.sentry.io/4507910882131968"
            options.tracesSampleRate = 1.0 // Set the performance monitoring sample rate
            options.setDiagnosticLevel(SentryLevel.ERROR)
            options.isDebug = true
            options.isEnableUncaughtExceptionHandler = true
        }


        setContent {
            val walletViewModel:WalletViewModel = hiltViewModel()
            val orderViewModel:OrderViewModel = hiltViewModel()
            val authViewModel:AuthViewModel = hiltViewModel()
            val feedViewModel:FeedViewModel = hiltViewModel()
            val navController = rememberNavController()
            val triviaViewModel:TriviaViewModel = hiltViewModel()
            val chatViewModel:ChatViewModel = hiltViewModel()
            val profileViewModel:ProfileViewModel = hiltViewModel()


            AmorgensTheme(darkTheme = false) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNav(navController =navController,
                        walletViewModel ,
                        orderViewModel,
                        authViewModel,
                        feedViewModel,
                        triviaViewModel,
                        chatViewModel,
                        profileViewModel
                    )

                }
            }
        }
    }



}




//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    AmorgensTheme {
//        Greeting("Android")
//    }
//}

//amara101 12345
// drake_drizy
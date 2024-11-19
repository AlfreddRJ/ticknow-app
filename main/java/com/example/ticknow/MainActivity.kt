package com.example.ticknow

import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsAnimation
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ticknow.data.AuthService
import com.example.ticknow.navigation.NavigationWrapper
import com.example.ticknow.ui.theme.TickNowTheme
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
//    private val fanViewModel: FanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fanViewModel: FanViewModel by viewModels()
        enableEdgeToEdge()
        setContent {
            TickNowTheme {
                NavigationWrapper(fanViewModel)
            }
        }
    }

}


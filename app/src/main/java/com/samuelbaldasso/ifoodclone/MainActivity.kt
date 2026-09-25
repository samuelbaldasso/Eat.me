package com.samuelbaldasso.ifoodclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.samuelbaldasso.ifoodclone.core.designsystem.theme.EatMeTheme
import com.samuelbaldasso.ifoodclone.ui.theme.composables.main.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EatMeTheme {
                MainScreen()
            }
        }
    }
}
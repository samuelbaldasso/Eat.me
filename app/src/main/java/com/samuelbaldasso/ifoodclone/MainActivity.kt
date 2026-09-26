package com.samuelbaldasso.ifoodclone

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.samuelbaldasso.ifoodclone.core.designsystem.theme.EatMeTheme
import com.samuelbaldasso.ifoodclone.ui.theme.composables.main.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Polished splash exit animation with fade & anticipate interpolator
        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            val splashView = splashScreenViewProvider.view
            val alpha = ObjectAnimator.ofFloat(splashView, View.ALPHA, 1f, 0f).apply {
                interpolator = AnticipateInterpolator()
                duration = 350L
                doOnEnd { splashScreenViewProvider.remove() }
            }
            alpha.start()
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        setContent {
            EatMeTheme {
                MainScreen()
            }
        }
    }
}
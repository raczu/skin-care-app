package com.raczu.skincareapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.raczu.skincareapp.screens.MainScreen
import com.raczu.skincareapp.ui.theme.SkinCareAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkinCareAppTheme {
                Text("Hello, world!")
            }
        }
    }
}

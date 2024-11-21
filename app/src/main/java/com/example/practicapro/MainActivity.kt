package com.example.practicapro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.practicapro.ui.navigation.AppNavigation


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                AppContent(modifier = Modifier.padding(innerPadding))
            }

        }
    }
}

@Composable
fun AppContent(modifier: Modifier = Modifier) {
    AppNavigation()
}

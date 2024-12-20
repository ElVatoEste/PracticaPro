package com.example.practicapro

import ConnectivityIndicator
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.practicapro.navigation.AppNavigation
import com.example.practicapro.network.NetworkObserver

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetworkObserver.startObserving(this)
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { innerPadding ->
                ConnectivityIndicator(snackbarHostState = snackbarHostState)
                AppContent(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}

@Composable
fun AppContent(modifier: Modifier = Modifier) {
    AppNavigation()
}

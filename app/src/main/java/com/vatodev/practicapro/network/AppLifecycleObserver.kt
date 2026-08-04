package com.vatodev.practicapro.network

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.vatodev.practicapro.rooms.appDatabase.DatabaseProvider
import kotlinx.coroutines.launch

/**
 * Al pasar la app a segundo plano vuelca el WAL de Room.
 *
 * Es el momento en que el sistema puede respaldar los datos, y la copia solo
 * incluye el archivo principal de la base.
 */
class AppLifecycleObserver : DefaultLifecycleObserver {

    override fun onStop(owner: LifecycleOwner) {
        owner.lifecycleScope.launch {
            DatabaseProvider.volcarWal()
        }
    }
}

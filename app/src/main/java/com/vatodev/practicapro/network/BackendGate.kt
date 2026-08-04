package com.vatodev.practicapro.network

import com.vatodev.practicapro.BuildConfig
import kotlinx.coroutines.flow.first

/**
 * Punto único de control de la superficie de red.
 *
 * Toda condición de red del proyecto debe pasar por aquí: con
 * `BACKEND_ENABLED` en `false` ninguna petición sale del dispositivo.
 */
object BackendGate {

    val isEnabled: Boolean get() = BuildConfig.BACKEND_ENABLED

    /** Backend activado y con conectividad. */
    suspend fun isReachable(): Boolean =
        isEnabled && NetworkObserver.isNetworkAvailable.first()
}

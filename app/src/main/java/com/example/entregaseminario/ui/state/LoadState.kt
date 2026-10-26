package com.example.entregaseminario.ui.state

import com.example.entregaseminario.BuildConfig
import retrofit2.HttpException
import java.io.IOException
import java.util.Locale

sealed interface LoadState<out T> {
    data object Loading : LoadState<Nothing>
    data class Ready<T>(val value: T) : LoadState<T>
    data class Failure(val message: String) : LoadState<Nothing>
}

internal suspend fun <T> resultState(block: suspend () -> T): LoadState<T> =
    try {
        LoadState.Ready(block())
    } catch (e: Exception) {
        LoadState.Failure(errorText(e))
    }

internal fun errorText(e: Exception): String {
    val spanish = Locale.getDefault().language == "es"
    return when (e) {
        is IOException -> when {
            BuildConfig.TMDB_READ_ACCESS_TOKEN.isBlank() -> if (spanish) {
                "Configurá TMDB_READ_ACCESS_TOKEN en local.properties."
            } else {
                "Set TMDB_READ_ACCESS_TOKEN in local.properties."
            }
            spanish -> "No hay conexión. Revisá la red e intentá nuevamente."
            else -> "Network unavailable. Check your connection and retry."
        }
        is HttpException -> when (e.code()) {
            401, 403 -> if (spanish) {
                "Falló la autenticación de TMDB. Revisá el token de lectura."
            } else {
                "TMDB authentication failed. Check the read access token."
            }
            else -> if (spanish) {
                "TMDB devolvió un error HTTP " + e.code() + "."
            } else {
                "TMDB request failed (HTTP " + e.code() + ")."
            }
        }
        else -> if (spanish) {
            "No se pudo cargar el contenido. Intentá nuevamente."
        } else {
            "Unable to load this content. Please retry."
        }
    }
}


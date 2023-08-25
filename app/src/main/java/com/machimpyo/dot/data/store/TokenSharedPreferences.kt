package com.machimpyo.dot.data.store

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class TokenSharedPreferences(
    private val context: Context
) {

    private val PREFS_NAME = "token_prefs"
    private val FIREBASE_ID_TOKEN = "firebase_id_token"

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)

    fun getFirebaseIdToken(): String? {
        return prefs.getString(FIREBASE_ID_TOKEN, null)
    }

    fun putFirebaseIdToken(idToken: String?) {
        prefs.edit().putString(FIREBASE_ID_TOKEN, idToken).apply()
    }

}
package com.example.sparkapp.ui.screens.login

import android.app.Application
import android.content.Context
//
// --- ALL THESE IMPORTS ARE REQUIRED TO FIX THE ERRORS ---
//
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
//
// --- END OF REQUIRED IMPORTS ---
//

// Create the DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "spark_app_prefs")

class DataStoreViewModel(application: Application) : AndroidViewModel(application) {

    // This property was missing the 'application.' prefix
    private val dataStore = application.dataStore

    companion object {
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        val USER_ID_KEY = intPreferencesKey("user_id")
    }

    // Function to save login data
    fun saveUserLogin(email: String, id: Int) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[USER_EMAIL_KEY] = email
                preferences[USER_ID_KEY] = id
            }
        }
    }

    // Function to clear login data (for logout)
    fun clearUserLogin() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences.clear()
            }
        }
    }

    // Flow to read the user's email
    val userEmail: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_EMAIL_KEY]
    }

    // Flow to read the user's ID
    val userId: Flow<Int?> = dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }
}
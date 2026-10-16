package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.appPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "tecno_ordem_app_preferences")

data class UserPreferences(
    val appLanguage: String = "pt-BR",
    val companyLogoUri: String = "",
    val hasSelectedInitialLanguage: Boolean = false,
    val appThemeMode: String = "SYSTEM"
)

class AppPreferencesDataStore(private val context: Context) {

    private val dataStore = context.appPreferencesDataStore

    object Keys {
        val APP_LANGUAGE = stringPreferencesKey("app_language")
        val COMPANY_LOGO_URI = stringPreferencesKey("company_logo_uri")
        val HAS_SELECTED_INITIAL_LANG = booleanPreferencesKey("has_selected_initial_lang")
        val APP_THEME_MODE = stringPreferencesKey("app_theme_mode")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                appLanguage = preferences[Keys.APP_LANGUAGE] ?: "pt-BR",
                companyLogoUri = preferences[Keys.COMPANY_LOGO_URI] ?: "",
                hasSelectedInitialLanguage = preferences[Keys.HAS_SELECTED_INITIAL_LANG] ?: false,
                appThemeMode = preferences[Keys.APP_THEME_MODE] ?: "SYSTEM"
            )
        }

    val appLanguageFlow: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.APP_LANGUAGE] ?: "pt-BR" }

    val companyLogoUriFlow: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.COMPANY_LOGO_URI] ?: "" }

    val hasSelectedInitialLangFlow: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.HAS_SELECTED_INITIAL_LANG] ?: false }

    suspend fun setAppLanguage(languageCode: String) {
        dataStore.edit { preferences ->
            preferences[Keys.APP_LANGUAGE] = languageCode
        }
    }

    suspend fun setCompanyLogoUri(logoUri: String) {
        dataStore.edit { preferences ->
            preferences[Keys.COMPANY_LOGO_URI] = logoUri
        }
    }

    suspend fun setHasSelectedInitialLanguage(hasSelected: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.HAS_SELECTED_INITIAL_LANG] = hasSelected
        }
    }

    suspend fun setAppThemeMode(themeMode: String) {
        dataStore.edit { preferences ->
            preferences[Keys.APP_THEME_MODE] = themeMode
        }
    }

    suspend fun clearCompanyLogo() {
        dataStore.edit { preferences ->
            preferences[Keys.COMPANY_LOGO_URI] = ""
        }
    }
}

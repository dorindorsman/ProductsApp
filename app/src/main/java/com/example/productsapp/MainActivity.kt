package com.example.productsapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.productsapp.ui.navigation.NavGraph
import com.example.productsapp.ui.theme.ProductsAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    @SuppressLint("FlowOperatorInvokedInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply saved locale before UI renders
        val prefs = runBlocking { dataStore.data.first() }
        val savedLanguage = prefs[stringPreferencesKey("language")] ?: "en"
        updateLocale(this, savedLanguage)

        enableEdgeToEdge()

        setContent {
            val isDarkMode by dataStore.data
                .map { it[booleanPreferencesKey("dark_mode")] ?: false }
                .collectAsStateWithLifecycle(initialValue = prefs[booleanPreferencesKey("dark_mode")] ?: false)

            val language by dataStore.data
                .map { it[stringPreferencesKey("language")] ?: "en" }
                .collectAsStateWithLifecycle(initialValue = savedLanguage)

            var previousLanguage by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(language) {
                if (previousLanguage != null && previousLanguage != language) {
                    updateLocale(this@MainActivity, language)
                    recreate()
                }
                previousLanguage = language
            }

            ProductsAppTheme(darkTheme = isDarkMode) {
                NavGraph()
            }
        }
    }
}

fun updateLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = context.resources.configuration
    config.setLocale(locale)
    @Suppress("DEPRECATION")
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}
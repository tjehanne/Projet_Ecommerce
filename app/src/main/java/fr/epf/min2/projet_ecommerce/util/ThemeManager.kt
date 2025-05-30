package fr.epf.min2.projet_ecommerce.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import fr.epf.min2.projet_ecommerce.R
import androidx.core.content.edit

object ThemeManager {

    // Clés pour les préférences partagées
    private const val PREFS_NAME = "app_prefs"
    private const val DARK_MODE_KEY = "dark_mode"
    private const val COLOR_THEME_KEY = "color_theme"

    // Appliquer tous les thèmes (mode sombre et couleur)
    fun applyTheme(context: Context) {
        applyDarkMode(context)
        applyColorTheme(context, getCurrentColorTheme(context))
    }

    // Appliquer le mode sombre (surcharge pour Context)
    fun applyDarkMode(context: Context) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean(DARK_MODE_KEY, isSystemInDarkMode(context))

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    // Définir le mode sombre
    fun setDarkMode(activity: Activity, isDarkMode: Boolean) {
        val sharedPrefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit { putBoolean(DARK_MODE_KEY, isDarkMode) }

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    // Obtenir le thème de couleur actuel
    fun getCurrentColorTheme(context: Context): String {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getString(COLOR_THEME_KEY, "default") ?: "default"
    }

    // Définir le thème de couleur
    fun setColorTheme(context: Context, themeName: String) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit { putString(COLOR_THEME_KEY, themeName) }
    }

    // Appliquer le thème de couleur
    fun applyColorTheme(context: Context, themeName: String) {
        // La mise à jour du thème est gérée en changeant le style de l'activité
        // Nous devons utiliser le système de ressources d'Android
        val styleRes = when (themeName) {
            "blue" -> R.style.Theme_Projet_Ecommerce_Blue
            "green" -> R.style.Theme_Projet_Ecommerce_Green
            "purple" -> R.style.Theme_Projet_Ecommerce_Purple
            "orange" -> R.style.Theme_Projet_Ecommerce_Orange
            else -> R.style.Theme_Projet_Ecommerce
        }

        if (context is Activity) {
            context.setTheme(styleRes)
        }
    }

    // Vérifier si le système est en mode sombre
    private fun isSystemInDarkMode(context: Context): Boolean {
        return (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }
}
package fr.epf.min2.projet_ecommerce

import android.app.Application
import fr.epf.min2.projet_ecommerce.util.ThemeManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Appliquer le thème au démarrage de l'application
        ThemeManager.applyTheme(this)
    }
}
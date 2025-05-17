package fr.epf.min2.projet_ecommerce

import android.content.Context
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import fr.epf.min2.projet_ecommerce.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        applyDarkMode()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            // Configurer d'abord la Toolbar
            setSupportActionBar(binding.appBarMain.toolbar)

            val navController = findNavController(R.id.nav_host_fragment_content_main)
            val drawerLayout: DrawerLayout = binding.drawerLayout
            val navView: NavigationView = binding.navView

            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_products, R.id.nav_scan, R.id.nav_cart, R.id.nav_settings
                ), drawerLayout
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)

            // Configurer la navigation inférieure
            binding.appBarMain.bottomNavigation.setupWithNavController(navController)

            navController.addOnDestinationChangedListener { _, destination, _ ->
                // Mettre à jour l'élément actif dans la barre de navigation inférieure
                if (destination.id == R.id.nav_products ||
                    destination.id == R.id.nav_scan ||
                    destination.id == R.id.nav_cart ||
                    destination.id == R.id.nav_settings
                ) {
                    binding.appBarMain.bottomNavigation.menu.findItem(destination.id)?.isChecked =
                        true
                }
            }
        } catch (e: Exception) {
            // Log l'erreur pour le débogage
            e.printStackTrace()

            // En cas d'erreur d'initialisation, alternative plus simple
            try {
                // Obtenir le NavHostFragment directement
                val navHostFragment = supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
                val navController = navHostFragment.navController
                val drawerLayout: DrawerLayout = binding.drawerLayout
                val navView: NavigationView = binding.navView

                // Configuration manuelle de la BottomNavigationView
                binding.appBarMain.bottomNavigation.setOnItemSelectedListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.nav_products -> navController.navigate(R.id.nav_products)
                        R.id.nav_scan -> navController.navigate(R.id.nav_scan)
                        R.id.nav_cart -> navController.navigate(R.id.nav_cart)
                        R.id.nav_settings -> navController.navigate(R.id.nav_settings)
                    }
                    true
                }

                // Configuration manuelle du menu latéral
                navView.setNavigationItemSelectedListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.nav_products -> navController.navigate(R.id.nav_products)
                        R.id.nav_scan -> navController.navigate(R.id.nav_scan)
                        R.id.nav_cart -> navController.navigate(R.id.nav_cart)
                        R.id.nav_settings -> navController.navigate(R.id.nav_settings)
                    }
                    drawerLayout.closeDrawers()
                    true
                }
            } catch (e: Exception) {
                // Vraiment rien à faire de plus si cela échoue
                e.printStackTrace()
            }
        }
    }

    private fun applyDarkMode() {
        try {
            val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val isDarkMode = sharedPrefs.getBoolean("dark_mode", false)

            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        } catch (e: Exception) {
            // Log l'erreur mais ne pas planter l'application
            e.printStackTrace()
            // En cas d'erreur d'initialisation, réessayer avec une approche plus simple
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
            val navController = navHostFragment?.findNavController()
            navController?.let {
                val drawerLayout: DrawerLayout = binding.drawerLayout
                val navView: NavigationView = binding.navView

                navView.setNavigationItemSelectedListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.nav_products -> it.navigate(R.id.nav_products)
                        R.id.nav_scan -> it.navigate(R.id.nav_scan)
                        R.id.nav_cart -> it.navigate(R.id.nav_cart)
                        R.id.nav_settings -> it.navigate(R.id.nav_settings)
                    }
                    drawerLayout.closeDrawers()
                    true
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
package fr.epf.min2.projet_ecommerce

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import fr.epf.min2.projet_ecommerce.databinding.ActivityMainBinding
import fr.epf.min2.projet_ecommerce.util.ThemeManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applyTheme()

        // Cacher la barre de titre
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adjustStatusBar()

        try {
            val navController = findNavController(R.id.nav_host_fragment_content_main)
            binding.appBarMain.bottomNavigation.setupWithNavController(navController)

            navController.addOnDestinationChangedListener { _, destination, _ ->
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
            e.printStackTrace()

            try {
                val navHostFragment = supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
                val navController = navHostFragment.navController

                binding.appBarMain.bottomNavigation.setOnItemSelectedListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.nav_products -> navController.navigate(R.id.nav_products)
                        R.id.nav_scan -> navController.navigate(R.id.nav_scan)
                        R.id.nav_cart -> navController.navigate(R.id.nav_cart)
                        R.id.nav_settings -> navController.navigate(R.id.nav_settings)
                    }
                    true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun applyTheme() {
        try {
            ThemeManager.applyTheme(this)
            updateNavigationBarColors()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateNavigationBarColors() {
        try {
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun adjustStatusBar() {
        // Cache complètement les barres système (barre d'état et barre de navigation)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }
}
package fr.epf.min2.projet_ecommerce.ui.settings

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import fr.epf.min2.projet_ecommerce.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuration du switch de thème
        setupThemeSwitch()

        // Désactiver temporairement la fonctionnalité de thème de couleur
        // La ligne suivante est commentée car colorThemeCard n'existe pas dans le layout
        // binding.colorThemeCard.visibility = View.GONE
    }

    private fun setupThemeSwitch() {
        val sharedPrefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("dark_mode", false)

        binding.themeSwitch.isChecked = isDarkMode

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("dark_mode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        binding.themeCard.setOnClickListener {
            binding.themeSwitch.isChecked = !binding.themeSwitch.isChecked
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package fr.epf.min2.projet_ecommerce.ui.settings

import android.content.Context
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.epf.min2.projet_ecommerce.R
import fr.epf.min2.projet_ecommerce.databinding.FragmentSettingsBinding
import fr.epf.min2.projet_ecommerce.util.ThemeManager

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

        // Configuration de la sélection de thème de couleur
        setupColorThemeSelection()
    }

    private fun setupThemeSwitch() {
        val sharedPrefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("dark_mode", false)

        binding.themeSwitch.isChecked = isDarkMode

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            ThemeManager.setDarkMode(requireActivity(), isChecked)
        }

        binding.themeCard.setOnClickListener {
            binding.themeSwitch.isChecked = !binding.themeSwitch.isChecked
        }
    }

    private fun setupColorThemeSelection() {
        val currentTheme = ThemeManager.getCurrentColorTheme(requireContext())

        // Récupérer les noms des thèmes depuis les ressources
        val themeNames = resources.getStringArray(R.array.theme_names)
        val themeValues = resources.getStringArray(R.array.theme_values)

        // Créer les boutons radio pour chaque thème disponible
        binding.themeRadioGroup.removeAllViews()
        for (i in themeNames.indices) {
            val radioButton = RadioButton(requireContext())
            radioButton.text = themeNames[i]
            radioButton.id = View.generateViewId()
            radioButton.isChecked = themeValues[i] == currentTheme
            binding.themeRadioGroup.addView(radioButton)
        }

        // Configurer le bouton d'application du thème
        binding.applyThemeButton.setOnClickListener {
            val selectedId = binding.themeRadioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedRadioButton = requireView().findViewById<RadioButton>(selectedId)
                val index = binding.themeRadioGroup.indexOfChild(selectedRadioButton)
                if (index >= 0 && index < themeValues.size) {
                    val selectedTheme = themeValues[index]
                    applyColorTheme(selectedTheme)
                }
            }
        }
    }

    private fun applyColorTheme(themeName: String) {
        ThemeManager.setColorTheme(requireContext(), themeName)
        ThemeManager.applyColorTheme(requireActivity(), themeName)

        // Afficher un message de confirmation
        Toast.makeText(requireContext(), R.string.theme_applied, Toast.LENGTH_SHORT).show()

        // Recréer l'activité pour appliquer complètement le nouveau thème
        requireActivity().recreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
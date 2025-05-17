package fr.epf.min2.projet_ecommerce.ui.scan

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import fr.epf.min2.projet_ecommerce.R
import fr.epf.min2.projet_ecommerce.databinding.FragmentScanBinding

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startQrCodeScanner()
        } else {
            Toast.makeText(
                requireContext(),
                "Permission caméra nécessaire pour scanner un QR code",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private val qrCodeScannerLauncher = registerForActivityResult(
        ScanContract()
    ) { result ->
        if (result.contents == null) {
            Toast.makeText(requireContext(), "Scan annulé", Toast.LENGTH_LONG).show()
        } else {
            try {
                val productId = result.contents.toInt()
                val bundle = Bundle().apply {
                    putInt("productId", productId)
                }
                findNavController().navigate(R.id.nav_product_detail, bundle)
            } catch (e: NumberFormatException) {
                Toast.makeText(
                    requireContext(),
                    "Format de QR Code invalide. Attendu: ID produit (nombre)",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.scanButton.setOnClickListener {
            checkCameraPermissionAndStartScanner()
        }
    }

    private fun checkCameraPermissionAndStartScanner() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startQrCodeScanner()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Toast.makeText(
                    requireContext(),
                    "Permission caméra nécessaire pour scanner un QR code",
                    Toast.LENGTH_LONG
                ).show()
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }

            else -> {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun startQrCodeScanner() {
        val options = ScanOptions()
            .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            .setPrompt("Alignez le QR code à l'intérieur du cadre")
            .setCameraId(0)
            .setBeepEnabled(false)
            .setBarcodeImageEnabled(true)
            .setOrientationLocked(true)
            .setCaptureActivity(CaptureActivityPortrait::class.java)
        qrCodeScannerLauncher.launch(options)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
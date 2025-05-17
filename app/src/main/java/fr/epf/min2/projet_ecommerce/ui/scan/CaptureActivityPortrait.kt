package fr.epf.min2.projet_ecommerce.ui.scan

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageButton
import fr.epf.min2.projet_ecommerce.R
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.google.zxing.BarcodeFormat

/**
 * Cette activité personnalisée permet de scanner des QR codes en mode portrait
 * avec un bouton retour en haut à gauche.
 */
class CaptureActivityPortrait : CaptureActivity() {
    private lateinit var barcodeView: DecoratedBarcodeView

    private val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult?) {
            result?.let {
                barcodeView.pause()
                returnResult(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_portrait)

        // Initialiser la vue de scan
        barcodeView = findViewById(R.id.barcode_scanner)
        barcodeView.decoderFactory = DefaultDecoderFactory(listOf(BarcodeFormat.QR_CODE))
        barcodeView.initializeFromIntent(intent)
        barcodeView.decodeContinuous(callback)

        // Configurer le bouton retour
        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()  // Fermer l'activité
        }
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    private fun returnResult(result: BarcodeResult) {
        val intent = intent
        intent.putExtra("SCAN_RESULT", result.text)
        intent.putExtra("SCAN_RESULT_FORMAT", result.barcodeFormat.toString())
        setResult(RESULT_OK, intent)
        finish()
    }
}
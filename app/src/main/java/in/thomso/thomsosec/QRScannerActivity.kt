package `in`.thomso.thomsosec

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

class QRScannerActivity : AppCompatActivity() {

    private val RECORD_REQUEST_CODE = 101
    private val TAG = "Error TAG "

    private lateinit var cameraSurface: SurfaceView
    private lateinit var camera: CameraSource
    private lateinit var scanner: BarcodeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscanner)

        initView()

        scanner = BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build()

        camera = CameraSource.Builder(this, scanner)
                .setRequestedPreviewSize(640, 480)
                .setAutoFocusEnabled(true).build()

        cameraSurface.holder.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
                try {
                    camera.start(p0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun surfaceDestroyed(p0: SurfaceHolder?) {
            }

            override fun surfaceCreated(p0: SurfaceHolder?) {
            }

        })

        scanner.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
            }

            override fun receiveDetections(p0: Detector.Detections<Barcode>?) {
                val qr = p0?.detectedItems
                if(qr?.size()!=0){
                    val vibrator:Vibrator = applicationContext
                            .getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(100)
                    Log.e("VALUE: ", qr!!.valueAt(0).displayValue)

                    val intent = Intent(this@QRScannerActivity,MainActivity::class.java)
                    intent.putExtra("qr",qr.valueAt(0).displayValue)
                    startActivity(intent)
                    camera.stop()
                    this@QRScannerActivity.finish()
//                    cameraSurface.post {
//                        val vibrator:Vibrator = applicationContext
//                                .getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//                        vibrator.vibrate(100)
//                        Log.e("VALUE: ", qr!!.valueAt(0).displayValue)
//                        val intent = Intent(this@QRScannerActivity,MainActivity::class.java)
//                        startActivity(intent)
//                    }
                }
            }

        })
    }


    private fun initView() {
        setupPermissions()
        cameraSurface = findViewById(R.id.camera_sv)
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                RECORD_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                }
            }
        }
    }
}

package `in`.thomso.thomsosec

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.google.zxing.integration.android.IntentIntegrator
import id.zelory.compressor.Compressor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File

class MainActivity : AppCompatActivity() {

    private var image:com.esafirm.imagepicker.model.Image? = null

    private lateinit var nameTV:EditText
    private lateinit var mailTV:EditText
    private lateinit var contactTV:EditText
    private lateinit var organizationTV:EditText
    private lateinit var queryTV:EditText
    private lateinit var scanBTN:Button
    private lateinit var imagePickBTN:Button
    private lateinit var pickedImage:ImageView
    private lateinit var sendBTN:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        intiView()
        val inspector = HttpLoggingInterceptor()
        inspector.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(inspector).build()
        val service = Retrofit.Builder()
                .baseUrl("https://thomso.in/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client).build()
                .create(Service::class.java)

        scanBTN.setOnClickListener {
//            val intent = Intent(this,QRScannerActivity::class.java)
//            startActivityForResult(intent,707)
              IntentIntegrator(this).initiateScan()
        }

        imagePickBTN.setOnClickListener {
            ImagePicker.create(this).returnMode(ReturnMode.ALL)
                    .toolbarFolderTitle("Folder") // folder selection title
                    .toolbarImageTitle("Tap to select") // image selection title
                    .toolbarArrowColor(Color.WHITE) // Toolbar 'up' arrow color
                    .single() // single mode
                    .start()
        }

        sendBTN.setOnClickListener {
            if (checkValidations()==0){
                return@setOnClickListener
            }
            if (image!=null){
                val temp = File(image?.path)
                val file = Compressor(this).compressToFile(temp)
                val img64 = BitmapFactory.decodeFile(file.absolutePath)
                val outputStream = ByteArrayOutputStream()
                img64.compress(Bitmap.CompressFormat.JPEG,20,outputStream)

                val b = outputStream.toByteArray()
                //val l = outputStream.size()
                var encode_image = ""
               // val sb = StringBuilder(encode_image)
                encode_image += Base64.encodeToString(b, Base64.DEFAULT)
                Log.e("IMAGE: ",encode_image)
               // val reqFile = RequestBody.create(MediaType.parse("image/*"),compressedFile)
               // val body = MultipartBody.Part.createFormData("img",file.name,reqFile)
                val name = nameTV.text.toString()
                val email = mailTV.text.toString()
                val contact = contactTV.text.toString()
                val organization = organizationTV.text.toString()
                val qr = queryTV.text.toString()
                val format = "jpeg"
                val data = Data(encode_image,name,email,contact,organization, qr, format)

                val req = service.postImage(data)

                req.enqueue(object :Callback<`in`.thomso.thomsosec.Response>{
                    override fun onFailure(call: Call<`in`.thomso.thomsosec.Response>, t: Throwable) {
                        Log.e("ERROR",t.message)
                    }

                    override fun onResponse(call: Call<`in`.thomso.thomsosec.Response>, response: Response<`in`.thomso.thomsosec.Response>) {
                      //  Log.e("Success: ",response.body().toString())
                        if (response.body()!=null&&response.body()!!.success!!){
                            showAlert(response.body()!!.msg!!,"Success")
                            setEveryThingEmpty()
                        }else {
                            showAlert(response.body()!!.msg!!,"ERROR")
                        }
                        //if(response.body().toString().contentEquals("success"))
                    }

                })
            }
        }
    }

    private fun setEveryThingEmpty() {
        nameTV.setText("")
        mailTV.setText("")
        contactTV.setText("")
        organizationTV.setText("")
        queryTV.setText("")
        image = null
        pickedImage.visibility = View.GONE
    }

    private fun checkValidations(): Int {
        when {
            nameTV.text.toString() == "" -> {
                showAlert("Enter Name","ERROR")
                return 0
            }
            contactTV.text.toString()=="" -> {
                showAlert("Enter Contact Details","ERROR")
                return 0
            }
            mailTV.text.toString() == "" -> {
                showAlert("Enter Mail","ERROR")
                return 0
            }
            organizationTV.text.toString() == "" -> {
                showAlert("Enter Organization","ERROR")
                return 0
            }
            queryTV.text.toString() == "" -> {
                showAlert("Scan QR Code","ERROR")
                return 0
            }
            image == null -> {
                showAlert("Select Image","ERROR")
                return 0
            }
            else -> return 1
        }
    }

    private fun showAlert(s: String,title:String) {
        val alert = android.support.v7.app.AlertDialog.Builder(this)
        alert.setTitle(title)
                .setMessage(s)
                .setPositiveButton("ok") { dialogInterface, i -> dialogInterface.dismiss()
                }.show()
    }

    private fun intiView() {
        nameTV = findViewById(R.id.name_tv)
        mailTV = findViewById(R.id.mail_tv)
        contactTV = findViewById(R.id.contact_tv)
        organizationTV = findViewById(R.id.org_tv)
        queryTV = findViewById(R.id.query_tv)
        scanBTN = findViewById(R.id.scan_btn)
        imagePickBTN = findViewById(R.id.image_pic_btn)
        pickedImage = findViewById(R.id.picked_iv)
        sendBTN = findViewById(R.id.send_btn)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      //  Log.e("ACTIVE: ",data.toString())
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // or get a single image only
//            pickedImage.visibility=View.VISIBLE
            image = ImagePicker.getFirstImageOrNull(data)
            if(image!=null){
                pickedImage.visibility = View.VISIBLE
                Glide.with(this).load(image!!.path).into(pickedImage)
            }
        }else{
            val intentRes = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
            if (intentRes!=null){
                if (intentRes.contents!=null){
                    queryTV.setText(intentRes.contents)
                }
            }
        }
//        else if (data!!.hasExtra("qr")&&requestCode==707&&resultCode==Activity.RESULT_OK){
//            Log.e("DATA RECIEVED", data.getStringExtra("qr"))
//            //queryTV.setText(data.extras.get("qr").toString())
//            queryTV.setText("text")
//        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}

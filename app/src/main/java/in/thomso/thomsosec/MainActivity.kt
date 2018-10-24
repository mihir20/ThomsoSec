package `in`.thomso.thomsosec

import android.content.Intent
import android.graphics.Color
import android.media.Image
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import id.zelory.compressor.Compressor
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
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
            val intent = Intent(this,QRScannerActivity::class.java)
            startActivityForResult(intent,100)
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
            if (image!=null){
                val file = File(image?.path)
                val compressedFile = Compressor(this).compressToFile(file)
                val reqFile = RequestBody.create(MediaType.parse("image/*"),compressedFile)
                val body = MultipartBody.Part.createFormData("image",file.name,reqFile)
                val name = RequestBody.create(MediaType.parse("text/plain"),"xxx")
                val email = RequestBody.create(MediaType.parse("text/plain"),"xxx")
                val contact = RequestBody.create(MediaType.parse("text/plain"),"xxx")
                val organization = RequestBody.create(MediaType.parse("text/plain"),"xxx")
                val qr = RequestBody.create(MediaType.parse("text/plain"),"xxx")
                val format = RequestBody.create(MediaType.parse("text/plain"),"jpg")

                val req = service.postImage(body,name,email,contact,organization, qr, format)

                req.enqueue(object :Callback<ResponseBody>{
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("ERROR",t.message)
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        Log.e("Success: ",response.toString())                    }

                })
            }
        }
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
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // or get a single image only
            image = ImagePicker.getFirstImageOrNull(data)
            if(image!=null){
              //  pickedImage.visibility = View.VISIBLE
                Glide.with(this).load(image!!.path).into(pickedImage)
            }
        }
        else if (requestCode==100){
            Log.e("DATA RECIEVED", data!!.getStringExtra("qr"))
            queryTV.setText(data.getStringExtra("qr"))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}

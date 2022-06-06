package com.example.nodrama.view

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.nodrama.R
import com.example.nodrama.databinding.ActivityArtBinding
import com.example.nodrama.viewmodel.ARTViewModel
import com.example.nodrama.viewmodel.LoginRegisterViewModel
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ARTActivity : AppCompatActivity() {
    private lateinit var loginRegisterViewModel: LoginRegisterViewModel
    private lateinit var artViewModel: ARTViewModel
    private lateinit var binding: ActivityArtBinding
    companion object {
        var resultOption: String = "Please try again"
        var selectedPhotoUri : Uri? = null
    }

    /**
     * Variable initialisation
     */
    private var CAMERA_PERMISSION_CODE = 123
    private var READ_STORAGE_PERMISSION_CODE = 113
    private var WRITE_STORAGE_PERMISSION_CODE = 113

    private var index = 0
    private var confidence = 0f

    private val TAG = "ARTtest"

    private lateinit var cameraLauncher : ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher : ActivityResultLauncher<Intent>

    lateinit var inputImage:InputImage
    lateinit var imageLabeler: ImageLabeler
    lateinit var result:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_art)
        binding = ActivityArtBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * Retrieve the Health Risk Warning dropdown list
         */
        val HRWList = resources.getStringArray(R.array.HRWList)

        val userDataMap = loadUserData()

        val localModel = LocalModel.Builder()
            .setAssetFilePath("automl/model.tflite")
            .build()

        val customImageLabelerOptions = CustomImageLabelerOptions.Builder(localModel)
            .setConfidenceThreshold(0.5f)
            .setMaxResultCount(5)
            .build()
         imageLabeler = ImageLabeling.getClient(customImageLabelerOptions)

        loginRegisterViewModel = ViewModelProvider(this).get(
            LoginRegisterViewModel::class.java
        )

        artViewModel = ViewModelProvider(this).get(
            ARTViewModel::class.java
        )
        retrieveData()

        var HRWAnswer = "No"

        /**
         * Spinner to select HRW option
         */
        val spinnerHRW = findViewById<Spinner>(R.id.spinnerHRW)
        if (spinnerHRW != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, HRWList
            )
            spinnerHRW.adapter = adapter
            spinnerHRW.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    HRWAnswer = HRWList[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }

        /**
         * Create camera
         */
        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val data = result?.data
            try {
                val photo = data?.extras?.get("data") as Bitmap
                selectedPhotoUri = getImageUriFromBitmap(this, photo)
                binding.cameraImageView.setImageBitmap(photo)
                inputImage = InputImage.fromBitmap(photo, 0)
                resultOption = processImage()
            } catch (e: Exception) {
                Log.d(TAG, "onActivityResult: ${e.message}")
            }
        }

        /**
         * Create photo gallery
         */
        galleryLauncher=registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val data = result?.data
            if(data!=null){
                selectedPhotoUri = data.data
            }
            try {
                inputImage = InputImage.fromFilePath(this@ARTActivity, data?.data!!)
                Log.d("HENRY", inputImage.toString())
                binding.cameraImageView.setImageURI(data?.data)
                resultOption = processImage()
            } catch (e: Exception) {

            }
        }

        /**
         * Launch the camera
         */
        binding.cameraButton.setOnClickListener {
            val options = arrayOf("Take Picture", "Gallery")
            val builder = AlertDialog.Builder(this@ARTActivity)
            builder.setTitle("Pick a option")
            builder.setItems(options, DialogInterface.OnClickListener{
                    dialog, which ->
                if (which==0){
                    val cameraIntent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    cameraLauncher.launch(cameraIntent)
                }else{
                    val storageIntent = Intent()
                    storageIntent.setType("image/*")
                    storageIntent.setAction(Intent.ACTION_GET_CONTENT)
                    galleryLauncher.launch(storageIntent)
                }
            })
            builder.show()
        }

        /**
         * On click listener for to submit ART form details into the database
         */
        binding.buttonARTSubmit.setOnClickListener {
            submitART(userDataMap.get("userId").toString(), HRWAnswer)
        }
    }

    override fun onResume(){
        super.onResume()
        checkPermission(android.Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE)
    }

    /**
     * Check for user's permission to launch the camera
     */
    private fun checkPermission(permission:String, requestCode: Int){
        if(ContextCompat.checkSelfPermission(this@ARTActivity, permission) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this@ARTActivity, arrayOf(permission), requestCode)
            }
    }

    /**
     * Denied Permission
     */
    override fun onRequestPermissionsResult(requestCode:Int, permissions: Array<out String>, grantResults: IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, READ_STORAGE_PERMISSION_CODE)
            }else{
                Toast.makeText(this@ARTActivity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }else if(requestCode == READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE_PERMISSION_CODE)
            }else{
                Toast.makeText(this@ARTActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }else if(requestCode == WRITE_STORAGE_PERMISSION_CODE) {
            if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                checkPermission(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE, WRITE_STORAGE_PERMISSION_CODE)
            }else{
                Toast.makeText(this@ARTActivity, "Storage Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    /**
     * Process the ART result image taken using the camera or uploaded from the gallery
     * to identify if the ART result on image is positive or negative
     */
    fun processImage(): String{
        var returnResult = ""
        imageLabeler.process(inputImage).addOnSuccessListener {
            for (label in it) {
                Log.d("WOTDA", label.index.toString())
                Log.d("WOTDA", label.confidence.toString())

                if(label.confidence > confidence){
                    index = 0
                    confidence = 0f
                    index = label.index
                    confidence = label.confidence
                }
            }

            /**
             * Positive ART Result
             */
            if(confidence > 0.65 && index == 0 ){
                returnResult = "Your status is Positive"
                result = "Positive"
                binding.tvTextView.text =  "Your status is "+ result
                val redColorValue: Int = Color.RED
                binding.tvTextView.setTextColor(redColorValue)

            }
            /**
             * Negative ART Result
             */
            else if(confidence > 0.65 && index == 1 ) {
                returnResult = "Your status is Negative"
                result = "Negative"
                binding.tvTextView.text =  "Your status is " + result
                val greenColorValue: Int = Color.GREEN
                binding.tvTextView.setTextColor(greenColorValue)

            }
            /**
             * Invalid Result
             */
            else{
                result = "Unknown"
                returnResult = "Please capture again"
                result = "Unknown"
                binding.tvTextView.text =  "Please capture again"
            }
        }.addOnFailureListener{
            Log.d(TAG, "processImage ${it.message}")
        }
        return result
    }

    /**
     * Submit ART result form details into the database
     */
    fun submitART(userId : String, HRWOption: String){

        val fullName = binding.textViewEmployeeName.getEditText()?.getText().toString().trim()
        val phoneNumber = binding.textViewEmployeeNumber.getEditText()?.getText().toString().trim()

        if(resultOption == "Please capture again") {
            Toast.makeText(applicationContext, "Image of your ART result is required!", Toast.LENGTH_LONG).show()
        }
        else
        {
            Toast.makeText(this, "Form has been submitted successfully", Toast.LENGTH_LONG).show()
            val intentART = Intent(this, ArtHomeActivity::class.java)
            startActivity(intentART)
            finish()
        }

        val saveTime = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneOffset.UTC)
            .format(Instant.now())

        Log.d("ARTSubmissionTest", result)
        saveArtData(userId, fullName, phoneNumber, HRWOption, result, saveTime, selectedPhotoUri)
    }

    /**
     * Retrieve logged in user's information such as Name and Phone number
     */
    fun retrieveData(){
        loginRegisterViewModel?.getUserDetailsLiveData()?.observe(this) {
            Log.d("Account Retrieved User", it.toString())
            if (it != null) {
                val fullname = it.fullname
                val phone = it.phoneNum

                binding.textViewEmployeeName.getEditText()?.setText(fullname.toString())
                binding.textViewEmployeeNumber.getEditText()?.setText(phone.toString())
            }
        }
    }

    /**
     * Load User Data
     */
    fun loadUserData (): Map<String, String?>{
        val sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userid", null)
        val exampleKey = sharedPreferences.getString("examplekey", null)
        val map = mapOf("userId" to userId, "somethingelse" to exampleKey)
        return map
    }

    /**
     * Save ART Data
     */
    fun saveArtData ( userId : String, fullName : String, phoneNumber : String, HRWOption :String, ResultOption :String, saveTime :String, imageUri : Uri?){
        if(imageUri!=null) {
            artViewModel.saveDataToRepo(
                userId,
                fullName,
                phoneNumber,
                HRWOption,
                ResultOption,
                saveTime,
                imageUri
            )
        }else{
            Toast.makeText(this, "Cannot save art", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Retrieve Image URI from Bitmap
     */
    fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }
}
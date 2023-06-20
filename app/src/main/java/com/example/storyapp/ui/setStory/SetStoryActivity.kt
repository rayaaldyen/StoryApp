package com.example.storyapp.ui.setStory

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils.isEmpty
import android.text.TextWatcher
import android.view.View
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivitySetStoryBinding
import com.example.storyapp.ui.custom.CustomButton
import com.example.storyapp.ui.main.MainActivity
import com.example.storyapp.utils.uriToFile
import com.google.android.gms.location.LocationServices
import java.io.File

class SetStoryActivity : AppCompatActivity() {
    private lateinit var setStoryViewModel: SetStoryViewModel
    private var getFile: File? = null
    private var _binding: ActivitySetStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var uploadButton: CustomButton

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var locationSwitch: Switch

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.permission_rejected),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySetStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uploadButton = binding.uploadButton

        locationSwitch = binding.locationSwitch

        setStoryViewModel = ViewModelProvider(
            this
        )[SetStoryViewModel::class.java]

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.cameraButton.setOnClickListener { startTakePhoto() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener {
            if (locationSwitch.isChecked) {
                uploadImageWithLocation()
            } else {
                uploadImageWithoutLocation()
            }
        }
        uploadButtonSetting()
    }

    private fun uploadImageWithLocation() {
        val description = binding.storyEditText.text.toString()
        if (getFile != null && !isEmpty(description)) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            if (ContextCompat.checkSelfPermission(
                    this.applicationContext,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            val latitude = location.latitude
                            val longitude = location.longitude
                            setStoryViewModel.setStory(getFile!!, description, latitude, longitude)
                        }
                    }
            }
            setStoryViewModel.error.observe(this) {
                if (it == true) {
                    errorMessage()
                }
            }
            setStoryViewModel.isLoading.observe(this) {
                showLoading(it)
                if (it == false) {
                    val intent = Intent(this@SetStoryActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        } else if (getFile == null && !isEmpty(description)) {
            Toast.makeText(
                this@SetStoryActivity,
                getString(R.string.null_photo_warning),
                Toast.LENGTH_SHORT
            ).show()
        } else if (getFile != null && isEmpty(description)) {
            Toast.makeText(
                this@SetStoryActivity,
                getString(R.string.null_desc_warning),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this@SetStoryActivity,
                getString(R.string.null_story_upload),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun uploadImageWithoutLocation() {
        val description = binding.storyEditText.text.toString()
        if (getFile != null && !isEmpty(description)) {
            setStoryViewModel.setStory(getFile!!, description, 0.0, 0.0)
            setStoryViewModel.error.observe(this) {
                if (it == true) {
                    errorMessage()
                }
            }
            setStoryViewModel.isLoading.observe(this) {
                showLoading(it)
                if (it == false) {
                    val intent = Intent(this@SetStoryActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        } else if (getFile == null && !isEmpty(description)) {
            Toast.makeText(
                this@SetStoryActivity,
                getString(R.string.null_photo_warning),
                Toast.LENGTH_SHORT
            ).show()
        } else if (getFile != null && isEmpty(description)) {
            Toast.makeText(
                this@SetStoryActivity,
                getString(R.string.null_desc_warning),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this@SetStoryActivity,
                getString(R.string.null_story_upload),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.progressBar.bringToFront()
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }


    private fun uploadButtonSetting() {
        val descEt = binding.storyEditText
        val descText = descEt.text
        descEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.uploadButton.isEnabled =
                    descText != null && descText.toString().isNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

    }

    private fun errorMessage() {
        Toast.makeText(
            this@SetStoryActivity,
            getString(R.string.error_message),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@SetStoryActivity)
            getFile = myFile
            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        com.example.storyapp.utils.createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@SetStoryActivity,
                "com.example.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val isBackCamera = true
            if (isBackCamera) {
                val result = BitmapFactory.decodeFile(myFile.path)
                binding.previewImageView.setImageBitmap(result)
            } else {
                val result =
                    BitmapFactory.decodeFile(myFile.path)
                binding.previewImageView.setImageBitmap(result)
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
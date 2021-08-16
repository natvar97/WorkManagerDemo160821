package com.indialone.workmanagerdemo160821

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anggrayudi.storage.SimpleStorage
import com.anggrayudi.storage.SimpleStorageHelper
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import com.indialone.workmanagerdemo160821.databinding.ActivityMainBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener


class MainActivity : AppCompatActivity(), View.OnClickListener, PickiTCallbacks {

    private val CAMERA_ID: Int = 112
    private val GALLERY: Int = 113
    private val MEDIA_STORE_GALLERY: Int = 114
    private val PDF_CODE = 111
    private val PDF_FOLDER = 110
    private lateinit var mBinding: ActivityMainBinding
    private var pdfFilePath = ""
    private var folderPath = ""
    private var isFolder = false
    private val pickIt = PickiT(this, this, this)
    private val storageHelper = SimpleStorageHelper(this)
    private val storage = SimpleStorage(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        // now in android 11 you can't create your custom toast message you should use default toast message
        Toast.makeText(this@MainActivity, "Welcome to MainActivity", Toast.LENGTH_SHORT).show()

//        mBinding.btnOpenCamera.setOnClickListener(this)
//        mBinding.btnOpenGallery.setOnClickListener(this)
//        mBinding.btnOpenGalleryMediastore.setOnClickListener(this)
        mBinding.btnOpenPdfs.setOnClickListener(this)

        storageHelper.onFileSelected = { requestCode, files ->
            for (file in files) {
//                pickIt.getPath(file.uri, Build.VERSION.SDK_INT)
                val intent = Intent(this@MainActivity, PdfActivity::class.java)
                intent.putExtra("pdfFilePath", file.uri.toString())
                startActivity(intent)
            }
        }

    }

    private fun requestCameraPermission() {
        Dexter.withContext(this@MainActivity)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    openCamera()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(
                        this@MainActivity,
                        "Permissioin is needed for continue",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    request: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }

            }).check()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_ID)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == CAMERA_ID) {
//                if (data != null) {
//                    val imageUri = data.extras!!.get("data") as Bitmap
//                    Log.e("imageUri", "$imageUri")
//                }
//            }
            if (requestCode == PDF_FOLDER) {
                if (data != null) {
                    pickIt.getPath(data.data!!, Build.VERSION.SDK_INT)
                    isFolder = true
//                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//                    intent.setDataAndType(Uri.parse(folderPath), "application/pdf")
//                    startActivityForResult(intent, PDF_CODE)
                    storageHelper.openFilePicker()
                }
            }
            if (requestCode == PDF_CODE) {
                if (data != null) {
                    pickIt.getPath(data.data!!, Build.VERSION.SDK_INT)
                    Log.e("data.data", "${data.data!!}")
                    Log.e("pdfFilePath", pdfFilePath)
                    val intent = Intent(this@MainActivity, PdfActivity::class.java)
                    intent.putExtra("pdfFilePath", pdfFilePath)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
//            R.id.btn_open_camera -> {
//                requestCameraPermission()
//            }
//            R.id.btn_open_gallery -> {
//                requestGalleryPermission(GALLERY)
//            }
//            R.id.btn_open_gallery_mediastore -> {
//                requestGalleryPermission(MEDIA_STORE_GALLERY)
//            }
            R.id.btn_open_pdfs -> {
                requestGalleryPermission()
            }
        }
    }

    private fun requestGalleryPermission() {
        Dexter.withContext(this@MainActivity)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    storageHelper.openFilePicker(storage.requestCodeFilePicker)
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1!!.continuePermissionRequest()
                }

            }).check()
    }

    private fun openPdfs() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.setType("application/pdf")
        startActivityForResult(intent, PDF_CODE)

//        val sm = getSystemService(Context.STORAGE_SERVICE) as StorageManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//
//            val intent = sm.primaryStorageVolume.createOpenDocumentTreeIntent()
//            val startDir = "Documents"
//
//            var uri: Uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI")!!
//
//            var scheme: String = uri.toString()
//
//            Log.d("scheme", "INITIAL_URI scheme: $scheme")
//
//            scheme = scheme.replace("/root/", "/document/")
//
//            scheme += "%3A$startDir"
//
//            uri = Uri.parse(scheme)
//
//            intent.putExtra("android.provider.extra.INITIAL_URI", uri)
//
//            Log.d("uri", "uri: " + uri.toString())
//
//            startActivityForResult(intent, PDF_FOLDER)
        }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setType("image/*")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun openMediaStoreGallery() {
        val intent = Intent(Intent.ACTION_VIEW, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivity(intent)
    }

    override fun PickiTonUriReturned() {

    }

    override fun PickiTonStartListener() {

    }

    override fun PickiTonProgressUpdate(progress: Int) {

    }

    override fun PickiTonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
//        if (isFolder) {
//            folderPath = path!!
//            isFolder = false
//        }
//        else {
            pdfFilePath = path!!
//        }
    }

//    override fun onBackPressed() {
//        pickIt.deleteTemporaryFile(this)
//        super.onBackPressed()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        if (!isChangingConfigurations) {
//            pickIt.deleteTemporaryFile(this)
//        }
//    }

}
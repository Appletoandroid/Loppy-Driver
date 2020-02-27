package com.appleto.loppydriver.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.appleto.loppydriver.R
import com.appleto.loppydriver.helper.Utils
import com.appleto.loppydriver.viewModel.RegisterViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(p0: View?) {
        when (p0) {
            btnSignUp -> {
                if (!Utils.isEmptyEditText(edtFirstName, "Please enter first name", cardFirstName)
                    && !Utils.isEmptyEditText(edtLastName, "Please enter last name", cardLastName)
                    && !Utils.isEmptyEditText(edtEmail, "Please enter Email", cardEmail)
                    && !Utils.isEmptyEditText(edtPhone, "Please enter phone", cardPhone)
                    && !Utils.isEmptyEditText(edtPassword, "Please enter password", cardPassword)
                    && !Utils.isValidateEmail(edtEmail, "Enter valid email address", cardEmail)
                    && !Utils.isValidatePhone(edtPhone, "Enter valid phone number", cardPhone)
                ) {
                    if (licenceImageFile == null) {
                        Utils.showSnackBar(root, "Please select licence image")
                    } else {
                        register()
                    }
                }
            }
            cardLicenceImage -> {
                ImagePicker.with(this)
                    .crop()                    //Crop image(Optional), Check Customization for more option
                    .compress(1024)            //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(
                        1080,
                        1080
                    )    //Final image resolution will be less than 1080 x 1080(Optional)
                    .start()
            }
        }
    }

    private var viewModel: RegisterViewModel? = null
    private var licenceImageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setSupportActionBar(toolbar)

        viewModel = ViewModelProvider(
            this
        ).get(RegisterViewModel::class.java)

        viewModel?.response?.observe(this, Observer {
            if (it?.has("message")!!) {
                Toast.makeText(this, it.get("message").asString, Toast.LENGTH_LONG).show()
                startActivity(
                    Intent(
                        this,
                        SuccessActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        })

        btnSignUp.setOnClickListener(this)
        cardLicenceImage.setOnClickListener(this)
    }

    private fun register() {
        val sendData = HashMap<String, RequestBody>()
        sendData["email"] =
            RequestBody.create(
                MediaType.parse("text/plain"),
                edtEmail.text.toString().trim()
            )
        sendData["phone"] =
            RequestBody.create(
                MediaType.parse("text/plain"),
                edtPhone.text.toString().trim()
            )
        sendData["password"] =
            RequestBody.create(
                MediaType.parse("text/plain"),
                edtPassword.text.toString().trim()
            )
        sendData["name"] =
            RequestBody.create(
                MediaType.parse("text/plain"),
                edtFirstName.text.toString().trim() + " " + edtLastName.text.toString().trim()
            )
        sendData["user_type"] =
            RequestBody.create(
                MediaType.parse("text/plain"),
                "driver"
            )
        sendData["fcm_token"] =
            RequestBody.create(
                MediaType.parse("text/plain"),
                "123456"
            )

        val selectedID = rgGender.checkedRadioButtonId
        val radioButton = findViewById<RadioButton>(selectedID)
        var gender = "male"
        if (radioButton.text.toString().trim() == "Male") {
            gender = "male"
        } else {
            gender = "female"
        }
        sendData["gender"] =
            RequestBody.create(
                MediaType.parse("text/plain"),
                gender
            )
        val licenceImage = MultipartBody.Part.createFormData(
            "license_image",
            licenceImageFile?.name,
            RequestBody.create(MediaType.parse("image/*"), licenceImageFile)
        )
//        sendData["file\"; filename=\"" + licenceImageFile?.name] =
//            RequestBody.create(
//                MediaType.parse("image/*"),
//                licenceImageFile
//            )

        viewModel?.register(this, sendData, licenceImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri = data?.data
            licenceImage.setImageURI(fileUri)
            tvAddLicenceImage.visibility = View.GONE

            //You can get File object from intent
            licenceImageFile = ImagePicker.getFile(data)

            //You can also get File Path from intent
            val filePath: String? = ImagePicker.getFilePath(data)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Utils.showSnackBar(root, ImagePicker.getError(data))
        } else {
            Utils.showSnackBar(root, "Task Cancelled")
        }
    }
}

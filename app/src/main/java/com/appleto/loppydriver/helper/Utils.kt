package com.appleto.loppydriver.helper

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Patterns
import android.view.View
import android.view.Window
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textfield.TextInputEditText
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import com.appleto.loppydriver.R
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


object Utils {

    val isRefresh = MutableLiveData<Boolean>()
    val isRefreshNewOrder = MutableLiveData<Boolean>()
    var progressDialog: Dialog? = null

    fun isEmptyEditText(
        editText: TextInputEditText,
        message: String,
        view: View
    ): Boolean {
        if (editText.text.toString().isEmpty()) {
            editText.requestFocus()
            showSnackBar(view, message)
            makeMeShake(view, 20, 5)
            return true
        }
        return false
    }

    fun isValidateEmail(
        editText: TextInputEditText,
        message: String,
        view: View
    ): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(editText.text.toString().trim()).matches()) {
            editText.requestFocus()
            showSnackBar(view, message)
            makeMeShake(view, 20, 5)
            return true
        }
        return false
    }

    fun isValidatePhone(
        editText: TextInputEditText,
        message: String,
        view: View
    ): Boolean {
        if (!Patterns.PHONE.matcher(editText.text.toString().trim()).matches() || editText.text?.length != 10) {
            editText.requestFocus()
            showSnackBar(view, message)
            makeMeShake(view, 20, 5)
            return true
        }
        return false
    }

    fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

    fun makeMeShake(view: View, duration: Int, offset: Int): View {
        val anim = TranslateAnimation((-offset).toFloat(), offset.toFloat(), 0f, 0f)
        anim.duration = duration.toLong()
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = 5
        view.startAnimation(anim)
        return view
    }

    fun showProgress(context: Context) {
        progressDialog = Dialog(context);
        progressDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog?.setContentView(R.layout.custom_dialog_progress);
        progressDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        progressDialog?.setCancelable(false);
        progressDialog?.show()
    }

    fun hideProgress() {
        if (progressDialog != null) {
            progressDialog?.dismiss()
        }
    }

    fun formatDateTime(input: String, output: String, date: String): String {
        var newDate: Date? = null
        var returnDate = ""
        try {
            newDate = SimpleDateFormat(input, Locale.getDefault()).parse(date)
            returnDate = SimpleDateFormat(output, Locale.getDefault()).format(newDate)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return returnDate
    }
}
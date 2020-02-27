package com.appleto.loppydriver.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.appleto.loppydriver.R
import com.appleto.loppydriver.helper.Const
import com.appleto.loppydriver.helper.PrefUtils
import com.appleto.loppydriver.helper.Utils
import com.appleto.loppydriver.viewModel.VerifyOTPViewModel
import kotlinx.android.synthetic.main.activity_verify_otp.*

class VerifyOTPActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(p0: View?) {
        when (p0) {
            btnSubmit -> {
                if (from == Const.LOGIN) {
                    if(otpView.otp != ""){
                        PrefUtils.getStringValue(this, Const.USER_ID)?.let {
                            otpView.otp?.let { it1 ->
                                viewModel?.verifyOTP(
                                    this,
                                    it, it1
                                )
                            }
                        }
                    } else {
                        Utils.showSnackBar(root,"Please enter otp")
                    }
                } else {
                    startActivity((Intent(this, ResetPasswordActivity::class.java)))
                }
            }
        }
    }

    private var from: String? = ""
    private var viewModel: VerifyOTPViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)

        from = intent?.extras?.getString(Const.FROM)

        viewModel = ViewModelProvider(
            this
        ).get(VerifyOTPViewModel::class.java)

        viewModel?.response?.observe(this, Observer {
            if (it?.has("data")!!) {
                val data = it.get("data").asJsonObject
                PrefUtils.storeStringValue(this, Const.USER_ID, data.get("user_id").asString)
                PrefUtils.storeStringValue(this, Const.NAME, data.get("name").asString)
                PrefUtils.storeStringValue(this, Const.EMAIL, data.get("email").asString)
                PrefUtils.storeStringValue(
                    this,
                    Const.PROFILE_IMAGE,
                    data.get("profile_image").asString
                )
                PrefUtils.storeStringValue(this, Const.MOBILE_NO, data.get("mobile_no").asString)
                PrefUtils.storeStringValue(this, Const.USER_TYPE, data.get("user_type").asString)
                PrefUtils.storeStringValue(
                    this,
                    Const.LICENCE_IMAGE,
                    data.get("license_image").asString
                )
                PrefUtils.storeStringValue(this, Const.GENDER, data.get("gender").asString)
                PrefUtils.storeBooleanValue(this, Const.IS_LOGGED_IN, true)

                startActivity(
                    (Intent(
                        this,
                        MainActivity::class.java
                    )).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        })

        btnSubmit.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}

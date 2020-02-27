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
import com.appleto.loppydriver.helper.Utils
import com.appleto.loppydriver.viewModel.ForgotPasswordViewModel
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity(), View.OnClickListener{

    override fun onClick(v: View?) {
        when (v) {
            btnSubmit -> {
                if (!Utils.isEmptyEditText(
                        edtNewPassword,
                        "Please enter new password",
                        cardNewPassword
                    )
                    && !Utils.isEmptyEditText(
                        edtConfirmNewPassword,
                        "Please enter confirm new password",
                        cardConfirmNewPassword
                    )
                ) {
                    mobile?.let {
                        viewModel?.forgotPassword(
                            this,
                            it,
                            edtNewPassword.text.toString().trim(),
                            edtConfirmNewPassword.text.toString().trim()
                        )
                    }
                }
            }
        }
    }

    private var from: String? = ""
    private var mobile: String? = ""
    private var viewModel: ForgotPasswordViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)

        from = intent?.extras?.getString(Const.FROM)
        mobile = intent?.extras?.getString(Const.MOBILE)

        viewModel = ViewModelProvider(
            this
        ).get(ForgotPasswordViewModel::class.java)

        viewModel?.response?.observe(this, Observer {
            if (it?.has("message")!!) {
                Utils.showSnackBar(root, it.get("message").asString)
                startActivity(
                    (Intent(
                        this,
                        LoginActivity::class.java
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

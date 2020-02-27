package com.appleto.loppydriver.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.appleto.loppydriver.R
import com.appleto.loppydriver.helper.Const
import com.appleto.loppydriver.helper.Utils
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(p0: View?) {
        when (p0) {
            btnSubmit -> {
                if (!Utils.isEmptyEditText(edtPhone, "Please enter phone number", cardPhone)
                    && !Utils.isValidatePhone(
                        edtPhone,
                        "Please enter valid phone number",
                        cardPhone
                    )
                ) {
                    startActivity(
                        (Intent(this, ResetPasswordActivity::class.java)).putExtra(
                            Const.FROM,
                            Const.FORGOT_PASSWORD
                        ).putExtra(Const.MOBILE, edtPhone.text.toString().trim())
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)

        btnSubmit.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}

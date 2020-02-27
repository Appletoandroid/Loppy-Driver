package com.appleto.loppydriver.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.appleto.loppydriver.R
import com.appleto.loppydriver.helper.Const
import com.appleto.loppydriver.viewModel.LoginViewModel
import com.appleto.loppydriver.helper.PrefUtils
import com.appleto.loppydriver.helper.Utils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(p0: View?) {
        when (p0) {
            btnNewAccount -> {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
            btnSignIn -> {
                if (!Utils.isEmptyEditText(edtPhone, "Please enter phone number", cardPhone)
                    && !Utils.isValidatePhone(edtPhone, "Please enter valid phone", cardPhone)
                    && !Utils.isEmptyEditText(edtPassword, "Please enter password", cardPassword)
                ) {
                    FirebaseInstanceId.getInstance().instanceId
                        .addOnCompleteListener(OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Log.w(
                                    LoginActivity::class.java.canonicalName,
                                    "getInstanceId failed",
                                    task.exception
                                )
                                return@OnCompleteListener
                            }

                            // Get new Instance ID token
                            val token = task.result?.token.toString()
                            viewModel?.login(this, edtPhone.text.toString().trim(), token)
                            Log.d("Token", token)
                        })
                }
            }
            btnForgotPassword -> {
                startActivity(Intent(this, ForgotPasswordActivity::class.java))
            }
        }
    }

    private var viewModel: LoginViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setSupportActionBar(toolbar)

        viewModel = ViewModelProvider(
            this
        ).get(LoginViewModel::class.java)

        viewModel?.response?.observe(this, Observer {
            if (it?.has("user_id")!!) {
                PrefUtils.storeStringValue(this, Const.USER_ID, it.get("user_id").asString)
                startActivity(
                    Intent(this, VerifyOTPActivity::class.java).putExtra(
                        Const.FROM,
                        Const.LOGIN
                    )
                )
            }
        })

        btnNewAccount.setOnClickListener(this)
        btnSignIn.setOnClickListener(this)
        btnForgotPassword.setOnClickListener(this)
    }
}

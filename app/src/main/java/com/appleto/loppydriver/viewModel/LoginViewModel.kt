package com.appleto.loppydriver.viewModel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.appleto.loppydriver.helper.Const
import com.appleto.loppydriver.retrofit2.ApiService
import com.appleto.loppydriver.helper.PrefUtils
import com.appleto.loppydriver.helper.Utils
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService by lazy {
        PrefUtils.getStringValue(application, Const.TOKEN)?.let { ApiService.create(it) }
    }
    var disposable: Disposable? = null
    var response = MutableLiveData<JsonObject>()

    fun login(context: Context, phone: String, fcmToken: String) {
        Utils.showProgress(context)
        disposable =
            apiService
                ?.login(phone, fcmToken)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { result ->
                        Utils.hideProgress()
                        if (result.has("status") && result.get("status").asInt == 1) {
                            response.value = result
                        } else {
                            Toast.makeText(
                                context,
                                result.get("message").asString,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    { error ->
                        Utils.hideProgress()
                        Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                )
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}
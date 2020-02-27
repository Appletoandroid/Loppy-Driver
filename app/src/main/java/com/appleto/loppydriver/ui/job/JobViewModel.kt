package com.appleto.loppydriver.ui.job

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appleto.loppydriver.apiModels.PendingRideModel
import com.appleto.loppydriver.apiModels.RideRequestModel
import com.appleto.loppydriver.helper.Const
import com.appleto.loppydriver.helper.PrefUtils
import com.appleto.loppydriver.helper.Utils
import com.appleto.loppydriver.retrofit2.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class JobViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService by lazy {
        PrefUtils.getStringValue(application, Const.TOKEN)?.let { ApiService.create(it) }
    }
    var disposable: Disposable? = null
    var response = MutableLiveData<PendingRideModel>()

    fun getPendingRides(context: Context, driverId: String) {
        Utils.showProgress(context)
        disposable =
            apiService
                ?.getPendingRides(driverId)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { result ->
                        Utils.hideProgress()
                        if (result.status == 1) {
                            response.value = result
                        } else {
                            response.value = result
                            Toast.makeText(
                                context,
                                result.message,
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

    fun getCompleteRides(context: Context, driverId: String) {
        Utils.showProgress(context)
        disposable =
            apiService
                ?.getCompleteRides(driverId)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { result ->
                        Utils.hideProgress()
                        if (result.status == 1) {
                            response.value = result
                        } else {
                            response.value = result
                            Toast.makeText(
                                context,
                                result.message,
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
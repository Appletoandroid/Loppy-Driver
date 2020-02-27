package com.appleto.loppydriver.ui.dashboard

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.appleto.loppydriver.R
import com.appleto.loppydriver.apiModels.RideRequestData
import com.appleto.loppydriver.helper.Const
import com.appleto.loppydriver.helper.PrefUtils
import com.appleto.loppydriver.helper.Utils
import com.appleto.loppydriver.viewModel.RideRequestViewModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v) {
            btnAccept -> {
                data?.rideRequestId?.let {
                    activity?.let { it1 ->
                        PrefUtils.getStringValue(activity!!, Const.USER_ID)?.let { it2 ->
                            viewModel?.acceptRideRequest(
                                it1,
                                it2,
                                it
                            )
                        }
                    }
                }
            }
            btnReject -> {
                data?.rideRequestId?.let {
                    activity?.let { it1 ->
                        PrefUtils.getStringValue(activity!!, Const.USER_ID)?.let { it2 ->
                            viewModel?.rejectRideRequest(
                                it1,
                                it2,
                                it
                            )
                        }
                    }
                }
            }
        }
    }

    private var viewModel: RideRequestViewModel? = null
    private var data: RideRequestData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(RideRequestViewModel::class.java)

        Handler().postDelayed({
            activity?.let {
                PrefUtils.getStringValue(activity!!, Const.USER_ID)?.let { it1 ->
                    viewModel?.getRideRequest(
                        it,
                        it1
                    )
                }
            }
        }, 100)

        viewModel?.response?.observe(this, Observer {
            if (it?.data != null) {
                data = it.data
                tvNoRecord.visibility = View.GONE
                cardView.visibility = View.VISIBLE
                btnAccept.visibility = View.VISIBLE
                btnReject.visibility = View.VISIBLE

                tvDateTime.text = it.data?.dropDateTime?.let { it1 ->
                    Utils.formatDateTime(
                        "yyyy-MM-dd HH:mm:ss", "MMMM dd, yyyy hh:mm a",
                        it1
                    )
                }
                tvPickUpLocation.text = it.data?.sourceAddress
                tvDestinationLocation.text = it.data?.destinationAddress
                activity?.let { it1 ->
                    Glide.with(it1)
                        .load(it.data?.image)
                        .placeholder(R.drawable.truck)
                        .error(R.drawable.truck)
                        .into(ivTruckImage)
                }
                tvTruckName.text = "Pick Up Truck - ${it.data?.truckName}"

            } else {
                data = null
                tvNoRecord.visibility = View.VISIBLE
                cardView.visibility = View.GONE
                btnAccept.visibility = View.GONE
                btnReject.visibility = View.GONE
            }
        })

        viewModel?.responseAcceptRide?.observe(this, Observer {
            Handler().postDelayed({
                activity?.let {
                    PrefUtils.getStringValue(activity!!, Const.USER_ID)?.let { it1 ->
                        viewModel?.getRideRequest(
                            it,
                            it1
                        )
                    }
                }
            }, 500)
        })

        viewModel?.responseRejectRide?.observe(this, Observer {
            Handler().postDelayed({
                activity?.let {
                    PrefUtils.getStringValue(activity!!, Const.USER_ID)?.let { it1 ->
                        viewModel?.getRideRequest(
                            it,
                            it1
                        )
                    }
                }
            }, 500)
        })

        btnAccept.setOnClickListener(this)
        btnReject.setOnClickListener(this)
    }
}
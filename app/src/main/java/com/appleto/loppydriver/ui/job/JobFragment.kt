package com.appleto.loppydriver.ui.job

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.appleto.loppydriver.R
import com.appleto.loppydriver.adapter.JobsListAdapter
import com.appleto.loppydriver.apiModels.PendingRideDatum
import com.appleto.loppydriver.helper.Const
import com.appleto.loppydriver.helper.PrefUtils
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_job.*

class JobFragment : Fragment() {

    private lateinit var viewModel: JobViewModel

    private lateinit var adapter: JobsListAdapter

    private var jobList = ArrayList<PendingRideDatum>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_job, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(JobViewModel::class.java)

        adapter = activity?.let { JobsListAdapter(it, jobList) }!!
        recyclerViewJobs.adapter = adapter
        recyclerViewJobs.layoutManager = LinearLayoutManager(activity)

        Handler().postDelayed({
            PrefUtils.getStringValue(activity!!, Const.USER_ID)?.let {
                viewModel.getPendingRides(
                    activity!!,
                    it
                )
            }
        }, 100)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                if (p0?.position == 0) {
                    PrefUtils.getStringValue(activity!!, Const.USER_ID)?.let {
                        viewModel.getPendingRides(
                            activity!!,
                            it
                        )
                    }
                } else {
                    PrefUtils.getStringValue(activity!!, Const.USER_ID)?.let {
                        viewModel.getCompleteRides(
                            activity!!,
                            it
                        )
                    }
                }
            }
        })


        viewModel.response.observe(this, Observer {
            if (it?.data != null) {
                jobList.clear()
                tvNoRecord.visibility = View.GONE
                recyclerViewJobs.visibility = View.VISIBLE
                jobList.addAll(it.data!!)
                adapter.notifyDataSetChanged()
            } else {
                tvNoRecord.visibility = View.VISIBLE
                recyclerViewJobs.visibility = View.GONE
            }
        })
    }
}
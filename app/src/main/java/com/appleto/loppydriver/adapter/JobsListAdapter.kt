package com.appleto.loppydriver.adapter

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import com.appleto.loppydriver.activity.MapsActivity
import com.appleto.loppydriver.R
import com.appleto.loppydriver.apiModels.PendingRideDatum
import com.appleto.loppydriver.helper.Const
import com.appleto.loppydriver.helper.Utils
import com.bumptech.glide.Glide
import kotlin.collections.ArrayList


class JobsListAdapter(private val context: Context, private var data: ArrayList<PendingRideDatum>) :
    RecyclerView.Adapter<JobsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.row_jobs_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        holder.tvDateTime.text = data[position].dropDateTime?.let {
            Utils.formatDateTime(
                "yyyy-MM-dd HH:mm:ss", "MMMM dd, yyyy hh:mm a",
                it
            )
        }
        holder.tvPickUpLocation.text = data[position].sourceAddress
        holder.tvDestinationLocation.text = data[position].destinationAddress
        Glide.with(context)
            .load(data[position].image)
            .placeholder(R.drawable.truck)
            .error(R.drawable.truck)
            .into(holder.ivTruckImage)
        holder.tvTruckName.text = "Pick Up Truck - ${data[position].truckName}"

        holder.itemView.setOnClickListener {
//            if (data[position].status == "accept") {
                context.startActivity(
                    Intent(context, MapsActivity::class.java).putExtra(
                        Const.JOB_DATA,
                        data[position]
                    )
                )
//            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)
        var tvPickUpLocation: TextView = itemView.findViewById(R.id.tvPickUpLocation)
        var tvDestinationLocation: TextView = itemView.findViewById(R.id.tvDestinationLocation)
        var tvTruckName: TextView = itemView.findViewById(R.id.tvTruckName)
        var ivTruckImage: ImageView = itemView.findViewById(R.id.ivTruckImage)
    }
}
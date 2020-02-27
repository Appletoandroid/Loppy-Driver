package com.appleto.loppydriver.apiModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class PendingRideModel : Serializable {

    @SerializedName("status")
    @Expose
    var status: Int? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("data")
    @Expose
    var data: List<PendingRideDatum>? = null

}

class PendingRideDatum : Serializable {

    @SerializedName("ride_request_id")
    @Expose
    var rideRequestId: String? = null
    @SerializedName("user_id")
    @Expose
    var userId: String? = null
    @SerializedName("source_address")
    @Expose
    var sourceAddress: String? = null
    @SerializedName("destination_address")
    @Expose
    var destinationAddress: String? = null
    @SerializedName("source_lat")
    @Expose
    var sourceLat: String? = null
    @SerializedName("source_long")
    @Expose
    var sourceLong: String? = null
    @SerializedName("destination_lat")
    @Expose
    var destinationLat: String? = null
    @SerializedName("destination_long")
    @Expose
    var destinationLong: String? = null
    @SerializedName("accepted_driver_id")
    @Expose
    var acceptedDriverId: String? = null
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null
    @SerializedName("drop_date_time")
    @Expose
    var dropDateTime: String? = null
    @SerializedName("truck_id")
    @Expose
    var truckId: String? = null
    @SerializedName("paid_by")
    @Expose
    var paidBy: String? = null
    @SerializedName("receiver_name")
    @Expose
    var receiverName: String? = null
    @SerializedName("receiver_mobile_no")
    @Expose
    var receiverMobileNo: String? = null
    @SerializedName("amount")
    @Expose
    var amount: String? = null
    @SerializedName("status")
    @Expose
    var status: String? = null
    @SerializedName("truck_name")
    @Expose
    var truckName: String? = null
    @SerializedName("image")
    @Expose
    var image: String? = null

}
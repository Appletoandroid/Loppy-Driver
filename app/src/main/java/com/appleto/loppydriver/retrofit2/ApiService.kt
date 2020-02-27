package com.appleto.loppydriver.retrofit2

import com.appleto.loppydriver.apiModels.PendingRideModel
import com.appleto.loppydriver.apiModels.RideRequestModel
import com.appleto.loppydriver.helper.Const
import com.google.gson.JsonObject
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.http.*
import java.util.concurrent.TimeUnit


interface ApiService {

    companion object {
        fun create(token: String): ApiService {

            val httpClient = OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            httpClient.addInterceptor(interceptor)

            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")

                // Adding Authorization token (API Key)
                // Requests will be denied without API key
                /*if (!TextUtils.isEmpty(token)) {
                    requestBuilder.addHeader("token", token)
                }*/

                val request = requestBuilder.build()
                chain.proceed(request)
            }

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create()
                )
                .client(httpClient.build())
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .baseUrl(Const.BASE_URL)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }

    @FormUrlEncoded
    @POST("login")
    fun login(@Field("phone") phone: String, @Field("fcm_token") fcmToken: String):
            Observable<JsonObject>

    @Multipart
    @POST("register")
    fun register(@PartMap map: HashMap<String, RequestBody>, @Part licenceImage: MultipartBody.Part): Observable<JsonObject>

    @FormUrlEncoded
    @POST("otp_verification")
    fun verifyOTP(@Field("user_id") userId: String, @Field("otp") otp: String):
            Observable<JsonObject>

    @FormUrlEncoded
    @POST("forgot_password")
    fun forgotPassword(
        @Field("phone") phone: String, @Field("new_password") newPassword: String, @Field(
            "conf_password"
        ) confPassword: String
    ): Observable<JsonObject>

    @FormUrlEncoded
    @POST("get_ride_request")
    fun getRideRequest(@Field("driver_id") driver_id: String): Observable<RideRequestModel>

    @FormUrlEncoded
    @POST("pending_rides")
    fun getPendingRides(@Field("driver_id") driver_id: String): Observable<PendingRideModel>

    @FormUrlEncoded
    @POST("complete_rides")
    fun getCompleteRides(@Field("driver_id") driver_id: String): Observable<PendingRideModel>

    @FormUrlEncoded
    @POST("accept_ride")
    fun acceptRideRequest(@Field("driver_id") driver_id: String, @Field("ride_request_id") rideRequestId: String): Observable<JsonObject>

    @FormUrlEncoded
    @POST("reject_ride")
    fun rejectRideRequest(@Field("driver_id") driver_id: String, @Field("ride_request_id") rideRequestId: String): Observable<JsonObject>

    @FormUrlEncoded
    @POST("update_driver_location")
    fun updateDriverLocation(
        @Field("driver_id") driverId: String, @Field("latitude") latitude: Double, @Field(
            "longitude"
        ) longitude: Double
    ): Observable<JsonObject>
}
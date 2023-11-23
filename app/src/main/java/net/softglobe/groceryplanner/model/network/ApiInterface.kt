package net.softglobe.groceryplanner.model.network

import net.softglobe.groceryplanner.model.network.request.BackUpRequest
import net.softglobe.groceryplanner.model.network.response.MetaDataResponse
import net.softglobe.groceryplanner.model.URLs
import net.softglobe.groceryplanner.model.network.response.LoginResponse
import net.softglobe.groceryplanner.model.network.response.PaymentFetchResponse
import net.softglobe.groceryplanner.model.network.response.UserMetaDataResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {

    @FormUrlEncoded
    @POST(URLs.LOGIN)
    suspend fun loginUser(@Field("email") email : String, @Field("password") password : String) : Response<LoginResponse>

    @POST(URLs.REGISTER)
    suspend fun registerUser(@Body user: User) : Response<Result>

    @FormUrlEncoded
    @POST(URLs.FORGOT_PASS)
    suspend fun forgotPassword(@Field("email") email : String, @Field("code") code : String) : Response<Result>

    @FormUrlEncoded
    @POST(URLs.RESET_PASS)
    suspend fun resetPassword(@Field("email") email : String, @Field("newpass") newPassword : String) : Response<Result>

    @FormUrlEncoded
    @POST(URLs.CHANGE_PASS)
    suspend fun changePassword(@Field("email") email : String, @Field("oldpass") oldPassword : String, @Field("newpass") newPassword : String,
                               @Field("authToken") authToken: String) : Response<Result>

    @POST(URLs.BACKUP_TO_SERVER)
    suspend fun backupToServer(@Body backUpRequest: BackUpRequest) : Response<Result>

    @FormUrlEncoded
    @POST(URLs.BACKUP_FROM_SERVER)
    suspend fun importBackupFromServer(@Field("email") email : String, @Field("authToken") authToken: String) : Response<BackUpRequest>

    @GET(URLs.METADATA)
    suspend fun getMetadata() : Response<MetaDataResponse>

    @FormUrlEncoded
    @POST(URLs.USER_METADATA)
    suspend fun getUserMetadata(@Field("email") email: String, @Field("authToken") authToken: String) : Response<UserMetaDataResponse>

    @FormUrlEncoded
    @POST(URLs.USER_DETAILS)
    suspend fun getUserDetails(@Field("email") email: String, @Field("authToken") authToken: String) : Response<LoginResponse>

    @FormUrlEncoded
    @POST(URLs.USER_OPERATIONS)
    suspend fun changeUserName(@Field("email") email: String, @Field("authToken") authToken: String, @Field("operation") operation: String, @Field("name") name: String) : Response<Result>

    @POST(URLs.PAYMENT_FETCH)
    suspend fun callPaymentFetchApi() : Response<PaymentFetchResponse>
}
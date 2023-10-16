package net.softglobe.groceryplanner.model.network

import net.softglobe.groceryplanner.model.URLs
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
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
}
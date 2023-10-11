package net.softglobe.groceryplanner.model.network

import net.softglobe.groceryplanner.LoginResponse
import net.softglobe.groceryplanner.model.URLs
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInterface {

    @FormUrlEncoded
    @POST(URLs.LOGIN)
    suspend fun loginUser(@Field("email") email : String, @Field("password") password : String) : Response<LoginResponse>
}
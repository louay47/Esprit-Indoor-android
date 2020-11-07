package com.example.espritindoor.technique;


import com.example.espritindoor.Model.Comment;
import com.example.espritindoor.Model.Feed;
import com.example.espritindoor.Model.Salle;
import com.example.espritindoor.Model.SdkSetupId;
import com.example.espritindoor.Model.user;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {



    @GET("login/{email}/{password}")
    public Call<user> GetUser(@Path("email") String userEmail, @Path("password") String userPassword);

    @GET("2020/5/18?key=e24658c6-953a-4cea-9630-3174f840b6d2")
    public Call<List<SdkSetupId>> getSdkSetupId();

    /*@GET("{SDK_SETUP_ID}/events?key=e24658c6-953a-4cea-9630-3174f840b6d2")
    public Call<List<CoordinateInfos>> getCoordinate(@Path("SDK_SETUP_ID")String id);*/

    @GET("{SDK_SETUP_ID}/events?key=e24658c6-953a-4cea-9630-3174f840b6d2")
    public Call<List<Feed>> getCoordinate(@Path("SDK_SETUP_ID")String id);

    @POST("contacts")
    public  Call<user> SetUser(@Body HashMap<Object, Object> map);


    @POST("userId/{sender}/{contenu}")
    public  Call<Comment> addComment(@Path("sender")String id , @Path("contenu") String contenu);

    @POST("addComment/{salleName}/commentId/{cid}")
    public  Call<Comment> addCommentToSalle(@Path("salleName")String id , @Path("cid") String contenu);

    @GET("{salleName}")
    public  Call<Salle> getComments(@Path("salleName")String id);



}

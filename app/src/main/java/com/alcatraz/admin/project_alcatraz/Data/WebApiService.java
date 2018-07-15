package com.alcatraz.admin.project_alcatraz.Data;

import com.alcatraz.admin.project_alcatraz.Social.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WebApiService {
    @GET("messages/{user_id}")
    Call<List<Message>> getMessages(@Path("user_id") int userId);
}
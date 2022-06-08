package ar.com.andessalud.andes.network;


import java.util.ArrayList;
import java.util.List;

import ar.com.andessalud.andes.models.PharmacyModelResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ServiceInterface {

    @Multipart
    @POST("create-order")
//    Call<PharmacyModelResponse> uploadNewsFeedImages(@Part List<MultipartBody.Part> files);
    Call<PharmacyModelResponse> updateProfile(
            @Part("date") RequestBody date,
            @Part("exact_location") RequestBody exactlocation,
            @Part("longitude") RequestBody longitude,
            @Part("latitude") RequestBody latitude,
            @Part List<MultipartBody.Part> files,
            @Part("idAfiliado") RequestBody session_id
            );
}

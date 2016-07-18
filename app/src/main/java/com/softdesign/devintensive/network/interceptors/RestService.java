package com.softdesign.devintensive.data.network.interceptors;

        import com.softdesign.devintensive.network.interceptors.req.UserLoginReq;
        import com.softdesign.devintensive.network.interceptors.res.UserModelRes;
        import com.softdesign.devintensive.network.interceptors.res.UploadphotoRes;
        import com.softdesign.devintensive.network.interceptors.res.UserListRes;

        import okhttp3.MultipartBody;
        import okhttp3.RequestBody;
        import okhttp3.ResponseBody;
        import retrofit2.Call;
        import retrofit2.http.Body;
        import retrofit2.http.Multipart;
        import retrofit2.http.POST;
        import retrofit2.http.Part;
        import retrofit2.http.GET;
        import retrofit2.http.Path;

public interface RestService {

    @POST("login")
    Call<UserModelRes> loginUser (@Body UserLoginReq req);

    @Multipart
    @POST("user/{userId}/publicValues/profilePhoto")
    Call<UploadphotoRes> uploadPhoto (@Path("userId") String userId,
                                      @Part MultipartBody.Part file);

    @GET("user/list?orderBy=rating")
    Call<UserListRes> getUserList();
}

package com.softdesign.devintensive.data.messages;

import com.softdesign.devintensive.data.messages.network.RestService;
import com.softdesign.devintensive.data.messages.network.ServiceGenerator;
import com.softdesign.devintensive.data.messages.network.req.UserLoginReq;
import com.softdesign.devintensive.data.messages.network.res.UserModelRes;

import retrofit2.Call;
import okhttp3.RequestBody;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;

public class DataManager {
    private static DataManager INSTANCE = null;
    private PreferencesManager mPreferencesManager;
    private RestService mRestService;

    private DataManager() {

        this.mPreferencesManager = new PreferencesManager();
        this.mRestService = ServiceGenerator.createService(RestService.class);
    }

    public static DataManager getInstanse() {
        if (INSTANCE == null) INSTANCE = new DataManager();
        return INSTANCE;
    }

    public PreferencesManager getPreferencesManager() {

        return mPreferencesManager;
    }
//region ================ Network ===============

    public Call<UserModelRes> loginUser(UserLoginReq userLoginReq){
        return mRestService.loginUser(userLoginReq);
    }
    public Call<ResponseBody> savePhoto(RequestBody body, MultipartBody.Part file){
        return mRestService.savePhoto(body, file);
    }


//endregion

}
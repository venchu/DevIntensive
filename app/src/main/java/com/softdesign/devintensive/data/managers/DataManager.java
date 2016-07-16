package com.softdesign.devintensive.data.managers;

import com.softdesign.devintensive.network.interceptors.RestService;
import com.softdesign.devintensive.network.interceptors.ServiceGenerator;
import com.softdesign.devintensive.network.interceptors.req.UserLoginReq;
import com.softdesign.devintensive.network.interceptors.res.UserModelRes;
import com.softdesign.devintensive.network.interceptors.res.UserListRes;

import retrofit2.Call;
import okhttp3.RequestBody;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;

public class DataManager {
    private static DataManager INSTANCE = null;
    private com.softdesign.devintensive.data.messages.PreferencesManager mPreferencesManager;
    private RestService mRestService;

    private DataManager() {

        this.mPreferencesManager = new com.softdesign.devintensive.data.messages.PreferencesManager();
        this.mRestService = ServiceGenerator.createService(RestService.class);
    }

    public static DataManager getInstanse() {
        if (INSTANCE == null) INSTANCE = new DataManager();
        return INSTANCE;
    }

    public com.softdesign.devintensive.data.messages.PreferencesManager getPreferencesManager() {

        return mPreferencesManager;
    }
//region ================ Network ===============

    public Call<UserModelRes> loginUser(UserLoginReq userLoginReq) {
        return mRestService.loginUser(userLoginReq);
    }

    public Call<ResponseBody> uploadUserPhoto(RequestBody body, MultipartBody.Part file) {
        return null;
    }

    public Call<UserListRes> getUserList() {
        return mRestService.getUserList();
    }

}
//endregion
import android.content.Context;

import com.softdesign.devintensive.data.managers.PreferencesManager;
import com.softdesign.devintensive.network.interceptors.PicassoCache;
import com.softdesign.devintensive.network.interceptors.RestService;
import com.softdesign.devintensive.network.interceptors.ServiceGenerator;
import com.softdesign.devintensive.network.interceptors.req.UserLoginReq;
import com.softdesign.devintensive.network.interceptors.res.UserModelRes;
import com.softdesign.devintensive.network.interceptors.res.UserListRes;
import com.softdesign.devintensive.utils.DevintensiveApplication;
import com.softdesign.devintensive.data.storage.models.DaoSession;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import okhttp3.RequestBody;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;

public class DataManager {
    private static DataManager INSTANCE = null;
    private Picasso mPicasso;
    private Context mContext;
    private PreferencesManager mPreferencesManager;
    private RestService mRestService;

    private DaoSession mDaoSession;

    private DataManager() {

        this.mPreferencesManager = new PreferencesManager();
        this.mRestService = ServiceGenerator.createService(RestService.class);
        this.mContext = DevintensiveApplication.getmContext();
        this.mPicasso = new PicassoCache(mContext).getmPicassoInstance();
        this.mDaoSession = new DevintensiveApplication.getsDaoSession();
    }

    public static DataManager getInstanse() {
        if (INSTANCE == null) INSTANCE = new DataManager();
        return INSTANCE;
    }

    public PreferencesManager getPreferencesManager() {

        return mPreferencesManager;

    }
    public Context getContext() {return mContext; }

    public Picasso getmPicasso() {
        return mPicasso;
    }

//region ================ Network =============== 

    public Call<UserModelRes> loginUser(UserLoginReq userLoginReq) {
        return mRestService.loginUser(userLoginReq);
    }

    public Call<ResponseBody> uploadUserPhoto(RequestBody body, MultipartBody.Part file) {
        return null;
    }

    public Call<UserListRes> getUserListFromNetwork() {
        return mRestService.getUserList();
    }


    public List<User> getUserListFromDb(){
        List<User> userList = new ArrayList<>();

        try{
            userList = mDaoSession.queryBuilder(User.class)
                    .where(UserDao.Properties.CodeLines.gt(0))
                    .orderDesc(UserDao.Properties.CodeLines)
                    .build()
                    .list();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return userList;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public List<User> getUserListByName(String query){

        List<User> userList = new ArrayList<>();
        try{
            userList = mDaoSession.queryBuilder(User.class)
                    .where(UserDao.Properties.Rating.gt(0),UserDao.Properties.SearchName.like("%" + query.toUpperCase() + "%"))
                    .orderDesc(UserDao.Properties.CodeLines)
                    .build()
                    .list();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return userList;
    }
}
//endregion

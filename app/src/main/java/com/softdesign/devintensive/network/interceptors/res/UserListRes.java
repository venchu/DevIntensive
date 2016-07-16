package com.softdesign.devintensive.network.interceptors.res;

        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

        import java.util.ArrayList;
        import java.util.List;

public class UserListRes {
    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private List<UserData> data = new ArrayList<>();

    public List<UserData> getData() {
        return data;
    }

    public class UserData {

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("first_name")
        @Expose
        private String firstName;
        @SerializedName("second_name")
        @Expose
        private String secondName;
        @SerializedName("__v")
        @Expose
        private int v;
        @SerializedName("repositories")
        @Expose
        private UserModelRes.Repositories repositories;
        @SerializedName("profileValues")
        @Expose
        private UserModelRes.ProfileValues profileValues;
        @SerializedName("publicInfo")
        @Expose
        private UserModelRes.PublicInfo publicInfo;
        @SerializedName("updated")
        @Expose
        private String updated;

        public UserModelRes.Repositories getRepositories() {
            return repositories;
        }public String getFullName() {
            return firstName + " " + secondName;
        }

        public UserModelRes.PublicInfo getPublicInfo() {
            return publicInfo;
        }
        public UserModelRes.ProfileValues getProfileValues() {
            return profileValues;
        }


    }
}
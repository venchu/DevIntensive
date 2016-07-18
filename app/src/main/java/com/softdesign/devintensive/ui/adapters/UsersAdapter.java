package com.softdesign.devintensive.ui.adapters;

import com.softdesign.devintensive.network.interceptors.res.UserListRes;

        import android.content.Context;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.TextView;

        import com.softdesign.devintensive.R;
        import com.softdesign.devintensive.data.messages.network.res.UserListRes;
        import com.softdesign.devintensive.ui.activities.views.AspectRatioImageView;
        import com.squareup.picasso.Picasso;

        import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    List<UserListRes.UserData> mUsers;
    Context mContext;
    UserViewHolder.CustomClickListener mCustomClickListener;

    public UsersAdapter(List<UserListRes.UserData> users, UserViewHolder.CustomClickListener customClickListener){
        this.mUsers = users;
        this.mCustomClickListener = customClickListener;
    }
    @Override
    public UsersAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);
        return new UserViewHolder(convertView, mCustomClickListener);
    }

    @Override
    public void onBindViewHolder(UsersAdapter.UserViewHolder holder, int position) {
        UserListRes.UserData user = mUsers.get(position);
        Picasso.with(mContext)
                .load(user.getPublicInfo().getPhoto())
                .placeholder(mContext.getResources().getDrawable(R.drawable.user_bg))
                .error(mContext.getResources().getDrawable(R.drawable.user_bg))
                .into(holder.userPhoto);
        holder.mFullName.setText(String.valueOf(user.getFullName()));
        holder.mRating.setText(String.valueOf(user.getProfileValues().getRating()));
        holder.mCodeLines.setText(String.valueOf(user.getProfileValues().getLinesCode()));
        holder.mProjects.setText(String.valueOf(user.getProfileValues().getProjects()));
        if (user.getPublicInfo().getBio() == null || user.getPublicInfo().getBio().isEmpty()){
            holder.mBio.setVisibility(View.GONE);
        }else {
            holder.mBio.setVisibility(View.VISIBLE);
            holder.mBio.setText(user.getPublicInfo().getBio());
        }

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }




    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView mFullName, mRating, mCodeLines, mProjects, mBio;
        protected AspectRatioImageView userPhoto;
        protected Button mShowMore;
        protected CustomClickListener mListener;

        public UserViewHolder(View itemView, CustomClickListener customClickListener) {
            super(itemView);
            userPhoto = (AspectRatioImageView) itemView.findViewById(R.id.user_photo);
            mFullName = (TextView) itemView.findViewById(R.id.user_fullname);
            mRating = (TextView) itemView.findViewById(R.id.user_rating);
            mProjects = (TextView) itemView.findViewById(R.id.projects);
            mCodeLines = (TextView) itemView.findViewById(R.id.codelines);
            mBio = (TextView) itemView.findViewById(R.id.edit_bio);
            mShowMore = (Button) itemView.findViewById(R.id.more_info_btn);
            mListener = customClickListener;

            mShowMore.setOnClickListener(this);
        }

        public interface CustomClickListener{
            void onUserItemClickListener(int position);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.more_info_btn){
                mListener.onUserItemClickListener(getAdapterPosition());
            }
        }
    }
}
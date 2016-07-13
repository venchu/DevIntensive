package com.softdesign.devintensive.ui.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.messages.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.NetworkStatusChecker;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int mCurrentEditMode = 0;
    private static final boolean[] STATUS = {false, true};
    private DataManager mDataManager;
    private AppBarLayout.LayoutParams mAppBarParams = null;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;

    private static final String TAG = ConstantManager.TAG_PREFIX + "MainActivity";
    private ImageView avatarView;

    @BindView(R.id.main_coordinator_container)
    CoordinatorLayout mMainCoordinatorContainer;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.navigation_drawer)
    DrawerLayout mNavigationDrawer;
    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.profile_placeholder)
    RelativeLayout mProfilePlaceholder;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.user_photo_img)
    ImageView mProfileImage;
    TextView mUserProfileText;
    TextView mUserEmailText;

    @BindView(R.id.call_phone)
    ImageView mCallPhone;
    @BindView(R.id.send_mail)
    ImageView mSendMail;
    @BindView(R.id.view_vk)
    ImageView mViewVk;
    @BindView(R.id.view_github)
    ImageView mViewGithub;

    @BindViews({R.id.input_layout_phone, R.id.input_layout_mail, R.id.input_layout_vk, R.id.input_layout_github})
    List<TextInputLayout> userInputFieldViews;
    @BindViews({R.id.edit_phone, R.id.edit_mail, R.id.edit_vk, R.id.edit_github,
            07:07:58
            R.id.edit_bio})
    List<EditText> userFieldViews;
    @BindViews({R.id.user_rating, R.id.codelines, R.id.projects})
    List<TextView> mUserValueViews;
    private Integer[] mArrayErrors = {R.string.error_format_phone, R.string.error_format_email, R.string.error_format_path_vk, R.string.error_format_path_github};

    private TextView mUserValueRating, mUserValueCodeLines, mUserValueProjects;
    private List<TextView> mUserValuesViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ButterKnife.apply(userFieldViews, ADD_CHANGED_LISTENER);

        mDataManager = DataManager.getInstanse();
        View v = mNavigationView.getHeaderView(0);
        mUserProfileText = (TextView) v.findViewById(R.id.edit_bio);
        mUserEmailText = (TextView) v.findViewById(R.id.edit_mail);

        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(MainActivity.this);
        mProfilePlaceholder = (RelativeLayout) findViewById(R.id.profile_placeholder);
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        mProfileImage = (ImageView) findViewById(R.id.user_photo_img);
        mCallPhone.setOnClickListener(this);
        mSendMail.setOnClickListener(this);
        mViewVk.setOnClickListener(this);
        mViewGithub.setOnClickListener(this);

        mUserValueRating = (TextView) findViewById(R.id.user_rating);
        mUserValueCodeLines = (TextView) findViewById(R.id.codelines);
        mUserValueProjects = (TextView) findViewById(R.id.projects);

        mUserValuesViews = new ArrayList<>();
        mUserValuesViews.add(mUserValueRating);
        mUserValuesViews.add(mUserValueCodeLines);
        mUserValuesViews.add(mUserValueProjects);



        setupToolbar();
        setupDrawer();
        loadUserInfoValue();
        loadUserValue();

        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.nav_head_back)
                .into(mProfileImage);

        if (savedInstanceState != null) {
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
            changeEditMode(mCurrentEditMode);

//активити запускается впервые
        }
//активити уже создавалось
    }

    private void uploadPhoto(Uri uri) {
        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            File file = new File(uri.getPath());

            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

            MultipartBody.Part body = MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

            String descriptionString = "load photo";
            RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);

            Call<ResponseBody> call = mDataManager.savePhoto(description, body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call,
                                       Response<ResponseBody> response) {
                    Log.v("Upload", "success");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("Upload error:", t.getMessage());
                }
            });
        } else {
            showSnackbar("Сеть на данный момент не доступна");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveUserInfoValue();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,
                "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    /**
     * Показываем snackbar
     */
    private void showSnackbar(String message) {
        Snackbar.make(mMainCoordinatorContainer, message, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Установка Toolbar
     */
    private void setupToolbar() {
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Чупиков В.С.");
        }
    }

    /**
     * Работа с navigation view
     */
    private void setupDrawer() {
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        assert mNavigationView != null;
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                showSnackbar(item.getTitle().toString());
                item.setChecked(true);
                if (item.getItemId() == R.id.exit_menu)
                    startActivity(new Intent(MainActivity.this, AuthActivity.class));
                mNavigationDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    /**
     * Получение результата из другой Activity (фото из камеры или галереи)
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedImage = data.getData();
                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);
                    insertProfileImage(mSelectedImage);
                }
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (mNavigationDrawer.isDrawerOpen(GravityCompat.START))
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        else onBackPressed();
    }

    /**
     * Смена режима страницы мой профиль
     *
     * @param mode режим страницы
     */
    private void changeEditMode(int mode) {
        ButterKnife.apply(userFieldViews, CHANGE_EDIT_ALL);
        if (mode == 0) {
            mFab.setImageResource(R.drawable.ic_menu_black_24dp);
            boolean isSave = true;
            for (int i = 0; i < userInputFieldViews.size(); i++) {
                if (userInputFieldViews.get(i).isErrorEnabled()) {
                    isSave = false;
                    loadUserInfoValue();
                    showSnackbar("Ошибка сохранения данных!");
                    break;
                }
            }
            if (isSave) saveUserInfoValue();
            hideProfilePlaceholder();
            unlockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));
            ButterKnife.apply(userInputFieldViews, CHANGE_ERROR_ALL);
        } else {
            mFab.setImageResource(R.drawable.ic_done_black_24dp);
            showProfilePlaceholder();
            lockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
            if (userFieldViews.get(0).requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        }
    }

    /**
     * Загрузка пользовательских данных
     */
    private void loadUserInfoValue() {
        List<String> userFields = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userFields.size(); i++) {
            if (!userFields.get(i).equals("null")) userFieldViews.get(i).setText(userFields.get(i));
        }
    }

    /**
     * Сохранение пользовательских данных
     */
    private void saveUserInfoValue() {
        List<String> userFields = new ArrayList<>();
        for (EditText userEditText : userFieldViews) {
            userFields.add(userEditText.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userFields);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (mCurrentEditMode == 0) mCurrentEditMode = 1;
                else mCurrentEditMode = 0;
                changeEditMode(mCurrentEditMode);
                break;
            case R.id.profile_placeholder:
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;
            case R.id.call_phone:
                callPhone();
                break;
            case R.id.send_mail:
                sendMail();
                break;
            case R.id.view_vk:
                openVk();
                break;
            case R.id.view_github:
                openGithub();
                break;
        }
    }

    private void loadUserValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserValue();
        for (int i = 0; i < userData.size(); i++) {
            mUserValuesViews.get(i).setText(userData.get(i));

        }
    }

    private void initUserInfoValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileValue();
        for (int i = 0; i < userData.size(); i++) {
            mUserValueViews.get(i).setText(userData.get(i));
        }
    }

    private void initContentValue() {
        List<String> contentData = mDataManager.getPreferencesManager().loadContentValue();
        for (int i = 0; i < contentData.size(); i++) {
            userFieldViews.get(i).setText(contentData.get(i));
        }
    }

    private void initNavValue() {
        List<String> navData = mDataManager.getPreferencesManager().loadNavValue();
        try {
            mUserProfileText.setText(navData.get(0) +" " + navData.get(1));
        } catch (Exception e) {
            Log.d("M", e.getMessage());
        }
        mUserEmailText.setText(mDataManager.getPreferencesManager().getEmail());
        Uri uri = Uri.parse(navData.get(2));
        Picasso.with(this).load(uri).into(avatarView);
/*File file = new File(uri.getPath());
createRoundedAvatar(file);*/
    }

    private void initPhoto() {
        insertProfileImage(mDataManager.getPreferencesManager().loadUserPhoto());
    }

    /**
     * Загрузка фотографии из галереи
     */
    private void loadPhotoFromGallery() {
        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        takeGalleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGalleryIntent, getString(R.string.user_profile_choose_message)), ConstantManager.REQUEST_GALLERY_PICTURE);

    }

    /**
     * Загрузка фотографии из камеры
     */
    private void loadPhotoFromCamera() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (mPhotoFile != null) {
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSIONS_CODE);
            Snackbar.make(mCollapsingToolbar, "Для корректной работы необходимо дать требуемые разрешения", Snackbar.LENGTH_LONG)
                    .setAction("Разрешить", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConstantManager.CAMERA_REQUEST_PERMISSIONS_CODE && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
// TODO: 09.07.2016 тут обрабатываем разрешение (разрекшение получено)
// например вывести сообщение или обработать какой-то логикой если нужно
            }
        }
        if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
// TODO: 09.07.2016 тут обрабатываем разрешение (разрекшение получено)
// например вывести сообщение
            или обработать какой-то логикой если нужно
        }
    }

    /**
     * Скрываем плейсхолдер в редактировании
     */
    private void hideProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.GONE);
    }

    /**
     * Показываем плейсхолдер в редактировании
     */
    private void showProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    /**
     * Блокируем mCollapsingToolbar
     */
    private void lockToolbar() {
        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    /**
     * Разблокируем mCollapsingToolbar
     */
    private void unlockToolbar() {
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    /**
     * Открываем vk
     */
    private void openVk() {
        Uri addressVk = Uri.parse("https://" + mDataManager.getPreferencesManager().loadUserProfileData().get(2));
        Intent openVk = new Intent(Intent.ACTION_VIEW, addressVk);
        startActivity(openVk);
    }

    /**
     * Открываем git
     */
    private void openGithub() {
        Uri addressGithub = Uri.parse("https://" + mDataManager.getPreferencesManager().loadUserProfileData().get(3));
        Intent openGithub = new Intent(Intent.ACTION_VIEW, addressGithub);
        startActivity(openGithub);
    }

    /**
     * Отправляем email
     */
    private void sendMail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, mDataManager.getPreferencesManager().loadUserProfileData().get(1));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Content subject");
        intent.putExtra(Intent.EXTRA_TEXT, "Content text");
        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    /**
     * Вызов номера телефона
     */
    private void callPhone() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                    mDataManager.getPreferencesManager().loadUserProfileData().get(0)));
            startActivity(dialIntent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CALL_PHONE}, ConstantManager.CALL_PHONE_REQUEST_PERMISSIONS_CODE);
            Snackbar.make(mCollapsingToolbar, "Для корректной работы необходимо дать требуемые разрешения", Snackbar.LENGTH_LONG)
                    .setAction("Разрешить", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    /**
     * Проверяем телефон
     *
     * @param phone
     * @return
     */
    private boolean validatePhone(String phone) {
        String[] arrText = phone.split(" |-");
        if (arrText[1].length() == 3 && arrText[2].length() == 3 && arrText[3].length() == 2 && arrText[4].length() >= 2) {
            int countSymbol = 0;
            for (int i = 0; i < arrText.length; i++) {
                countSymbol = arrText[i].length();
                if (arrText[i].equals("7")) countSymbol--;
            }
            if (countSymbol >= 11 && countSymbol <= 20) return true;
            else return false;
        } else {
            return false;
        }
    }

    /**
     * Проверяем email
     *
     * @param email
     * @return
     */
    private boolean validateEmail(String email) {
        String[] arrText = email.split("@");
        String[] arrSecondPart = arrText[1].split("\\.");
        if (arrText[0].length() >= 3 && arrSecondPart[0].length() >= 2 && arrSecondPart[1].length() >= 2) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Проверяем путь
     *
     * @param path
     * @return
     */
    private boolean validatePath(String path) {
        String[] arrText = path.split("vk\\.com/|github\\.com/");
        if (arrText[0].equals("")) return true;
        else return false;
    }

    /**
     * Вывод ошибки
     *
     * @param result
     * @param index
     */
    private void showError(boolean result, int index) {
        if (!result) userInputFieldViews.get(index).setError(getString(mArrayErrors[index]));
        else
            userInputFieldViews.get(index).setErrorEnabled(false);
    }

    /**
     * Открываем настройки изображения
     */
    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }
    /**
     * Создание диалога
     */
    @Override
    protected Dialog onCreateDialog ( int id){
        switch (id) {
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String[] selectItems = {getString(R.string.user_profile_dialog_gallery),
                        getString(R.string.user_profile_dialog_camera), getString(R.string.user_profile_dialog_cancel)};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.user_profile_dialog_title);
                builder.setItems(selectItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choiceItem) {
                        switch (choiceItem) {
                            case 0:
                                loadPhotoFromGallery();
                                break;
                            case 1:
                                loadPhotoFromCamera();
                                break;
                            case 2:
                                dialog.cancel();
                                break;
                        }
                    }
                });
                return builder.create();
            default:
                return null;
        }
    }
    /**
     * Создание файла изображения
     */

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATA, image.getAbsolutePath());
        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        return image;
    }

    /**
     * Добавляем изображение пользователя
     *
     * @param selectedImage
     */
    private void insertProfileImage(Uri selectedImage) {
        Picasso.with(this)
                .load(selectedImage)
                .into(mProfileImage);
        mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
    }

    /**
     * Отключение ошибок
     */
    final ButterKnife.Action<TextInputLayout> CHANGE_ERROR_ALL = new ButterKnife.Action<TextInputLayout>() {
        @Override
        public void apply(@NonNull TextInputLayout view, int index) {
            view.setErrorEnabled(false);
        }
    };

    final ButterKnife.Action<EditText> ADD_CHANGED_LISTENER = new ButterKnife.Action<EditText>() {
        @Override
        public void apply(@NonNull EditText view, int index) {
            view.addTextChangedListener(new MyTextWatcher(view, index));
        }
    };

    final ButterKnife.Action<EditText> CHANGE_EDIT_ALL = new ButterKnife.Action<EditText>() {
        @Override
        public void apply(@NonNull EditText view, int index) {
            view.setEnabled(STATUS[mCurrentEditMode]);
            view.setFocusable(STATUS[mCurrentEditMode]);
            view.setFocusableInTouchMode(STATUS[mCurrentEditMode]);
        }
    };


    private class MyTextWatcher implements TextWatcher {

        private int index;
        private View view;

        private MyTextWatcher(View view, int index) {
            this.view = view;
            this.index = index;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edit_phone:
                    showError(validatePhone(userFieldViews.get(index).getText().toString()), index);
                    break;
                case R.id.edit_mail:
                    showError(validateEmail(userFieldViews.get(index).getText().toString()), index);
                    break;
                case R.id.edit_vk:
                    showError(validatePath(userFieldViews.get(index).getText().toString()), index);
                    break;
                case R.id.edit_github:
                    showError(validatePath(userFieldViews.get(index).getText().toString()), index);
                    break;
            }
        }
    }
}
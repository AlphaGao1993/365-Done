package com.alphagao.done365.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.engines.Account;
import com.alphagao.done365.ui.fragment.FestivalFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Alpha on 2017/4/11.
 */

public class SettingsActivity extends BaseActivity {
    private static final String TAG = "SettingsActivity";
    private static final int FROM_CAMREA = 101;
    private static final int FROM_ALBUM = 102;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.head_image_view)
    CircleImageView headImageView;
    @BindView(R.id.user_name_view)
    TextView userNameView;
    @BindView(R.id.user_email_view)
    TextView userEmailView;
    @BindView(R.id.personal_view)
    RelativeLayout personalView;
    @BindView(R.id.custom_festival_view)
    TextView customFestivalView;
    @BindView(R.id.log_out_view)
    TextView logOutView;
    @BindView(R.id.settings_view)
    TextView settingsView;
    @BindView(R.id.past_today_view)
    TextView pastTodayView;
    private AlertDialog chooseImageDialog;
    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setupActionBar();
        loadHeadImage();
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.arrow_back_white_24dp);
        }
    }


    private void loadHeadImage() {
        long userId = userPrefs.getLong("user_id");
        Glide.with(this).load(getFilesDir() + "/head_img_" + userId + ".jpg")
                .signature(new StringSignature(UUID.randomUUID().toString()))
                .error(R.drawable.m06)
                .into(headImageView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @OnClick({R.id.head_image_view, R.id.personal_view, R.id.custom_festival_view,
            R.id.log_out_view, R.id.settings_view, R.id.past_today_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.head_image_view:
                setupHeadImage();
                break;
            case R.id.personal_view:
                jumpToAccountPage();
                break;
            case R.id.custom_festival_view:
                jumpToCustomizeFestival();
                break;
            case R.id.log_out_view:
                attemptLogOut();
                break;
            case R.id.settings_view:
                jumpToConfigPage();
                break;
            case R.id.past_today_view:
                jumpToPastToday();
                break;
            default:
                break;
        }
    }

    private void attemptLogOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.sure_to_exit))
                .setCancelable(true)
                .setNegativeButton(getString(R.string.sign_out), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        logOut();
                    }
                }).show();
    }

    private void jumpToAccountPage() {
        Intent intent = new Intent(this, AccountActivty.class);
        startActivity(intent);
    }

    private void jumpToPastToday() {
        Intent intent = new Intent(this, PastTodayActivity.class);
        startActivity(intent);
    }

    private void jumpToConfigPage() {
        Intent intent = new Intent(this, AppConfigActivity.class);
        startActivity(intent);
    }

    private void logOut() {
        userPrefs.putBoolean("login_succeed", false);
        prefs.putLong("current_login_user", 0L);
        Intent intent = new Intent(this, LoginActivity.class);
        for (Activity act : startedActivity) {
            if (!(act instanceof SettingsActivity)) {
                act.finish();
            }
        }
        startActivity(intent);
        finish();
    }

    private void jumpToCustomizeFestival() {
        Intent intent = new Intent(this, SingleFragmentActivity.class);
        intent.putExtra(SingleFragmentActivity.FRAGMENT_PARAM, FestivalFragment.class);
        startActivity(intent);
    }

    private void setupHeadImage() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_image_choose, null);
        TextView fromAlbumView = (TextView) view.findViewById(R.id.from_album_view);
        TextView fromCameraView = (TextView) view.findViewById(R.id.from_camera_view);
        chooseImageDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.setting_dialog_image_title)
                .setView(view)
                .setCancelable(true)
                .create();
        fromCameraView.setOnClickListener(listener);
        fromAlbumView.setOnClickListener(listener);
        chooseImageDialog.show();
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            chooseImageDialog.dismiss();
            switch (v.getId()) {
                case R.id.from_camera_view:
                    attemptTakePhoto();
                    break;
                case R.id.from_album_view:
                    pickImageFromAlbum();
                    break;
                default:
                    break;
            }
        }
    };

    private void pickImageFromAlbum() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            jumpToChoosePic();
        }
    }

    private void jumpToChoosePic() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, FROM_ALBUM);
    }

    private void attemptTakePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 2);
        } else {
            jumpToTakePhoto();
        }
    }

    private void jumpToTakePhoto() {
        File outPutImage = new File(getExternalCacheDir(), "image.jpg");
        try {
            if (outPutImage.exists()) {
                outPutImage.delete();
            }
            outPutImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(SettingsActivity.this,
                    "com.alphagao.done365.fileprovider", outPutImage);
        } else {
            imageUri = Uri.fromFile(outPutImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, FROM_CAMREA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FROM_CAMREA:
                    handleBackImage();
                    break;
                case FROM_ALBUM:
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitkat(data);
                    } else {
                        handleImageBeforeKitkat(data);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void handleBackImage() {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory
                    .decodeStream(getContentResolver().openInputStream(imageUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        saveImageBitmap(bitmap);
    }

    private void saveImageBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            long userId = userPrefs.getLong("user_id");
            OutputStream out;
            try {
                out = new FileOutputStream(
                        getFilesDir() + "/head_img_" + userId + ".jpg");
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, out);
                if (out != null) {
                    out.close();
                }
                callToUpdateHeadImage();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void handleImageBeforeKitkat(Intent data) {
        Uri uri = data.getData();
        String imagepath = getImagePath(uri, null);
        setHeadImageFromFile(imagepath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @TargetApi(19)
    private void handleImageOnKitkat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        setHeadImageFromFile(imagePath);
    }

    private void setHeadImageFromFile(String imagepath) {
        if (imagepath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagepath);
            saveImageBitmap(bitmap);
        }
    }

    private void callToUpdateHeadImage() {
        loadHeadImage();
        Message msg = Message.obtain();
        msg.what = Account.HEAD_IMAGE_UPDATE;
        messageManager.publishMessage(msg);
    }

    @Override
    public void onNewMessage(Message msg) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    jumpToChoosePic();
                } else {
                    toast(getString(R.string.request_permission_error_picture));
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    jumpToTakePhoto();
                } else {
                    toast(getString(R.string.request_permission_error_capture));
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        userNameView.setText(userPrefs.getString("user_name"));
        userEmailView.setText(userPrefs.getString("user_email"));
    }
}

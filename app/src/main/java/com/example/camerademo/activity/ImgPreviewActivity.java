package com.example.camerademo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.camerademo.App;
import com.example.camerademo.R;
import com.example.camerademo.rx.Rx;
import com.example.camerademo.utils.Constants;
import com.example.camerademo.utils.Picture;

import java.io.File;

public class ImgPreviewActivity extends BaseActivity {

    private static final int REQUEST_RECAPTURE = 1;

    ImageView mPreviewPicture;
    Button mBtnRecapture;
    Button mBtnOk;
    private boolean isModify;
    private String imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_preview);
        mPreviewPicture = (ImageView) findViewById(R.id.preview_picture);
        mBtnRecapture = (Button) findViewById(R.id.btn_delete);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        initView();
        initEvent();
    }

    private void initEvent() {
        Rx.clicks(mBtnOk, v -> uploadImg());
        Rx.clicks(mBtnRecapture, v -> deletePic());
    }

    private void deletePic() {
        boolean delete = false;
        if (!TextUtils.isEmpty(imgPath)) {
            File file = new File(imgPath);
            if (file.exists()) {
                delete = file.delete();
            }
        }
        //切换图片
        if (delete) {
            File latestPicture = Picture.getLatestPicture(this);
            if (latestPicture != null) {
                imgPath = latestPicture.getAbsolutePath();
                loadImg(imgPath);
            }
        }
    }

    private void uploadImg() {
        showMessage("确定");
    }

    private void initView() {
        imgPath = getIntent().getStringExtra(Constants.IMG_PATH);
        isModify = getIntent().getBooleanExtra(Constants.IS_MODIFY, false);
        loadImg(imgPath);
    }

    private void loadImg(String imgPath) {
        if (!TextUtils.isEmpty(imgPath)) {
            Glide.with(this)
                    .load(imgPath)
                    .crossFade()
                    .into(mPreviewPicture);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_RECAPTURE:
                    if (data != null) {
                        String imgPath = data.getStringExtra(Constants.IMG_PATH);
                        loadImg(imgPath);
                    }
                    break;
            }
        }
    }

    public static Intent getNewIntent(String path) {
        Intent intent = new Intent(App.getInstance(), ImgPreviewActivity.class);
        if (!TextUtils.isEmpty(path)) {
            intent.putExtra(Constants.IMG_PATH, path);
        }
        return intent;
    }

    public static Intent getNewIntent(String path, boolean isModify) {
        Intent intent = getNewIntent(path);
        intent.putExtra(Constants.IS_MODIFY, isModify);
        return intent;
    }

}

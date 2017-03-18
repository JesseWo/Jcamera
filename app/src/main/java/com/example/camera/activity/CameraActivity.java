package com.example.camera.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.camera.App;
import com.example.camera.camera.CameraManager;
import com.example.camera.fragment.ShowInfoDialog;
import com.example.camera.rx.Rx;
import com.example.camera.utils.Constants;
import com.example.camera.utils.GravityManager;
import com.example.camera.utils.LOG;
import com.example.camera.utils.Picture;
import com.example.camera.utils.ScreenUtil;
import com.example.camerademo.BuildConfig;
import com.example.camerademo.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CameraActivity extends BaseActivity implements SurfaceHolder.Callback,
        Camera.ShutterCallback, Camera.PictureCallback {

    private static final String TAG = CameraActivity.class.getSimpleName();
    private static final boolean debug = BuildConfig.DEBUG;

    private static final int REQUEST_PREVIEW = 2;

    private SurfaceView mPreview;
    private Button mCapture;

    protected String savePicturePath;

    private boolean hasSurface;
    protected boolean takingPicture = false;
    private AutoFocusHandle mAutoFocusHandle;
    private boolean hasAutoFocusFeature = false;
    private static final int MENU_ITEM_ID_FLASH = 0;
    private static final int MENU_ITEM_ID_TRANSFORM = 1;
    private boolean isScreenLightClosed;
    private int originScreenLight;
    private boolean isAutoBrightness;
    private ImageView ivBg;
    private static final int ANIM_DURATION = 800;
    private ImageView ivPreview;
    private Button btnSwitchCamera;
    private SimpleDateFormat sdf;
    private AnimatorSet animatorSet;
    private boolean isSwitchCamera;
    private ImageView ivCapturePreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        initView();
        initEvent();
        mAutoFocusHandle = new AutoFocusHandle(this);
        CameraManager.init(this);
        hasSurface = false;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        CameraManager.get().setCameraFacingType(getDefaultCamera());

        //记录屏幕亮度
        originScreenLight = ScreenUtil.getScreenBrightness(this);
        isAutoBrightness = ScreenUtil.isAutoBrightness(this);
        //重力感应器初始化
        GravityManager.getInstance().init(this, ivPreview, btnSwitchCamera);
    }

    @Override
    public void onResume() {
        super.onResume();
        openCamera();
        //注册重力感应器
        GravityManager.getInstance().register();
        //显示最近的一张照片
        File latestPicture = Picture.getLatestPicture(this);
        if (latestPicture != null) {
            Glide.with(this).load(latestPicture).into(ivPreview);
            savePicturePath = latestPicture.getAbsolutePath();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        closeCamera();
        GravityManager.getInstance().unregister();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem flashItem = menu.add(1, MENU_ITEM_ID_FLASH, Menu.NONE, "闪光灯");
        flashItem.setIcon(R.mipmap.flash);
        flashItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        flashItem.setVisible(CameraManager.get().isSupportFlashLight());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int type = CameraManager.get().getCameraFacingType();
        if (item.getItemId() == MENU_ITEM_ID_FLASH) {
            // flash light
            switch (type) {
                case Camera.CameraInfo.CAMERA_FACING_FRONT:
                    //调高屏幕亮度
                    isScreenLightClosed = !isScreenLightClosed;
                    ScreenUtil.setLight(this, isScreenLightClosed ? 255 : originScreenLight);
                    break;
                case Camera.CameraInfo.CAMERA_FACING_BACK:
                    //开闪光灯
                    boolean light = !CameraManager.get().isFlashLight();
                    CameraManager.get().setFlashLight(light);
                    if (debug)
                        LOG.d(TAG, "flash light " + light);
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void initAnim(ImageView iv) {
        ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(iv, "rotationY", 0, 180);
        ObjectAnimator scaleAnim1 = ObjectAnimator.ofFloat(iv, "scaleY", 1f, 0.7f, 1f);
        ObjectAnimator scaleAnim2 = ObjectAnimator.ofFloat(iv, "scaleX", 1f, 0.3f, 1f);
        animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000).playTogether(rotateAnim, scaleAnim1, scaleAnim2);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                iv.setVisibility(View.VISIBLE);
                closeCamera();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                openCamera();
                iv.setVisibility(View.INVISIBLE);
                isSwitchCamera = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void initView() {
        setTitle("JCamera");
        mPreview = (SurfaceView) findViewById(R.id.preview);
        mCapture = (Button) findViewById(R.id.capture);
        btnSwitchCamera = (Button) findViewById(R.id.btn_transform);
        ivBg = (ImageView) findViewById(R.id.capture_bg);
        ivCapturePreview = (ImageView) findViewById(R.id.capture_preview);
        ivPreview = (ImageView) findViewById(R.id.iv_preview);
    }

    private void initEvent() {
        Rx.clicks(mCapture, v -> takePicture());
        Rx.clicks(btnSwitchCamera, v -> switchCamera());
        Rx.clicks(mPreview, v -> AutoFocusOnce());
        Rx.clicks(ivPreview, v -> previewBigImg());
        initAnim(ivCapturePreview);
    }

    private void switchCamera() {
        isSwitchCamera = true;
        getCameraManager().takePicture(this, this);
    }

    private String getPictureName() {
        if (sdf == null) {
            sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        }
        return sdf.format(new Date()) + ".jpg";
    }

    protected boolean needRotatePicture() {
        return getIntent().getBooleanExtra(Constants.CARD, false);
    }

    protected int getDefaultCamera() {
        return getIntent().getIntExtra(Constants.FACE, Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    /**
     * 拍摄完成后回调
     *
     * @param data
     * @param camera
     */
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        if (isSwitchCamera) {
            Camera.Size size = camera.getParameters().getPictureSize();
            Bitmap bitmap = Picture.getBitmap(data, size.width, size.height);
            if (bitmap != null) {
                LOG.d(TAG, "bitmap " + bitmap.getWidth() + " " + bitmap.getHeight());
                LOG.d(TAG, "camera " + size.width + " " + size.height);
                int degree = 0;
                if (CameraManager.get().getCameraFacingType() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    degree = 270;
                } else {
                    if (needRotatePicture()) {
                        if (bitmap.getHeight() > bitmap.getWidth()) {
                            degree = -90;
                        }
                    } else {
                        if (bitmap.getHeight() < bitmap.getWidth()) {
                            degree = 90;
                        }
                    }
                }
                if (degree != 0)
                    bitmap = Picture.rotateBitmapByDegree(bitmap, degree);
                Bitmap blurBitmap = Picture.doBlur(bitmap, 10, true);
                ivCapturePreview.setImageBitmap(blurBitmap);

                int type = CameraManager.get().getCameraFacingType();
                switch (type) {
                    case Camera.CameraInfo.CAMERA_FACING_FRONT:
                        CameraManager.get().setCameraFacingType(Camera.CameraInfo.CAMERA_FACING_BACK);
                        break;
                    case Camera.CameraInfo.CAMERA_FACING_BACK:
                        CameraManager.get().setCameraFacingType(Camera.CameraInfo.CAMERA_FACING_FRONT);
                        break;
                }
                animatorSet.start();
                return;
            }
        }
        takingPicture = false;
        getCameraManager().setFlashLight(false);

        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Camera.Size size = camera.getParameters().getPictureSize();
                Bitmap bitmap = Picture.getBitmap(data, size.width, size.height);
                if (bitmap != null) {
                    LOG.d(TAG, "bitmap " + bitmap.getWidth() + " " + bitmap.getHeight());
                    LOG.d(TAG, "camera " + size.width + " " + size.height);
                    int degree = 0;
                    if (CameraManager.get().getCameraFacingType() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        degree = 270;
                    } else {
                        if (needRotatePicture()) {
                            if (bitmap.getHeight() > bitmap.getWidth()) {
                                degree = -90;
                            }
                        } else {
                            if (bitmap.getHeight() < bitmap.getWidth()) {
                                degree = 90;
                            }
                        }
                    }
                    if (degree != 0)
                        bitmap = Picture.rotateBitmapByDegree(bitmap, degree);
                    //保存图片
                    String imgPath = Picture.savePicture(CameraActivity.this, bitmap,
                            getPictureName(), Bitmap.CompressFormat.JPEG);
                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                    subscriber.onNext(imgPath);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imgPath -> {
                    savePicturePath = imgPath;
                    takingPicture = false;
                    //显示图片
                    Glide.with(this)
                            .load(savePicturePath)
                            .crossFade()
                            .into(ivPreview);
                });
    }

    private void previewBigImg() {
        boolean isModify = getIntent().getBooleanExtra(Constants.IS_MODIFY, false);
        if (isModify) {
            //重新拍摄
            Intent intent = new Intent();
            intent.putExtra(Constants.IMG_PATH, savePicturePath);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            //首次拍摄
            startActivityForResult(ImgPreviewActivity.getNewIntent(savePicturePath), REQUEST_PREVIEW);
        }
    }

    public CameraManager getCameraManager() {
        return CameraManager.get();
    }

    public void takePicture() {
        if (!takingPicture) {
            takingPicture = true;
            mAutoFocusHandle.removeMessages(AutoFocusHandle.FOCUS);
            getCameraManager().takePicture(this, this);
        }
    }

    protected void openCamera() {
        SurfaceHolder surfaceHolder = mPreview.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    protected void closeCamera() {
        if (mAutoFocusHandle != null) {
            mAutoFocusHandle.removeMessages(AutoFocusHandle.FOCUS);
            mAutoFocusHandle.removeMessages(AutoFocusHandle.FOCUS_FINISH);
        }
        CameraManager.get().stopPreview();
        CameraManager.get().closeDriver();
    }

    protected boolean initCamera(SurfaceHolder surfaceHolder) {
        boolean init = true;
        try {
            CameraManager.get().openDriver(surfaceHolder);
            setPictureParameters(CameraManager.get());
            setAutoFocus(CameraManager.get());
            CameraManager.get().configSurfaceView(mPreview);
            startPreview();
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            init = false;
        }

        if (!init) {
            Bundle args = new Bundle();
            args.putBoolean(ShowInfoDialog.SHOW_CANCEL_BUTTON, false);
            args.putBoolean(ShowInfoDialog.CANCELABLE, false);
            args.putString(ShowInfoDialog.INFO, "Camera init failed!");
            ShowInfoDialog dialog = ShowInfoDialog.newInstance((obj) -> finish(), args);
            dialog.show(getSupportFragmentManager(), ShowInfoDialog.TAG);
        }
        return init;
    }

    protected void startPreview() {
        CameraManager.get().startPreview();
        CameraManager.get().requestAutoFocus(mAutoFocusHandle, AutoFocusHandle.FOCUS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
        LOG.d(TAG, "surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LOG.d(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
        LOG.d(TAG, "surfaceDestroyed");
    }

    @Override
    public void onShutter() {
        if (debug)
            LOG.d(TAG, "onShutter");
    }

    private void setPictureParameters(CameraManager manager) {
        Camera camera = manager.getCamera();
        if (camera != null) {
            Camera.Parameters params = camera.getParameters();
            //Camera.Size size = params.getPreviewSize();
            //params.setPictureSize(size.width, size.height);
            params.set("jpeg-quality", 100);
            camera.setParameters(params);
        } else {
            LOG.e(TAG, "setPictureParameters camera is null");
        }
    }

    private void setAutoFocus(CameraManager manager) {
        Camera camera = manager.getCamera();
        if (camera != null) {
            Camera.Parameters params = camera.getParameters();
            if (params.getSupportedFocusModes().
                    contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                camera.setParameters(params);
                hasAutoFocusFeature = true;
            }
        } else {
            LOG.e(TAG, "setAutoFocus camera is null");
        }
    }

    protected void AutoFocusOnce() {
        getCameraManager().requestAutoFocus(mAutoFocusHandle, AutoFocusHandle.FOCUS_ONCE);
    }

    static class AutoFocusHandle extends Handler {

        public static final int FOCUS_FINISH = 0;
        public static final int FOCUS = 1;
        public static final int FOCUS_ONCE = 2;

        private CameraActivity mCaptureMeActivity;
        public boolean focusSuccess = false;

        public AutoFocusHandle(CameraActivity mCaptureMeActivity) {
            this.mCaptureMeActivity = mCaptureMeActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            LOG.d(TAG, "handleMessage " + msg.what);
            switch (msg.what) {
                case FOCUS_FINISH:
                    LOG.d(TAG, "focus finish");
                    mCaptureMeActivity.getCameraManager().takePicture(mCaptureMeActivity,
                            mCaptureMeActivity);
                    break;
                case FOCUS:
                    focusSuccess = (boolean) msg.obj;
                    mCaptureMeActivity.getCameraManager().requestAutoFocus(this, FOCUS);
                    break;
                case FOCUS_ONCE:
                    focusSuccess = (boolean) msg.obj;
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PREVIEW && resultCode == RESULT_OK) {
            setResult(resultCode, data);
            finish();
        }
    }

    public static Intent getNewIntent(boolean isFront, boolean isModify) {
        Intent intent = new Intent(App.getInstance(), CameraActivity.class);
        intent.putExtra(Constants.IS_MODIFY, isModify);
        if (isFront) {
            intent.putExtra(Constants.FACE, Camera.CameraInfo.CAMERA_FACING_FRONT);
        }
        return intent;
    }

    public static Intent getNewIntent(boolean isFront) {
        return getNewIntent(isFront, false);
    }
}

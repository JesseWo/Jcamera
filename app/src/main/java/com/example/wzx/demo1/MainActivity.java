package com.example.wzx.demo1;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = (ImageView) findViewById(R.id.iv);
        String imgUrl = "http://img001.file.rongbiz.net/uploadfile/201305/28/09/25-15-86959.jpg";
        Glide.with(this)
                .load(imgUrl)
                .placeholder(R.drawable.height)
//                .error(R.mipmap.ic_launcher)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Log.d(TAG, "onException:" + e.getMessage() + "model: " + model);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        Log.d(TAG, "onResourceReady");
                        return false;
                    }
                })
                .into(iv);
        Button btnTrans = (Button) findViewById(R.id.btn_trans);
        btnTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(iv, "rotationY", 0, 180);
                ObjectAnimator scaleAnim1 = ObjectAnimator.ofFloat(iv, "scaleY", 1f, 0.7f, 1f);
                ObjectAnimator scaleAnim2 = ObjectAnimator.ofFloat(iv, "scaleX", 1f, 0.3f, 1f);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(rotateAnim, scaleAnim1, scaleAnim2);
                animatorSet.setDuration(1000).start();
            }
        });
    }
}

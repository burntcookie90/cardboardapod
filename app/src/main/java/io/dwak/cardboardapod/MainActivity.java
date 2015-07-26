package io.dwak.cardboardapod;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;

import io.dwak.cardboardapod.databinding.MainActivityBinding;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.ViewObservable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private PublishSubject<Pair<Float, Float>> mGyroSubject = PublishSubject.create();
    private float translationX = 0.0f;
    private float translationY = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        MainActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        MainViewModel mainViewModel = new MainViewModel();
        mainViewModel.subscribe()
                     .subscribeOn(Schedulers.io())
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribe(new Subscriber<ApodImage>() {
                         @Override
                         public void onCompleted() {

                         }

                         @Override
                         public void onError(Throwable e) {

                         }

                         @Override
                         public void onNext(ApodImage apodImage) {
                             if (apodImage.url.contains("png")
                                     || apodImage.url.contains("bmp")
                                     || apodImage.url.contains("jpg")) {
                                 Glide.with(MainActivity.this)
                                      .load(apodImage.url)
                                      .fitCenter()
                                      .into(binding.leftImage);

                                 Glide.with(MainActivity.this)
                                      .load(apodImage.url)
                                      .into(binding.rightImage);
                                 mainViewModel.postImage(apodImage.url);
                             }
                             else {
                                 mainViewModel.getImage();
                             }
                         }
                     });
        mainViewModel.getImage();
        ViewObservable.clicks(binding.clickInterceptor)
                      .subscribe(onClickEvent -> {
                          mainViewModel.getImage();
                      });

        mGyroSubject.asObservable().subscribeOn(Schedulers.immediate())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Pair<Float, Float>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Pair<Float, Float> xyPair) {
                            Log.d("WDGW", xyPair.first + " " + xyPair.second);
//                        binding.leftImage.animate().setInterpolator(new LinearInterpolator()).translationY(-xyPair.second);
//                        binding.leftImage.animate().setInterpolator(new LinearInterpolator()).translationX(xyPair.first);
//
//                        binding.rightImage.animate().setInterpolator(new LinearInterpolator()).translationY(-xyPair.second);
//                        binding.rightImage.animate().setInterpolator(new LinearInterpolator()).translationX(xyPair.first);
                        }
                    });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register this class as a listener for the accelerometer sensor
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR),
                                        SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        // Unregister the listener
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            int someNumber = 500;
            float xChange = someNumber * event.values[0];
            //values[2] can be -90 to 90
            float yChange = someNumber * 2 * event.values[1];
            mGyroSubject.onNext(new Pair<>(xChange, yChange));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

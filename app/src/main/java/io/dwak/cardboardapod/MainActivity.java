package io.dwak.cardboardapod;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;

import io.dwak.cardboardapod.databinding.MainActivityBinding;
import io.dwak.cardboardapod.util.SystemUiHider;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity {

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
                             if(apodImage.url.contains("png")
                                     || apodImage.url.contains("bmp")
                                     || apodImage.url.contains("jpg")){
                                 Glide.with(MainActivity.this)
                                      .load(apodImage.url)
                                      .fitCenter()
                                      .into(binding.leftImage);

                                 Glide.with(MainActivity.this)
                                      .load(apodImage.url)
                                      .into(binding.rightImage);
                             }
                             else {
                                 mainViewModel.getImage();
                             }
                         }
                     });


        mainViewModel.getImage();
        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.

        binding.clickInterceptor.setOnClickListener(v -> mainViewModel.getImage());
    }
}

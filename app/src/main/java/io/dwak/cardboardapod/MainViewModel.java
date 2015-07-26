package io.dwak.cardboardapod;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class MainViewModel {

    private final ApodService mService;
    private Date mApodDate;
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private PublishSubject<ApodImage> mSubject = PublishSubject.create();
    private boolean mFirstImage = true;
    private final SpaceService mSpaceService;

    public MainViewModel() {
        mService = new RestAdapter.Builder().setConverter(new GsonConverter(new Gson()))
                                            .setLogLevel(RestAdapter.LogLevel.FULL)
                                            .setEndpoint("https://api.nasa.gov")
                                            .build().create(ApodService.class);

        mSpaceService = new RestAdapter.Builder()
                .setConverter(new GsonConverter(new Gson()))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("https://space-image-viewer.firebaseio.com/.json")
                .build().create(SpaceService.class);

        mApodDate = new Date();
    }

    void getImage(){

        if(!mFirstImage){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mApodDate);
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            mApodDate = calendar.getTime();
        }

        mService.getApod(mSimpleDateFormat.format(mApodDate), BuildConfig.API_KEY)
                .subscribeOn(Schedulers.io())
                .subscribe(mSubject::onNext);
        mFirstImage = false;
    }

    void postImage(String url){
        mSpaceService.sendImage(new SpaceServiceImage(url))
                .subscribe(s -> {
                });
    }

    Observable<ApodImage> subscribe(){
        return mSubject.asObservable();
    }
}

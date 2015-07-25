package io.dwak.cardboardapod;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface ApodService {
    @GET("/planetary/apod?concept_tags=True")
    Observable<ApodImage> getApod(@Query("date") String date,
                                  @Query("api_key") String apiKey);
}

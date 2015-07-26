package io.dwak.cardboardapod;

import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

public interface SpaceService {
    @POST("/")
    Observable<String> sendImage(@Body SpaceServiceImage spaceServiceImage);
}

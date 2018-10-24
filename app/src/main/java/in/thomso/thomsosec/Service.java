package in.thomso.thomsosec;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface Service {
    @POST("api/mobile/register/media")
    Call<Response> postImage(@Body Data data);
}

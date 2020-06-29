package cn.mwee.base_common.helper.retrofit.api;

import lombok.NonNull;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

/**
 * Created by liaomengge on 2019/3/25.
 */
public interface RetrofitApi {

    @GET
    Call<ResponseBody> doGet(@Url String url, @QueryMap @NonNull Map<String, String> param);

    @Headers({"Content-Type:application/json"})
    @POST
    Call<ResponseBody> doPost(@Url String url, @Body @NonNull Object param);

    @Headers({"Content-Type:application/x-www-form-urlencoded"})
    @FormUrlEncoded
    @POST
    Call<ResponseBody> doFormPost(@Url String url, @FieldMap @NonNull Map<String, String> param);
}

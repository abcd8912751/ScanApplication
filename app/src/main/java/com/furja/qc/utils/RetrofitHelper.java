package com.furja.qc.utils;



import com.furja.qc.beans.AssemblyBoard;
import com.furja.qc.beans.BaseHttpResponse;
import com.furja.qc.beans.InspectionHistory;
import com.furja.qc.beans.ProductNoList;
import com.furja.qc.beans.User;
import com.furja.qc.databases.DimenGaugeLog;
import com.furja.qc.databases.ProductModel;
import com.furja.qc.databases.TourInspectionLog;
import com.furja.qc.jsonbeans.MaterialJson;

import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * retrofit请求数据的接口
 */
public interface RetrofitHelper {
    @GET("FJCommonInterface/GetBarCodeInfo/")
    Observable<MaterialJson> getMaterialJson(@Query("BarCode") String barCode);

    @Headers({"Content-Type: application/json"})
    @POST("base/assemblylog")
    Observable<BaseHttpResponse<String>> postAssemblyLog(@Body RequestBody body);

    @GET("base/assemblyboard")
    Observable<BaseHttpResponse<List<AssemblyBoard>>> getAssemblyBoard(@Query("fstartdate") String startdate,@Query("fenddate") String enddate);

    @GET("base/productmodel")
    Observable<BaseHttpResponse<List<ProductModel>>> getProductModel();


    @Headers({"Content-Type: application/json"})
    @POST("base/zstourinspectionlog/mobile")
    Observable<BaseHttpResponse<String>> postTourInspectionLog(@Body RequestBody body);

    @GET("base/zstourinspectionlog/mobile")
    Observable<BaseHttpResponse<TourInspectionLog>> getTourInspectionLog(@Query("materialisn") String materialISN,
                                                                         @Query("producedate") String produceDate,
                                                                         @Query("timeperiod") String timePeriod,@Query("moldno") String moldNo);

    @Headers({"Content-Type: application/json"})
    @POST("base/zsdimengaugelog/mobile")
    Observable<BaseHttpResponse<String>> postDimenGaugeLog(@Body RequestBody requestBody);

    @GET("base/zsdimengaugelog/mobile")
    Observable<BaseHttpResponse<DimenGaugeLog>> getDimenGaugeLog(@Query("materialisn") String materialISN,
                                                                    @Query("producedate") String produceDate,
                                                                    @Query("timeperiod") String timePeriod,
                                                                    @Query("moldno") String moldNo,
                                                                    @Query("type") String type);
    @GET("base/inspectionhistory")
    Observable<BaseHttpResponse<List<InspectionHistory>>> getInspectionHistory(@Query("workplaceid") String workplaceID);

    @Headers({"Content-Type: application/json"})
    @POST("base/errorlog")
    Observable<ResponseBody> submitErrorLog(@Body RequestBody requestBody);

    @GET("FJCommonInterface/GetBarCodeInfo/")
    Observable<MaterialJson> getMaterialWithSop(@Query("barCode") String barCode);

    @GET
    Observable<ResponseBody> request(@Url String url);

    @GET("base/passport")
    Observable<BaseHttpResponse<User>> login(@Query("username") String userName, @Query("password") String password);

    @Multipart
    @POST("FJBadTypeInterface/SendBadTypeLog/")
    Observable<ResponseBody> postInjectionLog(@PartMap Map<String, RequestBody> uploadParams);
}

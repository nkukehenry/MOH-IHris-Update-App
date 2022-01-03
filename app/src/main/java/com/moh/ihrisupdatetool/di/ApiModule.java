package com.moh.ihrisupdatetool.di;

import android.app.Application;
import android.app.ProgressDialog;

import com.moh.ihrisupdatetool.api.AppApi;
import com.moh.ihrisupdatetool.repo.remote.AppRemoteCallRepository;
import com.moh.ihrisupdatetool.repo.remote.IAppRemoteCallRepository;
import com.moh.ihrisupdatetool.services.IApiErrorHandlingService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.moh.ihrisupdatetool.utils.AppConstants.BASE_URL;

@Module
@InstallIn(SingletonComponent.class)
public class ApiModule {


    @Singleton
    @Provides
    public AppApi provideAppApiApiService(Retrofit retrofit){
        return retrofit.create(AppApi.class);
    }

    @Singleton
    @Provides
    public static Interceptor provideErrorHandlingInterceptor(IApiErrorHandlingService apiErrorHandler) {

        return new Interceptor()  {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request request = chain.request();

                apiErrorHandler.showNetworkRequestLoader();

                //get them from header service if needed
                Map<String, String > headers= new HashMap<>();
                Request.Builder requestBuilder = request.newBuilder();

                for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                    requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
                 }

                Response response = chain.proceed(requestBuilder.build());

               // apiErrorHandler.closeNetworkRequestLoader();

                return response;
            }
        };
    }

    @Singleton
    @Provides
    public static OkHttpClient provideOkHttpClient(Interceptor apiInterceptor
            , HttpLoggingInterceptor loggingInterceptor
            ,HostnameVerifier hostnameVerifier) {

        return new OkHttpClient.Builder()
                .connectTimeout(60L, TimeUnit.SECONDS)
                .readTimeout(60L, TimeUnit.SECONDS)
                .addInterceptor(apiInterceptor)
                .addInterceptor(loggingInterceptor)
                .hostnameVerifier(hostnameVerifier)
                .retryOnConnectionFailure(false)
                .build();
    }

    @Singleton
    @Provides
    public Retrofit provideRetrofit(OkHttpClient client){
        return new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Singleton
    @Provides
    public static HttpLoggingInterceptor provideLoggingInterceptor() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    @Singleton
    @Provides
    public static HostnameVerifier provideHostNameVerifier() {
        return (hostname, session) -> {
            HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
            return true;
        };
    }


    @Singleton
    @Provides
    public IAppRemoteCallRepository providesGenericAppRepository(AppApi appApi){
        return new AppRemoteCallRepository(appApi);
    }

    @Singleton
    @Provides
    public  ProgressDialog provideProgressDialog(Application application) {

        final ProgressDialog loader = new ProgressDialog(application.getApplicationContext());
        loader.setMessage("Loading...");
        return loader;
    }


}

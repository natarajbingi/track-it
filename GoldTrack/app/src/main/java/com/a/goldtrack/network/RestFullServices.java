package com.a.goldtrack.network;

import android.content.Context;

import com.a.goldtrack.Model.AddCompany;
import com.a.goldtrack.Model.AddCompanyRes;
import com.a.goldtrack.Model.GetCompany;
import com.a.goldtrack.Model.GetCompanyRes;
import com.a.goldtrack.Model.UpdateCompanyDetails;
import com.a.goldtrack.Model.UpdateCompanyDetailsRes;
import com.a.goldtrack.company.ICallBacks;
import com.a.goldtrack.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestFullServices {

    public static APIService getClient() {
        OkHttpClient clientWith60sTimeout = new OkHttpClient()
                .newBuilder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(clientWith60sTimeout)
                .build();
        return retrofit.create(APIService.class);
    }


    public static void getCompanyList(GetCompany req, ICallBacks callBacks) {
        getClient().getCompany(req).enqueue(new Callback<GetCompanyRes>() {
            @Override
            public void onResponse(Call<GetCompanyRes> call, Response<GetCompanyRes> response) {
                if (response.isSuccessful())
                    callBacks.onSuccess(response.body());
                else callBacks.onError("");
            }

            @Override
            public void onFailure(Call<GetCompanyRes> call, Throwable t) {
                callBacks.onError(t.getMessage());
            }
        });
    }
    public static void addCompany(AddCompany req, ICallBacks callBacks) {
        getClient().addCompany(req).enqueue(new Callback<AddCompanyRes>() {
            @Override
            public void onResponse(Call<AddCompanyRes> call, Response<AddCompanyRes> response) {
                if (response.isSuccessful())
                    callBacks.onSuccessAddCompany(response.body());
                else callBacks.onError("");
            }

            @Override
            public void onFailure(Call<AddCompanyRes> call, Throwable t) {
                callBacks.onError(t.getMessage());
            }
        });
    }
    public static void updateCompanyDetails(UpdateCompanyDetails req, ICallBacks callBacks) {
        getClient().updateCompanyDetails(req).enqueue(new Callback<UpdateCompanyDetailsRes>() {
            @Override
            public void onResponse(Call<UpdateCompanyDetailsRes> call, Response<UpdateCompanyDetailsRes> response) {
                if (response.isSuccessful())
                    callBacks.onSuccessUpdateCompany(response.body());
                else callBacks.onError("");
            }

            @Override
            public void onFailure(Call<UpdateCompanyDetailsRes> call, Throwable t) {
                callBacks.onError(t.getMessage());
            }
        });
    }

}

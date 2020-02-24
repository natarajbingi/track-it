package com.a.goldtrack.network;

import android.content.Context;

import com.a.goldtrack.Model.AddCompany;
import com.a.goldtrack.Model.AddCompanyBranchesReq;
import com.a.goldtrack.Model.AddCompanyBranchesRes;
import com.a.goldtrack.Model.AddCompanyRes;
import com.a.goldtrack.Model.AddItemReq;
import com.a.goldtrack.Model.AddItemRes;
import com.a.goldtrack.Model.GetCompany;
import com.a.goldtrack.Model.GetCompanyBranches;
import com.a.goldtrack.Model.GetCompanyBranchesRes;
import com.a.goldtrack.Model.GetCompanyRes;
import com.a.goldtrack.Model.GetItemsReq;
import com.a.goldtrack.Model.GetItemsRes;
import com.a.goldtrack.Model.UpdateCompanyDetails;
import com.a.goldtrack.Model.UpdateCompanyDetailsRes;
import com.a.goldtrack.Model.UpdateItemReq;
import com.a.goldtrack.Model.UpdateItemRes;
import com.a.goldtrack.Model.UserLoginReq;
import com.a.goldtrack.Model.UserLoginRes;
import com.a.goldtrack.company.ICallBacks;
import com.a.goldtrack.companybranche.IBranchCallBacks;
import com.a.goldtrack.items.IItemsCallBacks;
import com.a.goldtrack.login.ILoginCallBacks;
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


    /*LOGIN*/
    public static void login(UserLoginReq req, ILoginCallBacks callBacks) {
        getClient().userLogin(req).enqueue(new Callback<UserLoginRes>() {
            @Override
            public void onResponse(Call<UserLoginRes> call, Response<UserLoginRes> response) {
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful())
                    callBacks.onSuccess(response.body());
                else callBacks.onError("Something went wrong,Server Error");
            }

            @Override
            public void onFailure(Call<UserLoginRes> call, Throwable t) {
                callBacks.onError(t.getMessage());
            }
        });
    }

    /*Company API's*/
    public static void getCompanyList(GetCompany req, ICallBacks callBacks) {
        getClient().getCompany(req).enqueue(new Callback<GetCompanyRes>() {
            @Override
            public void onResponse(Call<GetCompanyRes> call, Response<GetCompanyRes> response) {
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful())
                    callBacks.onSuccess(response.body());
                else callBacks.onError("Something went wrong,Server Error");
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
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful())
                    callBacks.onSuccessAddCompany(response.body());
                else callBacks.onError("Something went wrong,Server Error");
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
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful())
                    callBacks.onSuccessUpdateCompany(response.body());
                else callBacks.onError("Something went wrong,Server Error");
            }

            @Override
            public void onFailure(Call<UpdateCompanyDetailsRes> call, Throwable t) {
                callBacks.onError(t.getMessage());
            }
        });
    }

    /*Branches*/
    public static void addBranch(AddCompanyBranchesReq req, IBranchCallBacks callBacks) {
        getClient().addCompanyBranches(req).enqueue(new Callback<AddCompanyBranchesRes>() {
            @Override
            public void onResponse(Call<AddCompanyBranchesRes> call, Response<AddCompanyBranchesRes> response) {
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful())
                    callBacks.onSuccessAddBranch(response.body());
                else callBacks.onError("Something went wrong,Server Error");

            }

            @Override
            public void onFailure(Call<AddCompanyBranchesRes> call, Throwable t) {
                callBacks.onError(t.getMessage());
            }
        });
    }

    public static void getBranches(GetCompanyBranches req, IBranchCallBacks callBacks) {
        getClient().getCompanyBranches(req).enqueue(new Callback<GetCompanyBranchesRes>() {
            @Override
            public void onResponse(Call<GetCompanyBranchesRes> call, Response<GetCompanyBranchesRes> response) {
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful())
                    callBacks.onSuccessGetBranch(response.body());
                else callBacks.onError("Something went wrong,Server Error");
            }

            @Override
            public void onFailure(Call<GetCompanyBranchesRes> call, Throwable t) {
                callBacks.onError(t.getMessage());
            }
        });
    }

    public static void updateBranch(UpdateCompanyDetails req, IBranchCallBacks callBacks) {

    }

    /*Items*/
    public static void addItem(AddItemReq req, IItemsCallBacks callBacks) {
        getClient().addItem(req).enqueue(new Callback<AddItemRes>() {
            @Override
            public void onResponse(Call<AddItemRes> call, Response<AddItemRes> response) {
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful())
                    callBacks.onAddItemSuccess(response.body());
                else callBacks.onError("Something went wrong,Server Error");
            }

            @Override
            public void onFailure(Call<AddItemRes> call, Throwable t) {
                callBacks.onError(t.getMessage());
            }
        });
    }

    public static void getItems(GetItemsReq req, IItemsCallBacks callBacks) {
        getClient().getItemsForCompany(req).enqueue(new Callback<GetItemsRes>() {
            @Override
            public void onResponse(Call<GetItemsRes> call, Response<GetItemsRes> response) {

                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful())
                    callBacks.onGetItemsForCompany(response.body());
                else callBacks.onError("Something went wrong,Server Error");
            }

            @Override
            public void onFailure(Call<GetItemsRes> call, Throwable t) {
                callBacks.onError(t.getMessage());
            }
        });
    }

    public static void updateItem(UpdateItemReq req, IItemsCallBacks callBacks) {
        getClient().updateItemDetails(req).enqueue(new Callback<UpdateItemRes>() {
            @Override
            public void onResponse(Call<UpdateItemRes> call, Response<UpdateItemRes> response) {

                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful())
                    callBacks.onUpdateItemDetails(response.body());
                else callBacks.onError("Something went wrong,Server Error");
            }

            @Override
            public void onFailure(Call<UpdateItemRes> call, Throwable t) {
                callBacks.onError(t.getMessage());
            }
        });
    }
}

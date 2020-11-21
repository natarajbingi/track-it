package com.a.goldtrack.network;

import com.a.goldtrack.GTrackApplication;
import com.a.goldtrack.Model.AddCompany;
import com.a.goldtrack.Model.AddCompanyBranchesReq;
import com.a.goldtrack.Model.AddCompanyBranchesRes;
import com.a.goldtrack.Model.AddCompanyRes;
import com.a.goldtrack.Model.AddCustomerReq;
import com.a.goldtrack.Model.AddCustomerRes;
import com.a.goldtrack.Model.AddItemReq;
import com.a.goldtrack.Model.AddItemRes;
import com.a.goldtrack.Model.AddRemoveCommonImageReq;
import com.a.goldtrack.Model.AddRemoveCommonImageRes;
import com.a.goldtrack.Model.AddTransactionReq;
import com.a.goldtrack.Model.AddTransactionRes;
import com.a.goldtrack.Model.AddUserDailyClosureReq;
import com.a.goldtrack.Model.AddUserDailyClosureRes;
import com.a.goldtrack.Model.AddUserForCompany;
import com.a.goldtrack.Model.AddUserForCompanyRes;
import com.a.goldtrack.Model.CustomerWithOTPReq;
import com.a.goldtrack.Model.CustomerWithOTPRes;
import com.a.goldtrack.Model.DropdownDataForCompanyRes;
import com.a.goldtrack.Model.GetCompany;
import com.a.goldtrack.Model.GetCompanyBranches;
import com.a.goldtrack.Model.GetCompanyBranchesRes;
import com.a.goldtrack.Model.GetCompanyRes;
import com.a.goldtrack.Model.GetCustomerReq;
import com.a.goldtrack.Model.GetCustomerRes;
import com.a.goldtrack.Model.GetItemsReq;
import com.a.goldtrack.Model.GetItemsRes;
import com.a.goldtrack.Model.GetTransactionReq;
import com.a.goldtrack.Model.GetTransactionRes;
import com.a.goldtrack.Model.GetUserDailyClosureReq;
import com.a.goldtrack.Model.GetUserDailyClosureRes;
import com.a.goldtrack.Model.GetUserForCompany;
import com.a.goldtrack.Model.GetUserForCompanyRes;
import com.a.goldtrack.Model.UpdateCompanyBranchesReq;
import com.a.goldtrack.Model.UpdateCompanyBranchesRes;
import com.a.goldtrack.Model.UpdateCompanyDetails;
import com.a.goldtrack.Model.UpdateCompanyDetailsRes;
import com.a.goldtrack.Model.UpdateCustomerReq;
import com.a.goldtrack.Model.UpdateCustomerRes;
import com.a.goldtrack.Model.UpdateItemReq;
import com.a.goldtrack.Model.UpdateItemRes;
import com.a.goldtrack.Model.UpdateUserDailyClosureReq;
import com.a.goldtrack.Model.UpdateUserDailyClosureRes;
import com.a.goldtrack.Model.UpdateUserDetails;
import com.a.goldtrack.Model.UserLoginReq;
import com.a.goldtrack.Model.UserLoginRes;
import com.a.goldtrack.company.ICallBacks;
import com.a.goldtrack.companybranche.IBranchCallBacks;
import com.a.goldtrack.customer.ICustomerCallBacs;
import com.a.goldtrack.dailyclosure.IDailyClosureCallBacks;
import com.a.goldtrack.items.IItemsCallBacks;
import com.a.goldtrack.login.ILoginCallBacks;
import com.a.goldtrack.trans.IDropdownDataCallBacks;
import com.a.goldtrack.trans.ITransCallBacks;
import com.a.goldtrack.ui.home.IHomeFragCallbacks;
import com.a.goldtrack.ui.share.IDailyClosureDashCallBacks;
import com.a.goldtrack.users.IUserCallBacks;
import com.a.goldtrack.utils.Constants;
import com.a.goldtrack.utils.Sessions;

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
                Constants.logPrint(call.request().toString(), req, t.getMessage());
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
                Constants.logPrint(call.request().toString(), req, t.getMessage());
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
                Constants.logPrint(call.request().toString(), req, t.getMessage());
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
                Constants.logPrint(call.request().toString(), req, t.getMessage());
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
                Constants.logPrint(call.request().toString(), req, t.getMessage());
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
                Constants.logPrint(call.request().toString(), req, t.getMessage());
                callBacks.onError(t.getMessage());
            }
        });
    }

    public static void getDropdownDataForCompany(GetCompany req) {
        getClient().getDropdownDataForCompany(req).enqueue(new Callback<DropdownDataForCompanyRes>() {
            @Override
            public void onResponse(Call<DropdownDataForCompanyRes> call, Response<DropdownDataForCompanyRes> response) {
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful()) {
                    if (response.body().success)
                        Sessions.setUserObj(GTrackApplication.getInstance().getApplicationContext(), response.body(), Constants.dorpDownSession);
                } else {

                }
            }

            @Override
            public void onFailure(Call<DropdownDataForCompanyRes> call, Throwable t) {
                Constants.logPrint(call.request().toString(), req, t.getMessage());

            }
        });

    }

    public static void getDropdownDataForCompanyHome(GetCompany req, IDropdownDataCallBacks callBacks) {
        getClient().getDropdownDataForCompany(req).enqueue(new Callback<DropdownDataForCompanyRes>() {
            @Override
            public void onResponse(Call<DropdownDataForCompanyRes> call, Response<DropdownDataForCompanyRes> response) {
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful()) {
                    callBacks.onDropDownSuccess(response.body());
                } else callBacks.onErrorComplete("Something went wrong, Server Error");
            }

            @Override
            public void onFailure(Call<DropdownDataForCompanyRes> call, Throwable t) {
                Constants.logPrint(call.request().toString(), req, t.getMessage());
                callBacks.onError(t.getMessage());
            }
        });
       /* Gson gj = new Gson();
        DropdownDataForCompanyRes res = gj.fromJson(Constants.listme, DropdownDataForCompanyRes.class);
        callBacks.onDropDownSuccess(res);*/
    }

    public static void updateBranch(UpdateCompanyBranchesReq req, IBranchCallBacks callBacks) {
        getClient().updateCompanyBrancheDetails(req).enqueue(new Callback<UpdateCompanyBranchesRes>() {
            @Override
            public void onResponse(Call<UpdateCompanyBranchesRes> call, Response<UpdateCompanyBranchesRes> response) {
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful())
                    callBacks.onSuccessUpdateBranch(response.body());
                else callBacks.onError("Something went wrong,Server Error");
            }

            @Override
            public void onFailure(Call<UpdateCompanyBranchesRes> call, Throwable t) {
                Constants.logPrint(call.request().toString(), req, t.getMessage());
                callBacks.onError(t.getMessage());
            }
        });
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
                Constants.logPrint(call.request().toString(), req, t.getMessage());
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
                Constants.logPrint(call.request().toString(), req, t.getMessage());
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
                Constants.logPrint(call.request().toString(), req, t.getMessage());
                callBacks.onError(t.getMessage());
            }
        });
    }

    /*Users*/

    public static void addUser(AddUserForCompany req, IUserCallBacks callBacks) {
        getClient().addUserForCompany(req).enqueue(new Callback<AddUserForCompanyRes>() {
            @Override
            public void onResponse(Call<AddUserForCompanyRes> call, Response<AddUserForCompanyRes> response) {

                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful())
                    callBacks.addUserSuccess(response.body());
                else callBacks.onError("Something went wrong,Server Error");
            }

            @Override
            public void onFailure(Call<AddUserForCompanyRes> call, Throwable t) {
                Constants.logPrint(call.request().toString(), req, t.getMessage());
                callBacks.onError(t.getMessage());
            }
        });
    }

    public static void updateUser(UpdateUserDetails req, IUserCallBacks callBacks) {
        getClient().updateUserDetails(req).enqueue(new Callback<AddUserForCompanyRes>() {
            @Override
            public void onResponse(Call<AddUserForCompanyRes> call, Response<AddUserForCompanyRes> response) {
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful())
                    callBacks.updateUserSuccess(response.body());
                else callBacks.onError("Something went wrong,Server Error");
            }

            @Override
            public void onFailure(Call<AddUserForCompanyRes> call, Throwable t) {
                Constants.logPrint(call.request().toString(), req, t.getMessage());
                callBacks.onError(t.getMessage());
            }
        });
    }

    public static void getUsers(GetUserForCompany req, IUserCallBacks callBacks, IHomeFragCallbacks homeFragCallbacks) {
        getClient().getUserForCompany(req).enqueue(new Callback<GetUserForCompanyRes>() {
            @Override
            public void onResponse(Call<GetUserForCompanyRes> call, Response<GetUserForCompanyRes> response) {
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful()) {
                    if (callBacks != null) callBacks.getUsersSuccess(response.body());
                    if (homeFragCallbacks != null)
                        homeFragCallbacks.getUsersSuccess(response.body());
                } else {
                    if (callBacks != null) callBacks.onError("Something went wrong, Server Error");
                    if (homeFragCallbacks != null)
                        homeFragCallbacks.onError("Something went wrong, Server Error");
                }
            }

            @Override
            public void onFailure(Call<GetUserForCompanyRes> call, Throwable t) {
                Constants.logPrint(call.request().toString(), req, t.getMessage());
                if (callBacks != null) callBacks.onError(t.getMessage());
                if (homeFragCallbacks != null) homeFragCallbacks.onError(t.getMessage());
            }
        });
    }

    /*
     * Customer*/

    public static void addCusomer(AddCustomerReq req, ICustomerCallBacs callBacks) {
        getClient().addCustomer(req).enqueue(new Callback<AddCustomerRes>() {
            @Override
            public void onResponse(Call<AddCustomerRes> call, Response<AddCustomerRes> response) {

                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful())
                    if (response.body().success)
                        callBacks.addCustomerSuccess(response.body());
                    else callBacks.onErrorComplete(response.body().response);
                else callBacks.onErrorComplete("Something went wrong,Server Error");
            }

            @Override
            public void onFailure(Call<AddCustomerRes> call, Throwable t) {
                Constants.logPrint(call.request().toString(), req, t.getMessage());
                callBacks.onError(t.getMessage());
            }
        });
    }

    public static void updateCusomer(UpdateCustomerReq req, ICustomerCallBacs callBacks) {
        getClient().updateCustomerDetails(req).enqueue(new Callback<UpdateCustomerRes>() {
            @Override
            public void onResponse(Call<UpdateCustomerRes> call, Response<UpdateCustomerRes> response) {
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful())
                    callBacks.updateCustomerSuccess(response.body());
                else callBacks.onErrorComplete("Something went wrong,Server Error");
            }

            @Override
            public void onFailure(Call<UpdateCustomerRes> call, Throwable t) {
                Constants.logPrint(call.request().toString(), req, t.getMessage());
                callBacks.onError(t.getMessage());
            }
        });
    }

    public static void getCusomer(GetCustomerReq req, ICustomerCallBacs callBacks) {
        getClient().getCustomerDetails(req).enqueue(new Callback<GetCustomerRes>() {
            @Override
            public void onResponse(Call<GetCustomerRes> call, Response<GetCustomerRes> response) {
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful())
                    callBacks.getCustomerSuccess(response.body());
                else callBacks.onErrorComplete("Something went wrong, Server Error");
            }

            @Override
            public void onFailure(Call<GetCustomerRes> call, Throwable t) {
                Constants.logPrint(call.request().toString(), req, t.getMessage());
                callBacks.onError(t.getMessage());
            }
        });
    }

    /*
     * Transactions*/
    public static void getPTO(CustomerWithOTPReq req, ITransCallBacks callBacks) {

        /*CustomerWithOTPRes res = new CustomerWithOTPRes();
        res.success = true;
        res.id = 0;
        res.response = "Success";
        callBacks.onOtpSuccess(res);
        Constants.logPrint(null, req, res);*/
        getClient().validateCustomerWithOTP(req).enqueue(new Callback<CustomerWithOTPRes>() {
            @Override
            public void onResponse(Call<CustomerWithOTPRes> call, Response<CustomerWithOTPRes> response) {
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful())
                    if (response.body().success)
                        callBacks.onOtpSuccess(response.body());
                    else
                        callBacks.onErrorComplete(response.body().response);
                else callBacks.onErrorComplete("Something went wrong, Server Error");
            }

            @Override
            public void onFailure(Call<CustomerWithOTPRes> call, Throwable t) {
                Constants.logPrint(call.request().toString(), req, t.getMessage());
                callBacks.onError(t.getMessage());
            }
        });

    }
    /*
     * Customer Reg*/
    public static void getPTOCustomer(CustomerWithOTPReq req, ICustomerCallBacs callBacks) {

        getClient().validateCustomerWithOTP(req).enqueue(new Callback<CustomerWithOTPRes>() {
            @Override
            public void onResponse(Call<CustomerWithOTPRes> call, Response<CustomerWithOTPRes> response) {
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful())
                    if (response.body().success)
                        callBacks.onOtpSuccess(response.body());
                    else
                        callBacks.onErrorComplete(response.body().response);
                else callBacks.onErrorComplete("Something went wrong, Server Error");
            }

            @Override
            public void onFailure(Call<CustomerWithOTPRes> call, Throwable t) {
                Constants.logPrint(call.request().toString(), req, t.getMessage());
                callBacks.onError(t.getMessage());
            }
        });

    }

    public static void addTransaction(AddTransactionReq req, ITransCallBacks callBacks) {
        getClient().addTransaction(req).enqueue(new Callback<AddTransactionRes>() {
            @Override
            public void onResponse(Call<AddTransactionRes> call, Response<AddTransactionRes> response) {
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful())
                    if (response.body().success)
                        callBacks.onAddTransSuccess(response.body());
                    else
                        callBacks.onErrorComplete(response.body().response);
                else callBacks.onErrorComplete("Something went wrong, Server Error");
            }

            @Override
            public void onFailure(Call<AddTransactionRes> call, Throwable t) {
                Constants.logPrint(call.request().toString(), req, t.getMessage());
                callBacks.onError(t.getMessage());
            }
        });
    }

    public static void getTransaction(GetTransactionReq req, ITransCallBacks callTransBacks, IHomeFragCallbacks callHomebacks, IDailyClosureCallBacks callClosureBacks) {
        getClient().getTransactionForFilters(req).enqueue(new Callback<GetTransactionRes>() {
            @Override
            public void onResponse(Call<GetTransactionRes> call, Response<GetTransactionRes> response) {
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful()) {
                    if (callTransBacks != null) callTransBacks.onGetTransSuccess(response.body());
                    if (callHomebacks != null) callHomebacks.onGetTransSuccess(response.body());
                    if (callClosureBacks != null)
                        callClosureBacks.onGetTransSuccess(response.body());
                } else {
                    if (callTransBacks != null)
                        callTransBacks.onErrorComplete("Something went wrong, Server Error");
                    if (callHomebacks != null)
                        callHomebacks.onErrorComplete("Something went wrong, Server Error");
                    if (callClosureBacks != null)
                        callClosureBacks.onErrorComplete("Something went wrong, Server Error");
                }
            }

            @Override
            public void onFailure(Call<GetTransactionRes> call, Throwable t) {
                Constants.logPrint(call.request().toString(), req, t.getMessage());
                if (callTransBacks != null) callTransBacks.onError(t.getMessage());
                if (callHomebacks != null) callHomebacks.onError(t.getMessage());
                if (callClosureBacks != null) callClosureBacks.onError(t.getMessage());
            }
        });
    }

    /*Img For attachment
     * */
    public static void addRemoveCommonImage(AddRemoveCommonImageReq req, ITransCallBacks callTransBacks, ICustomerCallBacs callCustomerBacks) {
        getClient().addRemoveCommonImage(req).enqueue(new Callback<AddRemoveCommonImageRes>() {
            @Override
            public void onResponse(Call<AddRemoveCommonImageRes> call, Response<AddRemoveCommonImageRes> response) {
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful()) {
                    if (response.body().success) {
                        if (callTransBacks != null)
                            callTransBacks.onAddRemoveCommonImageSuccess(response.body());
                        if (callCustomerBacks != null)
                            callCustomerBacks.onAddRemoveCommonImageSuccess(response.body());
                    } else {
                        if (callTransBacks != null)
                            callTransBacks.onErrorComplete(response.body().response);
                        if (callCustomerBacks != null)
                            callCustomerBacks.onErrorComplete(response.body().response);
                    }
                } else {
                    if (callTransBacks != null)
                        callTransBacks.onErrorComplete("Something went wrong, Server Error");
                    if (callCustomerBacks != null)
                        callCustomerBacks.onErrorComplete("Something went wrong, Server Error");
                }
            }

            @Override
            public void onFailure(Call<AddRemoveCommonImageRes> call, Throwable t) {
                Constants.logPrint(call.request().toString(), req, t.getMessage());
                if (callTransBacks != null) callTransBacks.onError(t.getMessage());
                if (callCustomerBacks != null) callCustomerBacks.onError(t.getMessage());
            }
        });
    }


    /*UserDailyClosure
     * */
    public static void addDailyClosure(AddUserDailyClosureReq req, IDailyClosureCallBacks callBacks) {
        getClient().addUserDailyClosure(req).enqueue(new Callback<AddUserDailyClosureRes>() {
            @Override
            public void onResponse(Call<AddUserDailyClosureRes> call, Response<AddUserDailyClosureRes> response) {
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful())
                    if (response.body().success)
                        callBacks.onAddDailyClousureSuccess(response.body());
                    else
                        callBacks.onErrorComplete(response.body().response);
                else callBacks.onErrorComplete("Something went wrong, Server Error");
            }

            @Override
            public void onFailure(Call<AddUserDailyClosureRes> call, Throwable t) {
                Constants.logPrint(call.request().toString(), req, t.getMessage());
                callBacks.onError(t.getMessage());
            }
        });
    }

    public static void updateDailyClosures(UpdateUserDailyClosureReq req, IDailyClosureCallBacks callBacks) {
        getClient().updateUserDailyClosureDetails(req).enqueue(new Callback<UpdateUserDailyClosureRes>() {
            @Override
            public void onResponse(Call<UpdateUserDailyClosureRes> call, Response<UpdateUserDailyClosureRes> response) {
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful()) {
                    callBacks.onUpdateDailyClousureSuccess(response.body());
                } else {
                    callBacks.onErrorComplete("Something went wrong, Server Error");
                }
            }

            @Override
            public void onFailure(Call<UpdateUserDailyClosureRes> call, Throwable t) {
                Constants.logPrint(call.request().toString(), req, t.getMessage());
                callBacks.onError(t.getMessage());
            }
        });
    }

    public static void getDailyClosures(GetUserDailyClosureReq req, IDailyClosureCallBacks callBacks) {
        getClient().getUserDailyClosureForFilters(req).enqueue(new Callback<GetUserDailyClosureRes>() {
            @Override
            public void onResponse(Call<GetUserDailyClosureRes> call, Response<GetUserDailyClosureRes> response) {
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful()) {
                    callBacks.onGetDailyClosureSuccess(response.body());
                } else {
                    callBacks.onErrorComplete("Something went wrong, Server Error");
                }
            }

            @Override
            public void onFailure(Call<GetUserDailyClosureRes> call, Throwable t) {
                Constants.logPrint(call.request().toString(), req, t.getMessage());
                callBacks.onError(t.getMessage());
            }
        });
    }

    public static void getDailyClosures(GetUserDailyClosureReq req, IDailyClosureDashCallBacks callBacks) {
        getClient().getUserDailyClosureForFilters(req).enqueue(new Callback<GetUserDailyClosureRes>() {
            @Override
            public void onResponse(Call<GetUserDailyClosureRes> call, Response<GetUserDailyClosureRes> response) {
                Constants.logPrint(call.request().toString(), req, response.body());
                if (response.isSuccessful()) {
                    callBacks.onGetDailyClosureSuccess(response.body());
                } else {
                    callBacks.onErrorComplete("Something went wrong, Server Error");
                }
            }

            @Override
            public void onFailure(Call<GetUserDailyClosureRes> call, Throwable t) {
                Constants.logPrint(call.request().toString(), req, t.getMessage());
                callBacks.onError(t.getMessage());
            }
        });
    }
}

package com.a.goldtrack.network;

import com.a.goldtrack.Model.*;
import com.a.goldtrack.Model.AddTransactionReq;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIService {

    @POST("user/userLogin")
    Call<UserLoginRes> userLogin(@Body UserLoginReq signUpReq);

    /*Company
     * */
    @POST("company/addCompany")
    Call<AddCompanyRes> addCompany(@Body AddCompany getCompany);

    @POST("company/getCompany")
    Call<GetCompanyRes> getCompany(@Body GetCompany getCompany);

    @POST("company/updateCompanyDetails")
    Call<UpdateCompanyDetailsRes> updateCompanyDetails(@Body UpdateCompanyDetails updateCompanyDetails);


    @POST("company/getDropdownDataForCompany")
    Call<DropdownDataForCompanyRes> getDropdownDataForCompany(@Body GetCompany req);


    /*Users
     * */

    @POST("user/addUserForCompany")
    Call<AddUserForCompanyRes> addUserForCompany(@Body AddUserForCompany userForCompany);

    @POST("user/updateUserDetails")
    Call<AddUserForCompanyRes> updateUserDetails(@Body UpdateUserDetails userForCompany);

    @POST("user/getUserForCompany")
    Call<GetUserForCompanyRes> getUserForCompany(@Body GetUserForCompany userForCompany);


    /*Branches
     * */
    @POST("companybranches/getCompanyBranches")
    Call<GetCompanyBranchesRes> getCompanyBranches(@Body GetCompanyBranches getCompanyBranches);


    @POST("companybranches/addCompanyBranches")
    Call<AddCompanyBranchesRes> addCompanyBranches(@Body AddCompanyBranchesReq companyBranchesReq);

    @POST("companybranches/updateCompanyBrancheDetails")
    Call<UpdateCompanyBranchesRes> updateCompanyBrancheDetails(@Body UpdateCompanyBranchesReq companyBranchesReq);

    /*Items
     * */
    @POST("items/addItem")
    Call<AddItemRes> addItem(@Body AddItemReq companyBranchesReq);

    @POST("items/getItemsForCompany")
    Call<GetItemsRes> getItemsForCompany(@Body GetItemsReq companyBranchesReq);

    @POST("items/updateItemDetails")
    Call<UpdateItemRes> updateItemDetails(@Body UpdateItemReq companyBranchesReq);

    /*Customer
     * */
    @POST("customer/addCustomer")
    Call<AddCustomerRes> addCustomer(@Body AddCustomerReq addCustomerReq);

    @POST("customer/getCustomerDetails")
    Call<GetCustomerRes> getCustomerDetails(@Body GetCustomerReq companyBranchesReq);

    @POST("customer/updateCustomerDetails")
    Call<UpdateCustomerRes> updateCustomerDetails(@Body UpdateCustomerReq customerReq);

    /*Transaction
     * */
    @POST("transaction/validateCustomerWithOTP")
    Call<CustomerWithOTPRes> validateCustomerWithOTP(@Body CustomerWithOTPReq customerWithOTPReq);

    @POST("transaction/addTransaction")
    Call<AddTransactionRes> addTransaction(@Body AddTransactionReq req);

    @POST("transaction/getTransactionForFilters")
    Call<GetTransactionRes> getTransactionForFilters(@Body GetTransactionReq req);

    /* Img For attachment
     * */

    @POST("transaction/addRemoveCommonImage")
    Call<AddRemoveCommonImageRes> addRemoveCommonImage(@Body AddRemoveCommonImageReq req);

    /*UserDailyClosure
     * */
    @POST("user/addUserDailyClosure")
    Call<AddUserDailyClosureRes> addUserDailyClosure(@Body AddUserDailyClosureReq dailyClosureReq);

    @POST("user/updateUserDailyClosureDetails")
    Call<UpdateUserDailyClosureRes> updateUserDailyClosureDetails(@Body UpdateUserDailyClosureReq userDailyClosureReq);

    @POST("user/getUserDailyClosureForFilters")
    Call<GetUserDailyClosureRes> getUserDailyClosureForFilters(@Body GetUserDailyClosureReq getUserDailyClosureReq);

}

package com.a.goldtrack.network;

import com.a.goldtrack.Model.*;

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

    @POST("transaction/validateCustomerWithOTP")
    Call<CustomerWithOTPRes> validateCustomerWithOTP(@Body CustomerWithOTPReq customerWithOTPReq);

}

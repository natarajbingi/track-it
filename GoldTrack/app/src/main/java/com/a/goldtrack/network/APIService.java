package com.a.goldtrack.network;

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

package com.a.goldtrack.network;

import com.a.goldtrack.Model.*;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIService {

    @POST("teacher/teacherLogin")
    Call<TeacherLoginRes> TEACHER_LOGIN_RES_CALL(@Body TeacherLoginReq signUpReq);

    @POST("company/addCompany")
    Call<AddCompanyRes> addCompany(@Body AddCompany getCompany);

    @POST("company/getCompany")
    Call<GetCompanyRes> getCompany(@Body GetCompany getCompany);

    @POST("company/updateCompanyDetails")
    Call<UpdateCompanyDetailsRes> updateCompanyDetails(@Body UpdateCompanyDetails updateCompanyDetails);


    @POST("user/addUserForCompany")
    Call<AddUserForCompanyRes> addUserForCompany(@Body AddUserForCompany userForCompany);

    @POST("user/updateUserDetails")
    Call<AddUserForCompanyRes> updateUserDetails(@Body UpdateUserCompany userForCompany);

    @POST("company/addUserForCompany")
    Call<GetUserForCompanyRes> getUserForCompany(@Body GetUserForCompany userForCompany);


    @POST("user/addUserForCompany")
    Call<UpdateCompanyDetailsRes> addUserForCompany(@Body UpdateCompanyDetails updateCompanyDetails);


}

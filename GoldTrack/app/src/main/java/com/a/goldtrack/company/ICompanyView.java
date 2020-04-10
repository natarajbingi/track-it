package com.a.goldtrack.company;

import com.a.goldtrack.Model.GetCompanyRes;

public interface ICompanyView {

    void addCompanyDetailes();

    void updateCompanyDetailes();

    void PbSHow();

    void onSuccessGetCompany(GetCompanyRes model);

    void onErrorSpread(String msg);
}

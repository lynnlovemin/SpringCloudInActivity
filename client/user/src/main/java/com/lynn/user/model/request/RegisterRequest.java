package com.lynn.user.model.request;

import org.hibernate.validator.constraints.NotEmpty;

public class RegisterRequest {

    @NotEmpty
    private String mobile;

    @NotEmpty
    private String password;

    @NotEmpty
    private String capctha;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCapctha() {
        return capctha;
    }

    public void setCapctha(String capctha) {
        this.capctha = capctha;
    }
}

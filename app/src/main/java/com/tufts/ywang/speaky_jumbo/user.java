package com.tufts.ywang.speaky_jumbo;

/**
 * Created by wangy on 11/7/2017.
 */

public class user {

    public String password;
    public String lrn_language;
    public String nat_language;
    public String gender;
    public String contact;
    public String url;

    public user() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public user(String password, String url) {
        this.password = password;
        this.lrn_language = "English";
        this.nat_language = "English";
        this.gender = "Male";
        this.contact = "Null";
        this.url = url;
    }
}

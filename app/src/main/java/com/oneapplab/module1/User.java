package com.oneapplab.module1;

/**
 * Created by haider on 13-02-2017.
 */

public class User {


    public String uniqueID;
    public String buyItem;
    public String token;

    public User(String buyItem,String token) {

        this.buyItem=buyItem;
        this.token=token;

    }
    public User(){

    }

}

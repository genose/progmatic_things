package com.mkyong;

import jdk.jfr.Name;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;


@ManagedBean(name = "user")
@SessionScoped
public class UserBean implements Serializable{

    String bookmarkURL;

    public String getBookmarkURL() {
        return bookmarkURL;
    }

    public void setBookmarkURL(String bookmarkURL) {
        this.bookmarkURL = bookmarkURL;
    }

}

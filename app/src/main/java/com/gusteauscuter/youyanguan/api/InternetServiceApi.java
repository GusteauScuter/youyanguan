package com.gusteauscuter.youyanguan.api;

import org.json.JSONObject;

/**
 * Created by Z on 2016/4/22 0022.
 */
public interface InternetServiceApi {

    boolean Login(String username, String password);

    boolean RenewBook(String bookId);

    JSONObject getMyBookBorrowed();

    JSONObject SearchBook(String searchContent, String searchCriteria, int page);

    JSONObject GetStoreInfor(String bookId);

    JSONObject GetBookDetail(String bookId);

}

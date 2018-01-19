package com.madhouse.prepare_jee.helpers;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HEMANT on 19-01-2018.
 */

public class SearchChapters {
    private static final String TAG = "SearchChapters";
    private List<String> searchList;
    private List<String> resultList;

    public SearchChapters(List searchList) {
        this.searchList = searchList;
        resultList = new ArrayList<>();
    }

    public List searchInList(CharSequence charSequence) {
        Log.d(TAG, searchList + "searchInList: " + charSequence);
        if (charSequence == null || charSequence.length() == 0) {
            return searchList;
        } else {
            for (int i = 0; i < searchList.size(); i++) {
                String data = searchList.get(i);
                if (data.toLowerCase().contains(charSequence.toString())) {
                    resultList.add(searchList.get(i));
                }
            }
            Log.d(TAG, "searchInList: " + resultList);
            return resultList;
        }
    }
}

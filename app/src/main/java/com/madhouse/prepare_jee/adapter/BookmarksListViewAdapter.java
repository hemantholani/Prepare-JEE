package com.madhouse.prepare_jee.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.madhouse.prepare_jee.R;

import java.util.List;

/**
 * Created by HEMANT on 19-01-2018.
 */

public class BookmarksListViewAdapter extends BaseAdapter {
    private Context context;
    private List<String> bookmarksList;
    private List<String> subjectsList;
    private LayoutInflater layoutInflater;
    private Typeface poppins;

    public BookmarksListViewAdapter(Context context, List<String> bookmarksList, List<String> subjectsList) {
        this.context = context;
        this.bookmarksList = bookmarksList;
        this.subjectsList = subjectsList;
        layoutInflater = LayoutInflater.from(context);
        poppins = Typeface.createFromAsset(context.getAssets(), "fonts/poppins.ttf");

    }

    @Override
    public int getCount() {
        return bookmarksList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View listView;
        if (view == null) {
            listView = layoutInflater.inflate(R.layout.bookmarks_list_item, viewGroup, false);
            TextView chpText = listView.findViewById(R.id.chapterTitle);
            TextView subText = listView.findViewById(R.id.subjectTitle);
            chpText.setText(bookmarksList.get(i).toUpperCase());
            chpText.setTypeface(poppins);
            subText.setTypeface(poppins);
            if (subjectsList.get(i).toLowerCase().equals("maths")) {
                subText.setText("BIOLOGY");
                subText.setBackgroundColor(context.getResources().getColor(R.color.primary_maths));
            } else if (subjectsList.get(i).toLowerCase().equals("physics")) {
                subText.setText("PHYSICS");
                subText.setBackgroundColor(context.getResources().getColor(R.color.primary_physics));
            } else if (subjectsList.get(i).toLowerCase().equals("chemistry")) {
                subText.setText("CHEMISTRY");
                subText.setBackgroundColor(context.getResources().getColor(R.color.primary_chemistry));
            }
        } else {
            listView = view;
        }
        return listView;
    }
}

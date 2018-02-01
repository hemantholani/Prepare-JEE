package com.madhouse.prepare_jee.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.folioreader.util.FolioReader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.madhouse.prepare_jee.R;
import com.madhouse.prepare_jee.adapter.ChapterGridAdapter;
import com.madhouse.prepare_jee.adapter.ProgressDialogAdapter;
import com.madhouse.prepare_jee.helpers.SearchChapters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubjectFragment extends Fragment {

    private static final String TAG = "SubjectFragment";
    File pdfFile;
    private GridView gridView;
    private ChapterGridAdapter chapterGridAdapter;
    private Typeface poppins_bold, poppins;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private List<String> chapterList = new ArrayList<>();
    private String subject;
    private ProgressDialogAdapter progressDialogAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        subject = getArguments().getString("Subject");
        android.support.v7.widget.Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ImageButton search = toolbar.findViewById(R.id.search);
        ImageButton close = toolbar.findViewById(R.id.Cancel);
        final LinearLayout searchLayout = toolbar.findViewById(R.id.LLSearch);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                circleReveal(R.id.LLSearch, 1, true, true);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                circleReveal(R.id.LLSearch, 1, true, false);
            }
        });
        View rootView = inflater.inflate(R.layout.fragment_subject, container, false);
        gridView = rootView.findViewById(R.id.subject_grid);
        progressBar = rootView.findViewById(R.id.subject_progress_bar);
        if (subject.equals("physics")) {
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.primary_physics)));
        } else if (subject.equals("maths")) {
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.primary_maths)));
        } else if (subject.equals("chemistry")) {
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.primary_chemistry)));
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(subject + "_chapters");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded: dataSnapshot" + dataSnapshot);
                chapterList.add(dataSnapshot.getValue(String.class));
                progressBar.setVisibility(View.GONE);
                chapterGridAdapter.notifyDataSetChanged();
                Log.d(TAG, "onChildAdded: " + chapterList);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        chapterGridAdapter = new ChapterGridAdapter(getActivity(), chapterList, subject);
        gridView.setAdapter(chapterGridAdapter);
        EditText chapterSearch = toolbar.findViewById(R.id.txtSearch);
        chapterSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                SearchChapters searchChapters = new SearchChapters(chapterList);
                List<String> resultList = searchChapters.searchInList(charSequence);
                Log.d(TAG, "onTextChanged: " + chapterList);
                chapterGridAdapter = new ChapterGridAdapter(getActivity(), resultList, subject);
                gridView.setAdapter(chapterGridAdapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return rootView;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void circleReveal(int viewID, int posFromRight, boolean containsOverflow, final boolean isShow) {
        final View myView = getActivity().findViewById(viewID);

        int width = myView.getWidth();

        if (posFromRight > 0)

            if (containsOverflow)
                width -= getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);

        int cx = width;
        int cy = myView.getHeight() / 2;

        Animator anim;
        if (isShow)
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, (float) width);
        else
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, (float) width, 0);

        anim.setDuration((long) 220);

        // make the view invisible when the animation is done
        final LinearLayout titleLayout = getActivity().findViewById(R.id.LLTitle);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isShow) {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.INVISIBLE);
                    titleLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        // make the view visible and start the animation
        if (isShow) {
            titleLayout.setVisibility(View.GONE);
            myView.setVisibility(View.VISIBLE);
        }

        // start the animation
        anim.start();


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridView.setNumColumns(3);
        } else {
            gridView.setNumColumns(2);
        }
    }
}

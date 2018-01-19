package com.madhouse.prepare_jee.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.google.gson.Gson;
import com.madhouse.prepare_jee.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HEMANT on 19-01-2018.
 */

public class ChapterGridAdapter extends BaseAdapter {
    private static final String TAG = "ChapterGridAdapter";
    private Context context;
    private List<String> chapterList;
    private List<String> searchList;
    private ProgressDialogAdapter progressDialogAdapter;
    File pdfFile;
    private String subject;
    private Typeface poppins_bold;

    public ChapterGridAdapter(Context context, List<String> chapterList, String subject) {
        this.context = context;
        this.chapterList = chapterList;
        this.subject = subject;
        if (searchList == null) {
            searchList = new ArrayList<>(chapterList);
        }
//        poppins_bold = Typeface.createFromAsset(context.getAssets(), "fonts/poppins_bold.ttf");
    }

    @Override
    public int getCount() {
        return chapterList.size();
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
        View gridView;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            gridView = layoutInflater.inflate(R.layout.chapter_grid_layout, viewGroup, false);
            LinearLayout linearLayout = gridView.findViewById(R.id.chapter_background_layout);
            if (subject.equals("physics")) {
                linearLayout.setBackgroundResource(R.drawable.ripple_physics_grid);
            } else if (subject.equals("maths")) {
                linearLayout.setBackgroundResource(R.drawable.ripple_biology_grid);
            } else {
                linearLayout.setBackgroundResource(R.drawable.ripple_chem_grid);
            }
            final TextView textView = gridView.findViewById(R.id.chpName);
            textView.setText(chapterList.get(i).toUpperCase());
            Log.d(TAG, "getView: " + chapterList);
            Log.d(TAG, "getView: " + chapterList.get(i));
            //textView.setTypeface(poppins_bold);
            SharedPreferences sharedPreferences = context.getSharedPreferences("Bookmarks", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            List<String> bookmaksList = new ArrayList<>();
            bookmaksList = gson.fromJson(sharedPreferences.getString("bookmarksList", ""), ArrayList.class);
            List<String> subjectList = new ArrayList<>();
            subjectList = gson.fromJson(sharedPreferences.getString("subjectList", ""), ArrayList.class);

            final ImageButton imageButton = gridView.findViewById(R.id.bookmark_grid);
            imageButton.setTag(i);
            if (bookmaksList != null) {
                if (bookmaksList.contains(chapterList.get(i))) {
                    imageButton.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark_white));
                } else {
                    imageButton.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark_white_border));
                }
            }
            textView.setTag(i);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    Log.d(TAG, "onClick: ");
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = firebaseDatabase.getReference(subject + "_detailed_chapters/" + chapterList.get((Integer) view.getTag()).replace(" ", ""));
                    databaseReference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Log.d(TAG, "onChildAdded: " + dataSnapshot);
                            String fileURL = dataSnapshot.getValue(String.class);
                            Log.d(TAG, "onChildAdded: " + fileURL);
                            displayPDF(fileURL, chapterList.get((Integer) view.getTag()));
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
                }
            });
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("Bookmarks", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    Log.d(TAG, "onClick: " + view.getTag().toString());
                    List<String> bookmaksList = new ArrayList<>();
                    bookmaksList = gson.fromJson(sharedPreferences.getString("bookmarksList", ""), ArrayList.class);
                    List<String> subjectList = new ArrayList<>();
                    subjectList = gson.fromJson(sharedPreferences.getString("subjectList", ""), ArrayList.class);
                    if (bookmaksList == null || bookmaksList.isEmpty()) {
                        bookmaksList = new ArrayList<>();
                        subjectList = new ArrayList<>();
                    }
                    Log.d(TAG, "onClick: " + bookmaksList);
                    String chapterName = chapterList.get((Integer) view.getTag());
                    if (bookmaksList.contains(chapterName)) {
                        subjectList.remove(bookmaksList.indexOf(chapterName));
                        bookmaksList.remove(chapterName);
                        ImageButton imageButton1 = (ImageButton) view;
                        imageButton1.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark_white_border));
                    } else {
                        bookmaksList.add(chapterName);
                        subjectList.add(subject);
                        ImageButton imageButton1 = (ImageButton) view;
                        imageButton1.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark_white));
                    }
                    editor.putString("bookmarksList", gson.toJson(bookmaksList));
                    editor.putString("subjectList", gson.toJson(subjectList));
                    editor.commit();
                    Log.d(TAG, "onClick: " + bookmaksList);

                }
            });
        } else

        {
            gridView = view;
        }
        return gridView;
    }

    private void displayPDF(String fileURL, String fileName) {
        Log.d(TAG, "displayPDF: " + fileURL);
        new DownloadFile().execute(fileURL, fileName);
    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];
            String fileName = strings[1];
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialogAdapter = new ProgressDialogAdapter(context);
                    progressDialogAdapter.showDialog();
                }
            });
            pdfFile = new File(context.getFilesDir(), fileName);
            try {
                pdfFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            StorageReference islandRef = FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl);
            Log.d(TAG, "doInBackground: " + islandRef);
            Log.d(TAG, "onSuccess: " + pdfFile.length());
            islandRef.getFile(pdfFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onSuccess: " + pdfFile.length());

                    FolioReader folioReader = new FolioReader(context);
                    Log.d(TAG, "doInBackground: " + pdfFile.getAbsolutePath());
                    folioReader.openBook(pdfFile.getAbsolutePath());
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialogAdapter.hideDialog();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }                    // Handle any errors

            });
            return null;

        }
    }
}


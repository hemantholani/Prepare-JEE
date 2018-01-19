package com.madhouse.prepare_jee.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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
import com.madhouse.prepare_jee.adapter.BookmarksListViewAdapter;
import com.madhouse.prepare_jee.adapter.ProgressDialogAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookmarkActivity extends AppCompatActivity {
    private List<String> bookmaksList = new ArrayList<>();
    private List<String> subjectList = new ArrayList<>();
    private static final String TAG = "BookmarkActivity";
    private File pdfFile;
    private ProgressDialogAdapter progressDialogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Bookmarks", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        bookmaksList = gson.fromJson(sharedPreferences.getString("bookmarksList", ""), ArrayList.class);
        subjectList = gson.fromJson(sharedPreferences.getString("subjectList", ""), ArrayList.class);
        if (bookmaksList.isEmpty() || subjectList.isEmpty()) {

        } else {
            ListView listView = findViewById(R.id.bookmarksList);
            BookmarksListViewAdapter bookmarksListViewAdapter = new BookmarksListViewAdapter(getApplicationContext(), bookmaksList, subjectList);
            listView.setAdapter(bookmarksListViewAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    final String chapterName = bookmaksList.get(i);
                    DatabaseReference databaseReference = firebaseDatabase.getReference(subjectList.get(i) + "_detailed_chapters/" + bookmaksList.get(i).replace(" ", ""));
                    databaseReference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Log.d(TAG, "onChildAdded: " + dataSnapshot);
                            String fileURL = dataSnapshot.getValue(String.class);
                            Log.d(TAG, "onChildAdded: " + fileURL);
                            displayPDF(fileURL, chapterName);
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
        }
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialogAdapter = new ProgressDialogAdapter(BookmarkActivity.this);
                    progressDialogAdapter.showDialog();
                }
            });
            pdfFile = new File(getApplicationContext().getFilesDir(), fileName);
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

                    FolioReader folioReader = new FolioReader(getApplicationContext());
                    Log.d(TAG, "doInBackground: " + pdfFile.getAbsolutePath());
                    folioReader.openBook(pdfFile.getAbsolutePath());
                    runOnUiThread(new Runnable() {
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

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialogAdapter != null)
            progressDialogAdapter.hideDialog();
    }

    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

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
            progressBar.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.primary_physics)));
        } else if (subject.equals("maths")) {
            progressBar.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.primary_maths)));
        } else if (subject.equals("chemistry")) {
            progressBar.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.primary_chemistry)));
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
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                Log.d(TAG, "onItemClick: ");
                final String chapterName = chapterList.get(i);
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference(subject + "_detailed_chapters/" + chapterList.get(i).replace(" ", ""));
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

    private void displayPDF(String fileURL, String fileName) {
        Log.d(TAG, "displayPDF: " + fileURL);
        new DownloadFile().execute(fileURL, fileName);
    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];
            String fileName = strings[1];
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialogAdapter = new ProgressDialogAdapter(getActivity());
                    progressDialogAdapter.showDialog();
                }
            });
            pdfFile = new File(getContext().getFilesDir(), fileName.toLowerCase().replace(" ", "").replace("'", ""));
            try {
                pdfFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
           /* try {
                if (fileUrl != null) {
                    URL url = new URL(fileUrl);
                    Log.d(TAG, "doInBackground: file" + fileUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    FileOutputStream fileOutputStream = getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
                    int totalSize = urlConnection.getContentLength();
                    Log.d(TAG, "doInBackground: " + totalSize);
                    byte[] buffer = new byte[1024 * 1024];
                    int bufferLength;
                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, bufferLength);
                    }
                    fileOutputStream.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            StorageReference islandRef = FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl);
            Log.d(TAG, "doInBackground: " + islandRef);
            Log.d(TAG, "onSuccess: " + pdfFile.length());
            islandRef.getFile(pdfFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onSuccess: " + pdfFile.length());

                    FolioReader folioReader = new FolioReader(getActivity());
                    Log.d(TAG, "doInBackground: " + pdfFile.getAbsolutePath());
                    folioReader.openBook(pdfFile.getAbsolutePath());
                    getActivity().runOnUiThread(new Runnable() {
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

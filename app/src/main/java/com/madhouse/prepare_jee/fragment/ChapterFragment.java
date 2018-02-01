package com.madhouse.prepare_jee.fragment;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.madhouse.prepare_jee.R;

import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChapterFragment extends Fragment {


    private File pdfFile;
    private static final String TAG = "ChapterFragment";
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chapter, container, false);
        TextView title = getActivity().findViewById(R.id.subTitle);
        String fileName = getArguments().getString("FileName");
        progressBar = rootView.findViewById(R.id.chapterProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        String fileUrl = getArguments().getString("FileURL");
        Typeface poppins_bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/poppins_bold.ttf");
        title.setText(fileName);
        title.setTypeface(poppins_bold);
        title.setTextSize(18);
        Log.d(TAG, "onCreateView: " + fileName + "     " + fileUrl);
        final PDFView pdfView = rootView.findViewById(R.id.pdfViewer);
        pdfFile = new File(getContext().getFilesDir(), fileName.toLowerCase().replace(" ", "").replace("'", ""));
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
                Log.d(TAG, "onSuccess: " + pdfFile.getName());
                progressBar.setVisibility(View.GONE);
                pdfView.fromFile(pdfFile).load();
                pdfView.useBestQuality(true);
                pdfView.documentFitsView();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }                    // Handle any errors

        });
        return rootView;
    }

}

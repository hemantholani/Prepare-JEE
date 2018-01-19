package com.madhouse.prepare_jee.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.madhouse.prepare_jee.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ImageView shareButton, bookmarks;
    private TextView preTxt, pareTxt, byTxt;
    private TextView physicsTxt, physicsChps, bioTxt, bioChps, chemTxt, chemChps;
    private Typeface poppins, poppins_light, ench_celeb, poppins_bold, poppinsExtra_bold;
    private CardView physics, chem, bio;
    private boolean exit = false;
    private List<String> bookmaksList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shareButton = findViewById(R.id.share_button);
        bookmarks = findViewById(R.id.bookmark_button);
        preTxt = findViewById(R.id.txt_pre);
        pareTxt = findViewById(R.id.txt_pare);
        byTxt = findViewById(R.id.txt_by);
        physicsTxt = findViewById(R.id.txt_phy);
        physicsChps = findViewById(R.id.txt_phy_chp);
        bioTxt = findViewById(R.id.txt_maths);
        bioChps = findViewById(R.id.txt_maths_chp);
        chemTxt = findViewById(R.id.txt_chem);
        chemChps = findViewById(R.id.txt_chem_chp);
        physics = findViewById(R.id.physics_layout);
        chem = findViewById(R.id.chem_layout);
        bio = findViewById(R.id.biology_layout);
        poppins = Typeface.createFromAsset(getAssets(), "fonts/poppins.ttf");
        poppins_light = Typeface.createFromAsset(getAssets(), "fonts/poppins_light.ttf");
        poppins_bold = Typeface.createFromAsset(getAssets(), "fonts/poppins_bold.ttf");
        //poppinsExtra_bold = Typeface.createFromAsset(getAssets(), "fonts/poppins_extrabold.ttf");
        ench_celeb = Typeface.createFromAsset(getAssets(), "fonts/enchanting_celebrations.ttf");
        preTxt.setTypeface(poppins_light);
        pareTxt.setTypeface(poppins_bold);
        byTxt.setTypeface(ench_celeb);
        physicsTxt.setTypeface(poppins_bold);
        physicsChps.setTypeface(poppins_bold);
        bioTxt.setTypeface(poppins_bold);
        bioChps.setTypeface(poppins_bold);
        chemTxt.setTypeface(poppins_bold);
        chemChps.setTypeface(poppins_bold);
        physics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                intent.putExtra("Subject", "physics");
                intent.putExtra("Color", R.color.primary_physics);
                startActivity(intent);
                //overridePendingTransition(R.anim.enter_subject, R.anim.exit_subject);
            }
        });
        bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                intent.putExtra("Subject", "maths");
                intent.putExtra("Color", R.color.primary_maths);
                intent.putExtra("BookmarkCheck", false);
                startActivity(intent);
                //overridePendingTransition(R.anim.enter_subject, R.anim.exit_subject);
            }
        });

        chem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                intent.putExtra("Subject", "chemistry");
                intent.putExtra("Color", R.color.primary_chemistry);
                startActivity(intent);
                //overridePendingTransition(R.anim.enter_subject, R.anim.exit_subject);
            }
        });
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareMessage = "Check out this amazing app to score more in your medical exams!!";
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(share, "Share Prepare with everyone"));
            }
        });
        bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("Bookmarks", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                bookmaksList = new ArrayList<>();
                bookmaksList = gson.fromJson(sharedPreferences.getString("bookmarksList", ""), ArrayList.class);
                if (bookmaksList == null) {
                    Toast.makeText(MainActivity.this, "No Bookmarks Added", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onClick: " + bookmaksList);
                    Intent intent = new Intent(MainActivity.this, BookmarkActivity.class);
                    intent.putExtra("bookmarksList", (Serializable) bookmaksList);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.main_layout), "Press back again to exit", BaseTransientBottomBar.LENGTH_SHORT);
            snackbar.show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

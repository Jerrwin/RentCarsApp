package com.example.rentcars.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rentcars.R;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 2000;

    Animation topanim, bottomanim;
    ImageView logo;
    TextView aname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        topanim= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomanim= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        logo=findViewById(R.id.imageView);
        aname=findViewById(R.id.textView);

        logo.setAnimation(topanim);
        aname.setAnimation(bottomanim);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, Login.class);

            Pair[] pairs=new Pair[2];
            pairs[0]=new Pair<View,String>(logo,"logo_trans");
            pairs[1]=new Pair<View,String>(aname,"text_trans");

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
            startActivity(intent,options.toBundle());
            finish();

        }, SPLASH_SCREEN);


    }
}
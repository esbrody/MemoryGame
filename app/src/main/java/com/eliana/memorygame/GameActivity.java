package com.eliana.memorygame;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private int[][] levels = {{3, 2}, {4, 3}, {6, 4}};
    private int level;

    private LinearLayout mainLayout;
    private LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
    private LinearLayout.LayoutParams colParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);

    private int counter = 0;
    private  int rightGuesses = 0;

    private int[] images = {
            R.drawable.monster_one,
            R.drawable.monster_one,
            R.drawable.monster_two,
            R.drawable.monster_two,
            R.drawable.monster_three,
            R.drawable.monster_three
    };

    private int clickcounter = 0;
    private ImageView firstCard;

    private boolean inProccess;
    private long start, end;

    private Handler handler; //choose android OS


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        level = getIntent().getIntExtra(MainActivity.LEVEL, -1);
        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        handler = new Handler();
        Shuffle();

        init();

        int length = levels[level][0] * levels[level][1];
        images = new int[length];

        Field[] drawables = R.drawable.class.getFields();
        int counter = 0;
        for(Field f: drawables){
            if(f.getName().contains("monster_")){
                images[counter++] = getResources().getIdentifier(f.getName(), "drawable", getPackageName());
                images[counter++] = getResources().getIdentifier(f.getName(), "drawable", getPackageName());
            }
            if(counter == images.length){
                break;
            }
        }
        Shuffle();

        start = System.currentTimeMillis();


    }

    private void init() {

        for (int i = 0; i < levels[level][0]; i++) {

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(rowParams);

            for (int j = 0; j < levels[level][1]; j++) {

                ImageView card = new ImageView(this);
                card.setTag(counter++);
                card.setImageResource(R.drawable.back);
                card.setOnClickListener(this);
                card.setLayoutParams(colParams);
                row.addView(card);

            }

            mainLayout.addView(row);
        }

    }


    private void Shuffle(){
        Random random = new Random();
        int randomIndex ;
        int temp;
        for(int i = 0; i < images.length; i++){
            randomIndex = random.nextInt(images.length);
            temp = images[i];
            images[i] = images[randomIndex];
            images[randomIndex] = temp;
        }
    }


    @Override
    public void onClick(View v) {
        if(!inProccess) {

            int clickedCardTag = (int) v.getTag();
            final ImageView currentCard = (ImageView) v;
            currentCard.setImageResource(images[clickedCardTag]);

            clickcounter++;
            if (clickcounter % 2 == 0) {
                //even card
                inProccess = true;
                if (images[(int) firstCard.getTag()] == images[clickedCardTag]) {
                    //same cards
                    currentCard.setOnClickListener(null);// disabling the clicker;
                    rightGuesses++;
                    inProccess = false;
                    if (rightGuesses == images.length / 2) {
                        end = System.currentTimeMillis();
                        Toast.makeText(this, "end of game " + (end - start)/1000 + " seconds", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //not the same
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            firstCard.setOnClickListener(GameActivity.this);
                            firstCard.setImageResource(R.drawable.back);
                            currentCard.setImageResource(R.drawable.back);
                            inProccess = false;
                        }
                    }, 1000);

                }

                //evenCard
            } else {
                //oddCard
                firstCard = currentCard;
                firstCard.setOnClickListener(null);
            }
        }


    }
}


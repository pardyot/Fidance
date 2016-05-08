package com.example.pardyot.fidance;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * The main game is played in this activity. it contains a matrix of size gridXgrid. The game starts with the first player(Red) playing first and putting any of his finger on the red highlighted tile,
 * immediately the next blue color tile is highlighted which represents that of the second player(blue). The one who removes his fingers from the dance floor or holds the wrong tile looses.
 */
public class GameActivity extends AppCompatActivity {

    LinearLayout linearLayoutVertical;
    List<Button> buttonList = new ArrayList<>();
    List<Integer> priority = new ArrayList<>();
    List<Integer> blocksA = new ArrayList<>();
    List<Integer> blocksB = new ArrayList<>();
    boolean chanceA = true;
    Integer grid;
    boolean sound;
    Long downTime;
    Double distanceA = 0.0;
    Double distanceB = 0.0;
    MediaPlayer mp = new MediaPlayer();
    int current=0;
    private boolean gameOver = false;
    private View mDecorView;

    /**
     * The main function responsible for laying out all the layout on the screen.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        long seed = System.nanoTime();
        mp = MediaPlayer.create(this, R.raw.song);
        mDecorView = getWindow().getDecorView();
        grid = getIntent().getIntExtra("grid" , 4);
        sound = getIntent().getBooleanExtra("sound", true);
        if(sound) {
            mp.setLooping(true);
            mp.start();
        }
        int buttonHeight = getButtonHeight(grid);
        linearLayoutVertical = (LinearLayout) findViewById(R.id.game_linear_vertical);
        for (int i=0 ; i<grid*grid ; ++i) {
            priority.add(i);
        }
        Collections.shuffle(priority, new Random(seed));
        fillBlocks();
        for (int i=0; i<grid ; ++i) {
            LinearLayout linearLayoutHorizontal = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(dpToPx(5) , dpToPx(5), dpToPx(5), dpToPx(5));
            linearLayoutHorizontal.setLayoutParams(params);
            linearLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
            for (int j=0 ; j<grid ; ++j) {
                Button button = new Button(this);
                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        buttonHeight, 1.0f);
                button.setLayoutParams(buttonParams);
                button.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.frame), PorterDuff.Mode.MULTIPLY);
                linearLayoutHorizontal.addView(button);
                buttonList.add(button);
            }
            linearLayoutVertical.addView(linearLayoutHorizontal);
        }
        for (int i=0 ; i<buttonList.size() ; ++i) {
            final int finalI = i;
            buttonList.get(i).setOnTouchListener(new View.OnTouchListener() {
                Rect rect = null;
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int actionPeformed = event.getAction();
                    if(actionPeformed == MotionEvent.ACTION_DOWN) {
                        downTime = System.currentTimeMillis();
                        this.rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                        if (v == buttonList.get(priority.get(current))) {
                            int[] location = new int[2];
                            v.getLocationOnScreen(location);
//                            addDistances((v.getRight() + v.getLeft())/2, (v.getTop() + v.getBottom())/2 , event.getRawX() , event.getRawY());
                            addDistances(location[0], location[1], event.getRawX(), event.getRawY());
                            ++current;
                            nextChance();
                        } else {
                            gameOver(priority.get(current));
                        }
                    } else if(actionPeformed == MotionEvent.ACTION_MOVE) {
                            if(this.rect != null && !this.rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())){
                                gameOver(finalI);
                            }
                    } else if(actionPeformed == MotionEvent.ACTION_UP) {
                        gameOver(finalI);
                    }
                    return true;
                }
            });
        }

        downTime = System.currentTimeMillis();
        nextChance();
        Thread runnable = new Thread() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted() && !gameOver && current < grid*grid) {
                    if (System.currentTimeMillis() - downTime > 5000) {
                        gameOver(priority.get(current));
                        break;
                    } else {
                        try {
                            this.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        runnable.start();

    }

    /**
     * When there comes a situation where all the tiles has been pressed then we decide the winner based on the distance of the first press from the center of the tile and continuously
     * add them up to get the total distance of A and B and then decide based on this result. The one hacing greate distance value looses.
     * @param buttonX The x coordinate of the button
     * @param buttonY The y coordinate of the button
     * @param x The x coordinate of the press
     * @param y The y coordinate of the press
     */
    private void addDistances(float buttonX , float buttonY, float x, float y) {
        if(blocksA.contains(priority.get(current))) {
            distanceA += Math.sqrt((x - buttonX)*(x - buttonX) + (y - buttonY)*(y - buttonY));
        } else {
            distanceB += Math.sqrt((x - buttonX)*(x - buttonX) + (y - buttonY)*(y - buttonY));
        }

    }

    /**
     * This fills the blocksA and blocksB which tells about which player has to press which all tiles.
     */
    private void fillBlocks() {
        for (int i=0 ; i<priority.size() ; ++i) {
            if(i%2==0) {
                blocksA.add(priority.get(i));
            } else {
                blocksB.add(priority.get(i));
            }
        }
    }

    /**
     * This is called when any of the condition according to the rule are met.
     * @param num The current value which was being run which tells us from the priority List whether it was Player A or B.
     */
    private void gameOver(int num) {
        if(!gameOver) {
            Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            if(blocksA.contains(num)) {
                intent.putExtra("looser" , 1);
            } else {
                intent.putExtra("looser" , 2);
            }
            mp.stop();
            startActivity(intent);
            finish();
            gameOver = true;
        }
    }

    /**
     * This funciton gives the height of the button given the number of grids in the dance floor, so that the dance floor looks more consistent relative to the mobile screen.
     * @param grid The number of grids that the dance floor will have
     * @return
     */
    private int getButtonHeight(Integer grid) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        int width = size.x;
        return width/grid;
    }

    /**
     * This is called every time when any player presses his corresponding button.
     */
    private void nextChance(){
        if(current == grid*grid) {
            if(distanceA > distanceB) {
                gameOver(blocksA.get(0));
            } else {
                gameOver(blocksB.get(0));
            }
        } else {
            hightlightNext(buttonList.get(priority.get(current)));
        }
    }

    /**
     * Gives the corresponding value from dp to px.
     * @param dp
     * @return
     */
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


    /**
     * It highlights the next button that is to be pressed by the player, it is called from inside nextChance()
     * @param button The button which should be highlighted
     */
    private void hightlightNext(Button button){
        if(chanceA){
            button.getBackground().setColorFilter(ContextCompat.getColor(GameActivity.this, R.color.player_a_highlighted), PorterDuff.Mode.MULTIPLY);
        } else{
            button.getBackground().setColorFilter(ContextCompat.getColor(GameActivity.this, R.color.player_b_highlighted), PorterDuff.Mode.MULTIPLY);
        }
        chanceA = !chanceA;
    }

    /**
     * Responsible for fully immersive nature of this activity.
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

}

package com.balloon.project;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.balloon.project.utils.SoundHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tyrantgit.explosionfield.ExplosionField;

public class MainActivity extends AppCompatActivity implements Balloon.BalloonListener {

    private ExplosionField mExplosionField;

    private static final int MIN_ANIMATION_DURATION = 1000;
    private static final int MAX_ANIMATION_DURATION = 8000;

    private final Handler handler = new Handler();

    private ViewGroup mContentView;
    private int[] mBalloonColors = new int[2];
    private int mNextColor, mScreenWidth, mScreenHeight;

    private List<Balloon> mBalloons = new ArrayList<>();
    Button mGoButton;

    private final int mNumberofBalloons = 12;
    private boolean mPlaying;
    private boolean mGameStopped = true;
    private int  mRandomNumber;
    private TextView mNextNumberShow;
    private int mHitCount = 0, mMissCount = 0;

    private SoundHelper mSoundHelper;
    ImageView imageView[] = new ImageView[12];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mBalloonColors[0] = Color.argb(255, 255, 0, 0);
        mBalloonColors[1] = Color.argb(255, 0, 0, 255);
        imageView[0] = (ImageView) findViewById(R.id.ballon1);
        imageView[1] = (ImageView) findViewById(R.id.ballon2);
        imageView[2] = (ImageView) findViewById(R.id.ballon3);
        imageView[3] = (ImageView) findViewById(R.id.ballon4);
        imageView[4] = (ImageView) findViewById(R.id.ballon5);
        imageView[5] = (ImageView) findViewById(R.id.ballon6);
        imageView[6] = (ImageView) findViewById(R.id.ballon7);
        imageView[7] = (ImageView) findViewById(R.id.ballon8);
        imageView[8] = (ImageView) findViewById(R.id.ballon9);
        imageView[9] = (ImageView) findViewById(R.id.ballon10);
        imageView[10] = (ImageView) findViewById(R.id.ballon11);
        imageView[11] = (ImageView) findViewById(R.id.ballon12);

        mContentView = (ViewGroup) findViewById(R.id.activity_main);
        setToFullScreen();

        ViewTreeObserver viewTreeObserver = mContentView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mScreenHeight = mContentView.getHeight();
                    mScreenWidth = mContentView.getWidth();
                }
            });
        }

        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setToFullScreen();
            }
        });

        mGoButton = (Button) findViewById(R.id.go_button);
        mNextNumberShow = (TextView) findViewById(R.id.next_number);

        mSoundHelper = new SoundHelper(this);
        mSoundHelper.prepareMusicPlayer(this);
        mExplosionField = ExplosionField.attach2Window(this);


    }

    private void setToFullScreen() {

        ViewGroup rootLayout = (ViewGroup) findViewById(R.id.activity_main);

        rootLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        setToFullScreen();
    }

    private void startGame() {
        mNextNumberShow.setVisibility(View.VISIBLE);
        setToFullScreen();

        mHitCount = 0;
        mMissCount = 0;
        mGameStopped = false;
        startLevel();
        mSoundHelper.playMusic();
    }

    private void startLevel() {

        handler.removeCallbacks(UpdatesToUI);
        handler.postDelayed(UpdatesToUI, 5000);
        for (int i = 0; i < mNumberofBalloons; i++) {
            launchBalloon(i);
        }

        mPlaying = true;
        mGoButton.setText("Stop Game");
    }


    public void goButtonClickHandler(View view) {
        if (mPlaying) {
            gameOver();
        } else if (mGameStopped) {
            startGame();
        } else {
            startLevel();
        }
    }

    @Override
    public void popBalloon(Balloon balloon, boolean userTouch) {
        if (userTouch) {
            if (mRandomNumber == balloon.getId()) {
                mSoundHelper.playSound();
                mExplosionField.explode(balloon);
                mHitCount++;
                mContentView.removeView(balloon);
                mBalloons.remove(balloon);
                launchBalloon(balloon.getId());

            } else {
                mMissCount++;
                Toast.makeText(this, "Missed that one!", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void gameOver() {
        mSoundHelper.pauseMusic();
        Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show();

        for (Balloon balloon : mBalloons) {
            mContentView.removeView(balloon);
            balloon.setPopped(true);
        }

        handler.removeCallbacks(UpdatesToUI);
        mBalloons.clear();
        mPlaying = false;
        mGameStopped = true;
        mGoButton.setText("Start Game");
        mNextNumberShow.setVisibility(View.GONE);
        showDialog();

    }


    @Override
    protected void onStop() {
        super.onStop();
        gameOver();
    }


    private int getImageviewHeight(int i) {
        int[] locations = new int[2];
        imageView[i].getLocationOnScreen(locations);
        return locations[1];
    }

    private int getImageviewWidth(int i) {
        int[] locations = new int[2];
        imageView[i].getLocationOnScreen(locations);
        return locations[0];
    }


    private int getRandomNumber() {
        Random rand = new Random();
        return rand.nextInt(12);
    }

    private void launchBalloon(int x) {
        Balloon balloon = new Balloon(this, mBalloonColors[mNextColor], 200, x);
        mBalloons.add(balloon);

        if (mNextColor + 1 == mBalloonColors.length) {
            mNextColor = 0;
        } else {
            mNextColor++;
        }
        balloon.setX(getImageviewWidth(x));
        balloon.setY(getImageviewHeight(x) + balloon.getHeight());
        mContentView.addView(balloon);

        int duration = Math.max(MIN_ANIMATION_DURATION, MAX_ANIMATION_DURATION - (2 * 1000));
        balloon.releaseBalloon(mScreenHeight, duration, getImageviewHeight(x) + balloon.getHeight());

    }


    private Runnable UpdatesToUI = new Runnable() {
        public void run() {
            mRandomNumber = getRandomNumber();
            mNextNumberShow.setText(String.valueOf(mRandomNumber));
            handler.postDelayed(this, 5000);
            Toast.makeText(getApplicationContext(), "Next Number is :- " + mRandomNumber, Toast.LENGTH_SHORT).show();
        }
    };



    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            handler.removeCallbacks(UpdatesToUI);
        } catch (Exception e) {

        }
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;

        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);

        TextView mHitText = (TextView) dialog.findViewById(R.id.dialog_hit);
        TextView mMissText = (TextView) dialog.findViewById(R.id.dialog_missed);
        Button mButtonClose = (Button) dialog.findViewById(R.id.dialog_close);

        mHitText.setText(String.valueOf(mHitCount));
        mMissText.setText(String.valueOf(mMissCount));
        dialog.show();


        mButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }
}

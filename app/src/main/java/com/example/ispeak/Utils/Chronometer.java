/*
* Thomas M.
* Extended Pausing Feature by Elisa D.
 */
package com.example.ispeak.Utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class Chronometer extends AppCompatTextView {
    @SuppressWarnings("unused")
    private static final String TAG = "Chronometer";

    public interface OnChronometerTickListener {
        void onChronometerTick(Chronometer chronometer);
    }

    private long mBase;
    private long mPauseTime;
    private boolean mVisible;
    private boolean mStarted;
    private boolean mRunning;
    private boolean mPausing;
    private OnChronometerTickListener mOnChronometerTickListener;


    private static final int TICK_WHAT = 2;

    private long timeElapsed;

    public Chronometer(Context context) {
        this (context, null, 0);
    }

    public Chronometer(Context context, AttributeSet attrs) {
        this (context, attrs, 0);
    }

    public Chronometer(Context context, AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);

        init();
        mHandler = new Handler() {
            public void handleMessage(Message m) {
                if(mPausing) {
                    updatePauseTime(SystemClock.elapsedRealtime());
                } else if (mRunning) {
                    updateText(SystemClock.elapsedRealtime());
                    dispatchChronometerTick();
                }

                sendMessageDelayed(Message.obtain(this , TICK_WHAT),
                        100);
            }
        };
    }

    private void init() {
        mBase = SystemClock.elapsedRealtime();
        mPauseTime = 0;
        mPausing = false;
        updateText(mBase);
    }

    public void setBase(long base) {
        mBase = base;
        mPauseTime = 0;
        mPausing = false;
        dispatchChronometerTick();
        updateText(SystemClock.elapsedRealtime());
    }

    public long getBase() {
        return mBase;
    }

    public void setOnChronometerTickListener(
            OnChronometerTickListener listener) {
        mOnChronometerTickListener = listener;
    }

    public OnChronometerTickListener getOnChronometerTickListener() {
        return mOnChronometerTickListener;
    }

    public void start() {
        mStarted = true;
        updateRunning();
    }

    public void stop() {
        mStarted = false;
        updateRunning();
    }

    public void pause(){
        mPausing = true;
    }

    public void unpause(){
        mPausing = false;
    }


    public void setStarted(boolean started) {
        mStarted = started;
        updateRunning();
    }

    @Override
    protected void onDetachedFromWindow() {
        super .onDetachedFromWindow();
        mVisible = false;
        updateRunning();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super .onWindowVisibilityChanged(visibility);
        mVisible = visibility == VISIBLE;
        updateRunning();
    }

    private synchronized void updateBackground(long now){
        timeElapsed = now - mBase - mPauseTime;
    }

    private synchronized void updatePauseTime(long now) {
        mPauseTime = now - mBase - timeElapsed;
    }

    public synchronized void updateText(long now) {
        timeElapsed = now - mBase - mPauseTime;
        String text = Utils.formatAudioTimeToStringPresentation(timeElapsed);
        setText(text);
    }

    private void updateRunning() {
        boolean running = mVisible && mStarted;
        if (running != mRunning) {
            if (running) {
                updateText(SystemClock.elapsedRealtime());
                updateBackground(SystemClock.elapsedRealtime());
                dispatchChronometerTick();
                mHandler.sendMessageDelayed(Message.obtain(mHandler,
                        TICK_WHAT), 100);
            } else {
                mHandler.removeMessages(TICK_WHAT);
            }
            mRunning = running;
        }
    }

    private final Handler mHandler;

    void dispatchChronometerTick() {
        if (mOnChronometerTickListener != null) {
            mOnChronometerTickListener.onChronometerTick(this);
        }
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }

}
package ru.ddc.predictor.service;

import java.util.Timer;
import java.util.TimerTask;

public class Malfunction {
    private final Timer  timer;
    private int index;

    public Malfunction(int minutes, int index) {
        this.index = index;
        timer = new Timer();
        timer.schedule(new StopTask(), minutes * 60 * 1000L);
    }

    public Malfunction(int index) {
        this.index = index;
        timer =  new Timer();
    }

    public int getIndex() {
        return index;
    }

    class StopTask extends TimerTask {
        @Override
        public void run() {
            index = 0;
            timer.cancel();
        }
    }
}

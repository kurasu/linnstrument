package com.rogerlinndesign.modes;

import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.ControllerHost;
import com.rogerlinndesign.Color;
import com.rogerlinndesign.Display;
import com.rogerlinndesign.Mode;

public class DrumSequencerMode implements Mode
{

    private Clip mLauncherCursorClip;
    private final int WIDTH = 16;
    private final int HEIGHT = 8;

    @Override
    public void init(ControllerHost host)
    {
        mLauncherCursorClip = host.createLauncherCursorClip(WIDTH, HEIGHT);
        mLauncherCursorClip.addStepDataObserver(this::onStepData);
        mLauncherCursorClip.playingStep().addValueObserver(s -> mPlayingStep = s);
    }


    private void onStepData(int x, int y, int state)
    {
        mStepData[toIndex(x, y)] = (byte) state;
    }

    private int toIndex(int x, int y)
    {
        return x + y* WIDTH;
    }

    @Override
    public void exit()
    {

    }

    @Override
    public void show()
    {
        mLauncherCursorClip.scrollToKey(36); // HACK
    }

    @Override
    public void hide()
    {
    }

    @Override
    public void onTap(final int x, final int y, int velocity)
    {
        if (velocity > 0)
        {
            mLauncherCursorClip.toggleStep(x, 7-y, velocity);
        }
    }

    @Override
    public void paint(final Display display)
    {
        display.clear();

        for (int x = 0; x < WIDTH; x++)
        {
            for (int y = 0; y < HEIGHT; y++)
            {
                Color color = Color.OFF;

                final byte state = mStepData[toIndex(x, y)];

                if (state == 2) color = Color.WHITE;
                else if (state == 1) color = Color.YELLOW;
                else if (mPlayingStep == x) color = Color.RED;

                display.setColor(x, 7-y, color);
            }
        }
    }

    @Override
    public String getLabel()
    {
        return "Drum Sequencer";
    }

    final byte[] mStepData = new byte[WIDTH*HEIGHT];
    int mPlayingStep = -1;
}

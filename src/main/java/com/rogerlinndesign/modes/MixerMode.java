package com.rogerlinndesign.modes;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Track;
import com.rogerlinndesign.Color;
import com.rogerlinndesign.Display;

public class MixerMode extends AbstractTrackMode
{

    @Override
    public void init(ControllerHost host)
    {
        super.init(host);

        visitTracks((x, t) ->
        {
            t.exists().markInterested();
            t.color().markInterested();
            t.mute().markInterested();
            t.solo().markInterested();
            t.arm().markInterested();
            t.volume().markInterested();
            t.addVuMeterObserver(8, -1, false, v -> onTrackVu(x, v));
            t.color().addValueObserver((r,g,b) -> onTrackColor(x, r, g, b));
        });
    }

    private void onTrackColor(Integer x, float r, float g, float b)
    {
        mColor[x] = Color.closestFromRgb(r, g, b);
    }

    private void onTrackVu(int x, int v)
    {
        mVuMeters[x] = v;
    }

    @Override
    public void exit()
    {

    }

    @Override
    public void show()
    {
    }

    @Override
    public void hide()
    {
    }

    @Override
    public void onTap(final int x, final int y, int velocity)
    {
        final Track track = trackFromX(x);

        if (track.exists().get())
        {
            if (y == 5) track.mute().toggle();
            else if (y == 6) track.solo().toggle();
            else if (y == 7) track.selectInMixer();
        }
    }

    @Override
    public void onSlideY(int x, int y, int subY)
    {
        if (y < 6)
        {
            final Track track = trackFromX(x);

            if (track.exists().get())
            {
                int level = (4 - y) << 7 | (127 - subY);
                track.volume().set(level, 5 << 7);
            }
        }
    }

    @Override
    public void paint(final Display display)
    {
        display.clear();

        visitTracks((x, t) ->
        {
            if (t.exists().get())
            {
                final boolean mute = t.mute().get();
                final boolean solo = t.solo().get();
                display.setColor(x, 5, mute ? Color.WHITE : Color.ORANGE);
                display.setColor(x, 6, solo ? Color.WHITE : Color.YELLOW);
                display.setColor(x, 7, mColor[x]);

                for(int i=0; i<5; i++)
                {
                    Color color = Color.CYAN;

                    if (mVuMeters[x] >= 7)
                    {
                        color = Color.RED;
                    }
                    else if (mute)
                    {
                        color = Color.BLUE;
                    }

                    display.setColor(x, 4-i, mVuMeters[x] > i ? color : Color.BLACK);
                }
            }
        });
    }

    @Override
    public String getLabel()
    {
        return "Mixer";
    }

    private int[] mVuMeters = new int[MAIN_TRACKS + FX_TRACKS + 1];
    private Color[] mColor = new Color[MAIN_TRACKS + FX_TRACKS + 1];
}

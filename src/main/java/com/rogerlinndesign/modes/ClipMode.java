package com.rogerlinndesign.modes;

import com.bitwig.extension.controller.api.ClipLauncherSlotBank;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;
import com.rogerlinndesign.Color;
import com.rogerlinndesign.Display;
import com.rogerlinndesign.Mode;

public class ClipMode implements Mode
{

    private TrackBank mTrackBank;

    @Override
    public void init(ControllerHost host)
    {
        final int numTracks = 24;
        mTrackBank = host.createMainTrackBank(numTracks, 0, 8);

        for(int t=0; t<numTracks; t++)
        {
            final int trackIndex = t;
            final Track track = mTrackBank.getItemAt(t);
            final ClipLauncherSlotBank slotBank = track.clipLauncherSlotBank();
            slotBank.addHasContentObserver((slot, content) -> updateHasContent(trackIndex, slot, content));
            slotBank.addColorObserver((slot, r, g, b) -> updateClipColor(trackIndex, slot, r, g, b));
        }
    }

    private void updateClipColor(int trackIndex, int slotIndex, float R, float G, float B)
    {
        mColor[toIndex(trackIndex, slotIndex)] = Color.closestFromRgb(R, G, B);
    }

    private int toIndex(int trackIndex, int slotIndex)
    {
        return trackIndex + slotIndex * 25;
    }

    private void updateHasContent(int trackIndex, int slotIndex, boolean content)
    {
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

    }

    @Override
    public void paint(final Display display)
    {
        display.clear();

        for(int x=0; x<25; x++)
        {
            for(int s=0; s<8; s++)
            {
                final Color color = mColor[toIndex(x, s)];

                if (color != null)
                    display.setColor(x, s, color);
            }
        }
    }

    @Override
    public String getLabel()
    {
        return "Clip";
    }


    private Color[] mColor = new Color[25 * 8];
}

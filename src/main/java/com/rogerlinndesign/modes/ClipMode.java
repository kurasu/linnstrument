package com.rogerlinndesign.modes;

import com.bitwig.extension.controller.api.ClipLauncherSlot;
import com.bitwig.extension.controller.api.ClipLauncherSlotBank;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Track;
import com.rogerlinndesign.Color;
import com.rogerlinndesign.Display;

public class ClipMode extends AbstractTrackMode
{
    @Override
    public void init(ControllerHost host)
    {
        super.init(host);

        visitTracks((trackIndex, track) ->
        {
            final ClipLauncherSlotBank slotBank = track.clipLauncherSlotBank();
            slotBank.addHasContentObserver((slot, content) -> updateHasContent(trackIndex, slot, content));
            slotBank.addColorObserver((slot, r, g, b) -> updateClipColor(trackIndex, slot, r, g, b));
        });
    }

    @Override
    protected int getNumScenes()
    {
        return 8;
    }

    private void updateClipColor(int trackIndex, int slotIndex, float R, float G, float B)
    {
        mClipColors[toClipIndex(trackIndex, slotIndex)] = Color.closestFromRgb(R, G, B);
    }

    private int toClipIndex(int trackIndex, int slotIndex)
    {
        return trackIndex + slotIndex * getNumTracks();
    }

    private void updateHasContent(int trackIndex, int slotIndex, boolean hasContent)
    {
        mClipHasContent[toClipIndex(trackIndex, slotIndex)] = hasContent;
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

        final ClipLauncherSlot slot = track.clipLauncherSlotBank().getItemAt(y);
        slot.launch();
    }

    @Override
    public void paint(final Display display)
    {
        display.clear();

        for(int x=0; x<getNumTracks(); x++)
        {
            for(int s=0; s<getNumScenes(); s++)
            {
                final int clipIndex = toClipIndex(x, s);

                if (mClipHasContent[clipIndex])
                {
                    final Color color = mClipColors[clipIndex];

                    if (color != null)
                        display.setColor(x, s, color);
                }
            }
        }
    }

    @Override
    public String getLabel()
    {
        return "Clip";
    }

    private Color[] mClipColors = new Color[getNumTracks() * getNumScenes()];
    private boolean[] mClipHasContent = new boolean[getNumTracks() * getNumScenes()];
}

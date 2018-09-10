package com.rogerlinndesign.modes;

import com.bitwig.extension.controller.api.*;
import com.rogerlinndesign.Mode;

import java.util.function.BiConsumer;

public abstract class AbstractTrackMode implements Mode
{
    final static int MAIN_TRACKS = 20;
    final static int FX_TRACKS = 4;

    @Override
    public void init(ControllerHost host)
    {
        mMainTracks = host.createMainTrackBank(MAIN_TRACKS, 0, getNumScenes());
        mFXTracks = host.createEffectTrackBank(FX_TRACKS, getNumScenes());
        mMasterTrack = host.createMasterTrack(getNumScenes());
    }

    protected int getNumScenes()
    {
        return 0;
    }

    protected int getNumTracks()
    {
        return 25;
    }

    protected void visitTracks(BiConsumer<Integer, Track> trackConsumer)
    {
        for(int t = 0; t< MAIN_TRACKS; t++)
        {
            trackConsumer.accept(t, mMainTracks.getItemAt(t));
        }

        for(int t = 0; t< FX_TRACKS; t++)
        {
            trackConsumer.accept(MAIN_TRACKS + t, mFXTracks.getItemAt(t));
        }

        trackConsumer.accept(MAIN_TRACKS + FX_TRACKS, mMasterTrack);

    }

    protected Track trackFromX(int x)
    {
        if (x < MAIN_TRACKS)
        {
            return mMainTracks.getItemAt(x);
        }
        else if (x < MAIN_TRACKS + FX_TRACKS)
        {
            return mFXTracks.getItemAt(x - MAIN_TRACKS);
        }

        return mMasterTrack;
    }

    private MasterTrack mMasterTrack;
    private TrackBank mFXTracks;
    private TrackBank mMainTracks;
}

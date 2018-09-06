package com.rogerlinndesign;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.MidiOut;

public class Display
{
    void setColor(int x, int y, int color)
    {
        mTargetData[getIndex(x, y)] = (byte) color;
    }

    void setColor(int x, int y, Color color)
    {
        mTargetData[getIndex(x, y)] = color.get();
    }

    void flush(final MidiOut midiOut)
    {
        int lastSentY = -1;
        int lastSentX = -1;

        for(int x=0; x<=25; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                final int i = getIndex(x, y);

                if (mTargetData[i] != mHardwareData[i])
                {
                    if (x != lastSentX) {
                        midiOut.sendMidi(ShortMidiMessage.CONTROL_CHANGE, 20, x);
                        lastSentX = x;
                    }

                    if (y != lastSentY) {
                        midiOut.sendMidi(ShortMidiMessage.CONTROL_CHANGE, 21, y);
                    }

                    int color = mTargetData[i];
                    midiOut.sendMidi(ShortMidiMessage.CONTROL_CHANGE, 22, color);

                    mHardwareData[i] = mTargetData[i];
                }
            }
        }
    }

    public int getWidth()
    {
        return 26;
    }

    public int getHeight()
    {
        return 8;
    }

    private int getSize()
    {
        return getWidth() * getHeight();
    }

    private int getIndex(final int x, final int y)
    {
        return x + y*getWidth();
    }

    private byte[] mTargetData = new byte[getSize()];
    private byte[] mHardwareData = new byte[getSize()];
}

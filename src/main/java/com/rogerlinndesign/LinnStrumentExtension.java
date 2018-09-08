package com.rogerlinndesign;

import com.bitwig.extension.controller.api.*;
import com.bitwig.extension.controller.ControllerExtension;
import com.rogerlinndesign.modes.MixerMode;
import com.rogerlinndesign.modes.PaintMode;

import java.util.ArrayList;
import java.util.List;

public class LinnStrumentExtension extends ControllerExtension
{
    public LinnStrumentExtension(final LinnStrumentExtensionDefinition definition, final ControllerHost host)
    {
        super(definition, host);
    }

    @Override
    public void init()
    {
        final ControllerHost host = getHost();

        final MidiIn midiIn = host.getMidiInPort(0);
        final NoteInput noteInput = midiIn.createNoteInput("", "??????");
        midiIn.setMidiCallback(this::onMidi);
        noteInput.setShouldConsumeEvents(false);
        noteInput.setUseExpressiveMidi(true, 0, 24);

        mModes = new ArrayList<>();
        mModes.add(new MixerMode());
        mModes.add(new PaintMode());

        host.scheduleTask(this::initPhase1, 100);

        for (Mode mode : mModes)
        {
            mode.init(host);
        }
    }

    private void selectMode(Mode mode)
    {
        if (mode == mCurrentMode) return;

        if (mCurrentMode != null)
        {
            mCurrentMode.hide();
        }

        mCurrentMode = mode;

        if (mode != null)
        {
            mode.show();
        }
    }

    void initPhase1()
    {
        final MidiOut midiOut = getHost().getMidiOutPort(0);

        /*
        // Set up MPE mode: Zone 1 15 channels
        sendRPN(0, 6, 15<<7);

        // Set up MPE mode: Zone 2 off
        sendRPN(15, 6, 0);
        */

        setUserFirmwareMode(true);

        drawBitwigLogo();

        getHost().scheduleTask(() ->
        {
            selectMode(mModes.get(1));
        }, 2000);

        //getHost().scheduleTask(this::onTimer, 2000);
    }

    private void drawBitwigLogo()
    {
        final Color c = Color.ORANGE;
        final Display d = this.mDisplay;

        for (int x = 11; x <= 14; x++) d.setColor(x, 2, c);
        for (int x = 10; x <= 15; x++) d.setColor(x, 3, c);

        d.setColor(10, 4, c);
        d.setColor(11, 4, c);
        d.setColor(14, 4, c);
        d.setColor(15, 4, c);
    }

    private void onMidi(int status, int data1, int data2)
    {
        getHost().println(Integer.toHexString(status << 16 | data1 << 8 | data2) + " (" + data1 + "," + data2 + ")");

        int y = status & 0xf;
        int x = data1;

        if (data2 > 0)
        {
            if (mCurrentMode != null)
            {
                mCurrentMode.onTap(x - 1, 7 - y, data2);
            }
        }
    }

    private void setUserFirmwareMode(boolean b)
    {
        sendNRPN(0, 245, b ? 1 : 0);
    }

    int mCount;

    private void onTimer()
    {
        int color = 1 + (mCount % 10);
        int row = (mCount % 8);

        for (int y = 0; y < 8; y++)
        {
            mDisplay.setColor(-1, y, y == row ? color : 0);
        }

        for (int x = 0; x < 25; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                mDisplay.setColor(x, y, color);
            }
        }

        mCount++;

        getHost().scheduleTask(this::onTimer, 500);
    }

    void sendRPN(int channel, int rpn, int value)
    {
        final MidiOut midiOut = getHost().getMidiOutPort(0);

        final int status = 0xB0 | channel;

        final int rpnLSB = rpn & 0x7f;
        final int rpnMSB = rpn >> 7;
        final int valueLSB = value & 0x7f;
        final int valueMSB = value >> 7;

        midiOut.sendMidi(status, 101, rpnMSB);
        midiOut.sendMidi(status, 100, rpnLSB);
        midiOut.sendMidi(status, 6, valueMSB);
        midiOut.sendMidi(status, 38, valueLSB);
        midiOut.sendMidi(status, 101, 127);
        midiOut.sendMidi(status, 100, 127);
    }

    void sendNRPN(int channel, int nrpn, int value)
    {
        final MidiOut midiOut = getHost().getMidiOutPort(0);

        final int status = 0xB0 | channel;

        final int nrpnLSB = nrpn & 0x7f;
        final int nrpnMSB = nrpn >> 7;
        final int valueLSB = value & 0x7f;
        final int valueMSB = value >> 7;

        midiOut.sendMidi(status, 99, nrpnMSB);
        midiOut.sendMidi(status, 98, nrpnLSB);
        midiOut.sendMidi(status, 6, valueMSB);
        midiOut.sendMidi(status, 38, valueLSB);
        midiOut.sendMidi(status, 101, 127);
        midiOut.sendMidi(status, 100, 127);
    }

    @Override
    public void exit()
    {
        for (Mode mode : mModes)
        {
            mode.exit();
        }

        setUserFirmwareMode(false);
    }

    @Override
    public void flush()
    {
        if (mCurrentMode != null)
        {
            mCurrentMode.paint(mDisplay);
        }

        mDisplay.flush(getMidiOutPort(0));
    }

    private boolean mShouldSendInit = false;
    private boolean mDidRunInitTask = false;
    private int mPitchBendRange = 24;
    private Display mDisplay = new Display();
    private List<Mode> mModes;
    private Mode mCurrentMode;
}

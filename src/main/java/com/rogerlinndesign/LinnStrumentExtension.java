package com.rogerlinndesign;

import com.bitwig.extension.controller.api.*;
import com.bitwig.extension.controller.ControllerExtension;
import com.rogerlinndesign.modes.MixerMode;

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

        selectMode(mModes.get(0));

        host.scheduleTask(this::initPhase1, 100);
    }

    private void selectMode(Mode mode)
    {
        if (mode == mCurrentMode) return;

        if (mCurrentMode != null)
        {
            mCurrentMode.deselected();
        }

        mCurrentMode = mode;

        if (mode != null)
        {
            mode.selected();
        }
    }

    void initPhase1()
    {
        final MidiOut midiOut = getHost().getMidiOutPort(0);

        // Set up MPE mode: Zone 1 15 channels
        midiOut.sendMidi(0xB0, 101, 0); // Registered Parameter Number (RPN) - MSB*
        midiOut.sendMidi(0xB0, 100, 6); // Registered Parameter Number (RPN) - LSB*
        midiOut.sendMidi(0xB0, 6, 15);
        midiOut.sendMidi(0xB0, 38, 0);

        // Set up MPE mode: Zone 2 off
        midiOut.sendMidi(0xBF, 101, 0);
        midiOut.sendMidi(0xBF, 100, 6);
        midiOut.sendMidi(0xBF, 6, 0);

        setUserFirmwareMode(true);

        drawBitwigLogo();

        getHost().scheduleTask(this::onTimer, 2000);
    }

    private void drawBitwigLogo()
    {
        final Color c = Color.ORANGE;
        final Display d = this.mDisplay;

        for(int x=10;x<=13; x++) d.setColor(x, 2, c);
        for(int x=9;x<=14; x++) d.setColor(x, 3, c);

        d.setColor(9, 4, c);
        d.setColor(10, 4, c);
        d.setColor(13, 4, c);
        d.setColor(14, 4, c);
    }

    private void onMidi(int status, int data1, int data2)
    {
        getHost().println(Integer.toHexString(status << 16 | data1 << 8 | data2) + " (" + data1 + "," + data2 + ")");
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
            mDisplay.setColor(0, y, y == row ? color : 0);
        }

        for(int x=1; x<=25; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                mDisplay.setColor(x,y, color);
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
        //setUserFirmwareMode(false);
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

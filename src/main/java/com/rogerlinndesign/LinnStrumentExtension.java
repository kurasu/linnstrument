package com.rogerlinndesign;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.callback.ShortMidiMessageReceivedCallback;
import com.bitwig.extension.controller.api.*;
import com.bitwig.extension.controller.ControllerExtension;

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


      /*final String[] yesNo = {"Yes", "No"};
      final SettableEnumValue shouldSendInit =
              host.getPreferences().getEnumSetting("Send initialization messages", "MPE", yesNo, "Yes");

      shouldSendInit.addValueObserver(newValue ->
      {
         mShouldSendInit = newValue.equalsIgnoreCase("Yes");

         if (mShouldSendInit && mDidRunInitTask)
         {
            sendInitializationMessages();
            sendPitchbendRange(mPitchBendRange);
         }
      });

      final SettableRangedValue bendRange =
              host.getPreferences().getNumberSetting("Pitch Bend Range", "MPE", 1, 96, 1, "", 24);

      bendRange.addRawValueObserver(range ->
      {
         mPitchBendRange = (int)range;
         noteInput.setUseExpressiveMidi(true, 0, mPitchBendRange);

         if (mShouldSendInit && mDidRunInitTask)
         {
            sendPitchbendRange(mPitchBendRange);
         }
      });

      host.scheduleTask(() ->
      {
         mDidRunInitTask = true;

         if (mShouldSendInit)
         {
            sendInitializationMessages();
         }
      }, 2000);*/

        host.scheduleTask(this::sendInitializationMessages, 100);

        host.scheduleTask(this::onTimer, 200);
    }

    void sendInitializationMessages()
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

    void sendPitchbendRange(int range)
    {
        final MidiOut midiOut = getHost().getMidiOutPort(0);

        // Set up Pitch bend range
        midiOut.sendMidi(0xB0, 101, 0); // Registered Parameter Number (RPN) - MSB*
        midiOut.sendMidi(0xB0, 100, 0); // Registered Parameter Number (RPN) - LSB*
        midiOut.sendMidi(0xB0, 6, range);
        midiOut.sendMidi(0xB0, 38, 0);
    }

    @Override
    public void exit()
    {
        setUserFirmwareMode(false);
    }

    @Override
    public void flush()
    {
        mDisplay.flush(getMidiOutPort(0));
    }

    private boolean mShouldSendInit = false;
    private boolean mDidRunInitTask = false;
    private int mPitchBendRange = 24;
    private Display mDisplay = new Display();
}

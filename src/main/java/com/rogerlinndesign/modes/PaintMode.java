package com.rogerlinndesign.modes;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.rogerlinndesign.Color;
import com.rogerlinndesign.Display;
import com.rogerlinndesign.Mode;

import java.util.Arrays;

public class PaintMode implements Mode
{

    private SettableStringValue mDocumentData;

    @Override
    public void init(ControllerHost host)
    {
        mDocumentData = host.getDocumentState().getStringSetting("image", "dummy", HEIGHT * WIDTH, "");
        mDocumentData.addValueObserver(this::documentDataChanged);
    }

    private void documentDataChanged(String s)
    {
        Arrays.fill(mData, (byte)0);

        for(int i=0; i<s.length(); i++)
        {
            final char c = s.charAt(i);
            final byte value = (byte) Character.digit(c, 16);
            mData[i] = value;
        }
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
    public void onTap(int x, int y, int velocity)
    {
        if (x == 0)
        {
            mColor = colorPalette(y).get();
        }
        else
        {
            final int index = getIndex(x, y);
            mData[index] = mColor;

            updateDocumentState();
        }
    }

    private void updateDocumentState()
    {
        StringBuilder sb = new StringBuilder(WIDTH * HEIGHT);

        for(int i=0; i<mData.length; i++)
        {
            sb.append(Character.forDigit(mData[i], 16));
        }

        mDocumentData.set(sb.toString());
    }

    @Override
    public void paint(Display display)
    {
        for (int y = 0; y < 8; y++)
        {
            display.setColor(0, y, colorPalette(y));
        }

        for (int x = 1; x < 25; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                display.setColor(x, y, mData[getIndex(x, y)]);
            }
        }
    }

    private Color colorPalette(int index)
    {
        final Color[] PALETTE = {
                Color.BLACK, Color.RED, Color.YELLOW, Color.GREEN,
                Color.CYAN, Color.BLUE, Color.MAGENTA, Color.WHITE};

        return PALETTE[index];
    }

    private int getIndex(int x, int y)
    {
        return y * WIDTH + x;
    }

    final static int WIDTH = 25;
    final static int HEIGHT = 8;

    byte mColor = 1;
    byte[] mData = new byte[25 * HEIGHT];
}

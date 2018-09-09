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
        else if (isSplit() && x == 24)
        {
            mColorR = colorPalette(y).get();
        }
        else
        {
            final int index = getIndex(x, y);
            mData[index] = x > 12 ? mColorR : mColor;

            updateDocumentState();
        }
    }

    private boolean isSplit()
    {
        return false;
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

            if (isSplit())
            {
                display.setColor(24, y, colorPalette(y));
            }
        }

        final int N = isSplit() ? 24 : 25;

        for (int x = 1; x < N; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                display.setColor(x, y, mData[getIndex(x, y)]);
            }
        }
    }

    @Override
    public String getLabel()
    {
        return "Paint";
    }

    private Color colorPalette(int index)
    {
        final Color[] PALETTE = {
                Color.BLACK, Color.RED, Color.LIME, Color.PINK,
                Color.CYAN, Color.BLUE, Color.MAGENTA, Color.ORANGE};

        return PALETTE[index];
    }

    private int getIndex(int x, int y)
    {
        return y * WIDTH + x;
    }

    final static int WIDTH = 25;
    final static int HEIGHT = 8;

    byte mColor = 1;
    byte mColorR = 1;
    byte[] mData = new byte[WIDTH * HEIGHT];
}

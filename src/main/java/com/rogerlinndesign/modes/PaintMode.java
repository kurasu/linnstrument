package com.rogerlinndesign.modes;

import com.rogerlinndesign.Display;
import com.rogerlinndesign.Mode;

public class PaintMode implements Mode
{
    @Override
    public void selected()
    {

    }

    @Override
    public void deselected()
    {

    }

    @Override
    public void onTap(int x, int y)
    {
        if (x == 0)
        {
            mColor = (byte) y;
        }
        else
        {
            final int index = getIndex(x, y);
            mData[index] = mColor;
        }
    }

    @Override
    public void paint(Display display)
    {
        for(int y=0; y<8; y++)
        {
            display.setColor(0, y, y);
        }

        for(int x=1; x<25; x++)
        {
            for(int y=0; y<8; y++)
            {
                display.setColor(x, y, mData[getIndex(x, y)]);
            }
        }
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

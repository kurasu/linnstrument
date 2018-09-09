package com.rogerlinndesign;

import com.bitwig.extension.controller.api.ControllerHost;

public interface Mode
{
    /**
     * called by extension init
     **/
    void init(ControllerHost host);

    /**
     * called by extension exit
     **/
    void exit();

    /**
     * called when mode becomes active
     **/
    void show();

    /**
     * called when mode was active but another ones gets selected
     **/
    void hide();

    void onTap(final int x, final int y, int velocity);

    default void onSlideY(final int x, final int y, int subY)
    {

    }

    void paint(final Display display);

    String getLabel();

    default Color getModeButtonColor()
    {
        return Color.GREEN;
    }
}

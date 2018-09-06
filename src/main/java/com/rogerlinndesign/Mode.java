package com.rogerlinndesign;

public interface Mode
{
    void selected();

    void deselected();

    void onTap(final int x, final int y);

    void paint(final Display display);
}

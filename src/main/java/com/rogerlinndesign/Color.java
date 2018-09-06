package com.rogerlinndesign;

public enum Color
{
    OFF,
    RED,
    YELLOW,
    GREEN,
    CYAN,
    BLUE,
    MAGENTA,
    BLACK,
    WHITE,
    ORANGE,
    LIME,
    PINK;

    byte get()
    {
        return (byte) this.ordinal();
    }

    Color fromRGB1Bit(final int rgb)
    {
        switch (rgb)
        {
            case 0b000:
                return BLACK;
            case 0b111:
                return WHITE;
            case 0b100:
                return RED;
            case 0b010:
                return GREEN;
            case 0b001:
                return BLUE;
            case 0b110:
                return YELLOW;
            case 0b011:
                return CYAN;
            case 0b101:
                return MAGENTA;
        }

        return OFF;
    }
}

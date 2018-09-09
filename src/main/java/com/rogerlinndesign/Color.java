package com.rogerlinndesign;

import com.sun.org.apache.regexp.internal.RE;

import java.util.ArrayList;
import java.util.List;

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

    public byte get()
    {
        return (byte) this.ordinal();
    }

    public Color fromRGB1Bit(final int rgb)
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

    static class ColorMatch
    {
        ColorMatch(final int R, final int G, final int B, final Color color)
        {
            mR = R / 255.0f;
            mG = G / 255.0f;
            mB = B / 255.0f;
            mColor = color;
        }

        private final Color mColor;
        private final float mR, mG, mB;

        public Color getColor()
        {
            return mColor;
        }

        public float distance(final float R, final float G, final float B)
        {
            float dR = R - mR;
            float dG = G - mG;
            float dB = B - mB;
            return dR*dR + dG*dG + dB*dB;
        }
    }

    public static Color closestFromRgb(final float R, final float G, final float B)
    {
        Color color = BLACK;
        float minDistance = Float.MAX_VALUE;

        for (ColorMatch matcher : MATCHERS)
        {
            final float distance = matcher.distance(R, G, B);

            if (distance < minDistance)
            {
                color = matcher.getColor();
                minDistance = distance;
            }
        }

        return color;
    }

    private static List<ColorMatch> MATCHERS = new ArrayList<>();

    private static void initColorMatch(int R, int G, int B, Color color)
    {
        MATCHERS.add(new ColorMatch(R,G,B,color));
    }

    static
    {
        initColorMatch(84, 84, 84, WHITE); // DARK_GREY
        initColorMatch(122, 122, 122, WHITE); // MID_GREY
        initColorMatch(201, 201, 201, WHITE); // LIGHT_GREY
        initColorMatch(134, 137, 172, WHITE); // METAL_GREY
        initColorMatch(87, 97, 198, BLUE); // SUN_BLUE
        initColorMatch(32, 138, 224, BLUE); // LIGHT_SUN_BLUE
        initColorMatch(163, 121, 67, ORANGE); // BROWN
        initColorMatch(198, 159, 112, ORANGE); // LIGHT_BROWN
        initColorMatch(217, 46, 36, RED); // RED
        initColorMatch(255, 87, 6, ORANGE); // ORANGE
        initColorMatch(217, 157, 16, YELLOW); // YELLOW
        initColorMatch(115, 152, 20, GREEN); // PUKE_GREEN
        initColorMatch(0, 157, 71, CYAN); // BLUE_GREEN
        initColorMatch(236, 97, 87, PINK); // LIGHT_RED
        initColorMatch(255, 131, 62, ORANGE); // LIGHT_ORANGE
        initColorMatch(228, 183, 78, LIME); // LIGHT_YELLOW
        initColorMatch(160, 192, 76, GREEN); // LIGHT_PUKE_GREEN
        initColorMatch(62, 187, 98, CYAN); // LIGHT_BLUE_GREEN
        initColorMatch(0, 166, 148, CYAN); // PETROL
        initColorMatch(0, 153, 217, BLUE); // SKY_BLUE
        initColorMatch(149, 73, 203, MAGENTA); // VIOLET
        initColorMatch(217, 56, 113, MAGENTA); // MAGENTA
        initColorMatch(67, 210, 185, CYAN); // LIGHT_PETROL
        initColorMatch(68, 200, 255, BLUE); // LIGHT_SKY_BLUE
        initColorMatch(188, 118, 240, MAGENTA); // LIGHT_VIOLET
        initColorMatch(225, 102, 145, PINK); // LIGHT_MAGENTA
    }

}

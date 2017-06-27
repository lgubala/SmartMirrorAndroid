package com.pejko.portal.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

public class FontUtils
{
    static final String REGULAR = "Regular";
    static final String LIGHT = "Light";
    static final String BOLD = "Bold";
    static final String THIN = "Thin";
    static final String ITALIC = "Italic";

    /**
     * Map of font types to font paths in assets
     */
    static Map<String, String> fontMap = new HashMap<>();

    static {
        fontMap.put(REGULAR, "fonts/Roboto-Regular.ttf");
        fontMap.put(LIGHT, "fonts/Roboto-Light.ttf");
        fontMap.put(BOLD, "fonts/Roboto-Bold.ttf");
        fontMap.put(THIN, "fonts/Roboto-Thin.ttf");
        fontMap.put(ITALIC, "fonts/Roboto-Italic.ttf");
    }

    /* cache for loaded typefaces */
    private static Map<String, Typeface> typefaceCache = new HashMap<>();

    /**
     * Creates typeface and puts it into cache
     */
    private static Typeface getRobotoTypeface(Context context, String fontType)
    {
        String fontPath = fontMap.get(fontType);
        if (!typefaceCache.containsKey(fontType)) {
            typefaceCache.put(fontType, Typeface.createFromAsset(context.getAssets(), fontPath));
        }
        return typefaceCache.get(fontType);
    }

    public static Typeface getLightTypeface(Context context)
    {
        return getRobotoTypeface(context, THIN);
    }

    public static Typeface getItalicTypeface(Context context)
    {
        return getRobotoTypeface(context, ITALIC);
    }

    public static Typeface getRegularTypeface(Context context)
    {
        return getRobotoTypeface(context, REGULAR);
    }

    public static Typeface getDefaultTypeface(Context context)
    {
        return getRobotoTypeface(context, LIGHT);
    }

    public static Typeface getBoldTypeface(Context context)
    {
        return getRobotoTypeface(context, BOLD);
    }
}

// eof

package com.cooper.wheellog.utils;

import android.content.Context;
import android.util.TypedValue;

public class MathsUtil {
    public static double kmToMiles(double km) {
        return km * 0.62137119;
    }

    public static float kmToMiles(float km) {
        return km * 0.62137119F;
    }

    public static int dpToPx(Context context, int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()));
    }

    public static int byteArrayInt2(byte low, byte high) {
        return (low & 255) + ((high & 255) * 256);
    }

    public static  long byteArrayInt4(byte value1, byte value2, byte value3, byte value4) {
        return (((((long) ((value1 & 255) << 16))) | ((long) ((value2 & 255) << 24))) | ((long) (value3 & 255))) | ((long) ((value4 & 255) << 8));
    }

    public static int intFromBytes(byte[] bytes, int starting) {
        if (bytes.length >= starting + 4) {
            return (((((((bytes[starting + 3] & 255)) << 8) | (bytes[starting + 2] & 255)) << 8) | (bytes[starting + 1] & 255)) << 8) | (bytes[starting] & 255);
        }
        return 0;
    }

    public static long longFromBytes(byte[] bytes, int starting) {
        if (bytes.length >= starting + 8) {
            return ((((((((((((((((long) (bytes[starting + 7] & 255))) << 8) | ((long) (bytes[starting + 6] & 255))) << 8) | ((long) (bytes[starting + 5] & 255))) << 8) | ((long) (bytes[starting + 4] & 255))) << 8) | ((long) (bytes[starting + 3] & 255))) << 8) | ((long) (bytes[starting + 2] & 255))) << 8) | ((long) (bytes[starting + 1] & 255))) << 8) | ((long) (bytes[starting] & 255));
        }
        return 0;
    }

    public static long signedIntFromBytes(byte[] bytes, int starting) {
        if (bytes.length >= starting + 4) {
            return (((((((bytes[starting + 3] & 255)) << 8) | (bytes[starting + 2] & 255)) << 8) | (bytes[starting + 1] & 255)) << 8) | (bytes[starting] & 255);
        }
        return 0;
    }
}

package <!PACKAGE_NAME>;

import android.content.Context;

public class drawables {
    public static final int MIN_DPI = 10;
    public static final int LDPI = 120;
    public static final int MDPI = 160;
    public static final int HDPI = 240;
    public static final int XHDPI = 320;
    public static final int XXHDPI = 400;
    public static final int MAX_DPI = 1000;
    public static final int[] DPI_LIST = new int[] {MIN_DPI, LDPI, MDPI, HDPI, XHDPI, XXHDPI, MAX_DPI};
    
    public static Object get(Context context, int id) {
        int density = findBestDpiOrder((int) (context.getResources().getDisplayMetrics().density));
        
        Object drawable = null;
        int dpiOrder = findBestDpiOrder(density);
        
        //first search higher dpi resrouces
        int currentOrder = dpiOrder;
        while (DPI_LIST[currentOrder] < MAX_DPI) {
            switch (DPI_LIST[currentOrder]) {
            case LDPI:
                drawable = getLDpi(context, id);
                break;
            case MDPI:
                drawable = getMDpi(context, id);
                break;
            case HDPI:
                drawable = getHDpi(context, id);
                break;
            case XHDPI:
                drawable = getXHDpi(context, id);
                break;
            case XXHDPI:
                drawable = getXXHDpi(context, id);
                break;
            default:
                break;
            }
            if (drawable != null) {
                return drawable;
            }
            currentOrder += 1;
        }

        //then search lower dpi resrouces
        currentOrder = dpiOrder - 1;
        while (DPI_LIST[currentOrder] > MIN_DPI) {
            switch (DPI_LIST[currentOrder]) {
            case LDPI:
                drawable = getLDpi(context, id);
                break;
            case MDPI:
                drawable = getMDpi(context, id);
                break;
            case HDPI:
                drawable = getHDpi(context, id);
                break;
            case XHDPI:
                drawable = getXHDpi(context, id);
                break;
            case XXHDPI:
                drawable = getXXHDpi(context, id);
                break;
            default:
                break;
            }
            if (drawable != null) {
                return drawable;
            }
            currentOrder -= 1;
        }
        
        drawable = getNoDpi(context, id);
        
        return drawable;
    }
    
    /**
     * return the best dpi's order in the DPI_LIST
     * @param density the origin density
     * @return the order
     */
    private static int findBestDpiOrder(int density) {
        int bestDpi = 3;
        for (int i = 0; i < DPI_LIST.length - 1; i++) {
            if (DPI_LIST[i] <= density && density <= DPI_LIST[i + 1]) {
                if (DPI_LIST[i + 1] == MAX_DPI) {
                    bestDpi = i;
                } else {
                    bestDpi = i + 1;
                }
                return bestDpi;
            }
        }
        return DPI_LIST.length - 1;
    }
    
    public static Object getNoDpi(Context context, int id) {
        switch(id) {
<!NODPI_BLOCK>
        default:
            return null;
        }
    }
    
    public static Object getLDpi(Context context, int id) {
        switch(id) {
<!LDPI_BLOCK>
        default:
            return null;
        }
    }
    
    public static Object getMDpi(Context context, int id) {
        switch(id) {
<!MDPI_BLOCK>
        default:
            return null;
        }
    }
    
    public static Object getHDpi(Context context, int id) {
        switch(id) {
<!HDPI_BLOCK>
        default:
            return null;
        }
    }
    
    public static Object getXHDpi(Context context, int id) {
        switch(id) {
<!XHDPI_BLOCK>
        default:
            return null;
        }
    }
    
    public static Object getXXHDpi(Context context, int id) {
        switch(id) {
<!XXHDPI_BLOCK>
        default:
            return null;
        }
    }
}
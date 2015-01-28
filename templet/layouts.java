package <!PACKAGE_NAME>;

import android.content.Context;
import android.view.View;

public class layouts {
    
    public static View get(Context context, int id) {
        switch(id) {
<!LAYOUT_BLOCK>
        default:
            return null;
        }
    }
}
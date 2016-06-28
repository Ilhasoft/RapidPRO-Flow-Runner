package in.ureport.flowrunner.util;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by john-mac on 6/27/16.
 */
public class KeyboardHelper {

    public static void hideForced(Fragment fragment) {
        KeyboardHelper.changeVisibilityInternal(fragment.getView(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private static void changeVisibilityInternal(View view, int flags) {
        ((InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(view.getWindowToken(), flags);
    }

}

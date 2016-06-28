package br.com.ilhasoft.flowrunner.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.Locale;

/**
 * Created by johncordeiro on 11/11/15.
 */
public class LocaleLoader extends AsyncTaskLoader<Locale []> {

    public LocaleLoader(Context context) {
        super(context);
    }

    @Override
    public Locale[] loadInBackground() {
        return Locale.getAvailableLocales();
    }
}

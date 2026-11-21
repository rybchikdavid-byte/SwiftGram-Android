package org.telegram.messenger;

import android.content.Context;

import org.telegram.messenger.regular.BuildConfig;

public class ApplicationLoaderImpl extends ApplicationLoader {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        SwiftGramCrashHandler.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SwiftGramCrashHandler.mark("app-created");
    }

    @Override
    protected String onGetApplicationId() {
        return BuildConfig.APPLICATION_ID;
    }
}

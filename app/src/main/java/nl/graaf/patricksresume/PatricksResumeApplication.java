package nl.graaf.patricksresume;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import timber.log.Timber;

/**
 * Created by Patrick van de Graaf on 11/27/2017.
 */

public class PatricksResumeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            //TODO add Fabric
//            CrashlyticsCore core = new CrashlyticsCore.Builder()
//                    .disabled(BuildConfig.DEBUG)
//                    .build();
//            Fabric.with(this, new Crashlytics.Builder().core(core).build());
//            Timber.plant(new CrashReportingTree());
        }
    }

    /**
     * A tree which logs important information for crash reporting.
     */
    private static class CrashReportingTree extends Timber.Tree {
        private static final String CRASHLYTICS_KEY_PRIORITY = "priority";
        private static final String CRASHLYTICS_KEY_TAG = "tag";
        private static final String CRASHLYTICS_KEY_MESSAGE = "message";

        @Override
        protected void log(int priority, String tag, @NonNull String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

//            Crashlytics.setInt(CRASHLYTICS_KEY_PRIORITY, priority);
//            Crashlytics.setString(CRASHLYTICS_KEY_TAG, tag);
//            Crashlytics.setString(CRASHLYTICS_KEY_MESSAGE, message);
//
//            if (t != null) {
//                if (priority == Log.ERROR) {
//                    CrashLibrary.logError(t);
//                } else if (priority == Log.WARN) {
//                    CrashLibrary.logWarning(t);
//                }
//            } else {
//                Crashlytics.logException(new Exception(message));
//            }
        }
    }
}

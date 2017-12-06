package nl.graaf.patricksresume.views.helpers;

/**
 * Created by Patrick van de Graaf on 11/27/2017.
 */

public final class CrashLibrary {
    private CrashLibrary() {
        throw new AssertionError("No instances.");
    }

    public static void log(int priority, String tag, String message) {
//        Crashlytics.log(priority, tag, message);
    }

    public static void logWarning(Throwable t) {
//        Crashlytics.logException(t);
    }

    public static void logError(Throwable t) {
//        Crashlytics.logException(t);
    }
}

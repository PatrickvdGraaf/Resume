package nl.graaf.patricksresume.views.helpers;

/**
 * Created by Patrick van de Graaf on 11/27/2017.
 */

public final class CrashLibrary {
    public static void log(int priority, String tag, String message) {
        // TODO add log entry to circular buffer.
    }

    public static void logWarning(Throwable t) {
        // TODO report non-fatal warning.
    }

    public static void logError(Throwable t) {
        // TODO report non-fatal error.
    }

    private CrashLibrary() {
        throw new AssertionError("No instances.");
    }
}

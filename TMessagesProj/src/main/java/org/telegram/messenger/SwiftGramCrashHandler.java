package org.telegram.messenger;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Writes launch and crash reports to Download/SwiftGram-*.txt so they can be
 * inspected without a computer attached.
 */
public final class SwiftGramCrashHandler {

    private static final String CRASH_FILE = "SwiftGram-crash.txt";
    private static final String START_FILE = "SwiftGram-start.txt";
    private static volatile Context appContext;
    private static volatile boolean installed;

    private SwiftGramCrashHandler() {
    }

    public static void install(Context context) {
        if (context == null) {
            return;
        }
        appContext = context.getApplicationContext();
        if (installed) {
            return;
        }
        installed = true;
        final Thread.UncaughtExceptionHandler previous = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            try {
                StringWriter buffer = new StringWriter();
                PrintWriter writer = new PrintWriter(buffer);
                writer.println("SwiftGram crash report");
                writer.println("time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()));
                writer.println("app: " + appVersion());
                writer.println("device: " + Build.MANUFACTURER + " " + Build.MODEL + ", Android " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")");
                writer.println("thread: " + (thread == null ? "?" : thread.getName()));
                writer.println();
                throwable.printStackTrace(writer);
                writer.println();
                writer.println("---- logcat ----");
                writer.println(readLogcat());
                writer.flush();
                write(CRASH_FILE, buffer.toString(), false);
            } catch (Throwable ignored) {
            }
            if (previous != null) {
                previous.uncaughtException(thread, throwable);
            }
        });
        try {
            write(START_FILE, "SwiftGram started\napp: " + appVersion() + "\ntime: "
                    + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()) + "\n", false);
        } catch (Throwable ignored) {
        }
    }

    private static String appVersion() {
        try {
            Context context = appContext;
            if (context == null) {
                return "?";
            }
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName
                    + " (" + (Build.VERSION.SDK_INT >= 28 ? context.getPackageManager().getPackageInfo(context.getPackageName(), 0).getLongVersionCode() : context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode) + ")";
        } catch (Throwable e) {
            return "?";
        }
    }

    private static String readLogcat() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{
                    "logcat", "-d", "-v", "threadtime", "-t", "400", "--pid=" + android.os.Process.myPid()
            });
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final InputStream in = process.getInputStream();
            final Thread reader = new Thread(() -> {
                try {
                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = in.read(buf)) > 0 && out.size() < 200000) {
                        out.write(buf, 0, len);
                    }
                } catch (Throwable ignored) {
                }
            });
            reader.start();
            process.waitFor();
            reader.join(1500);
            return out.toString("UTF-8");
        } catch (Throwable e) {
            return "(logcat unavailable: " + e + ")";
        }
    }

    private static void write(String name, String text, boolean append) {
        Context context = appContext;
        if (context == null) {
            return;
        }
        byte[] data = text.getBytes();
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                ContentResolver resolver = context.getContentResolver();
                Uri collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
                Uri existing = findDownload(resolver, collection, name);
                if (existing == null) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                    existing = resolver.insert(collection, values);
                }
                if (existing != null) {
                    OutputStream stream = resolver.openOutputStream(existing, append ? "wa" : "wt");
                    if (stream != null) {
                        stream.write(data);
                        stream.close();
                        return;
                    }
                }
            } catch (Throwable ignored) {
            }
        }
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            FileOutputStream stream = new FileOutputStream(new File(dir, name), append);
            stream.write(data);
            stream.close();
            return;
        } catch (Throwable ignored) {
        }
        try {
            File dir = context.getExternalFilesDir(null);
            if (dir != null) {
                FileOutputStream stream = new FileOutputStream(new File(dir, name), append);
                stream.write(data);
                stream.close();
            }
        } catch (Throwable ignored) {
        }
    }

    private static Uri findDownload(ContentResolver resolver, Uri collection, String name) {
        Cursor cursor = null;
        try {
            cursor = resolver.query(collection, new String[]{MediaStore.MediaColumns._ID},
                    MediaStore.MediaColumns.DISPLAY_NAME + "=?", new String[]{name}, null);
            if (cursor != null && cursor.moveToFirst()) {
                return ContentUris.withAppendedId(collection, cursor.getLong(0));
            }
        } catch (Throwable ignored) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
}

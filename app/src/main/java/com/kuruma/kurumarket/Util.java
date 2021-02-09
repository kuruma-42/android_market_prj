package com.kuruma.kurumarket;

import android.app.Activity;
import android.util.Patterns;
import android.widget.Toast;

import java.net.URLConnection;

public class Util {

    public Util() {/* */}

    public static final String INTENT_PATH = "path";
    public static final String INTENT_MEDIA = "MEDIA";

    public static final int GALLERY_IMAGE = 0;
    public static final int GALLERY_VIDEO = 1;

    public static void showToast(Activity activity, String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    public static boolean isStorageUri(String Uri){
       return Patterns.WEB_URL.matcher(Uri).matches() && Uri.contains("https://firebasestorage.googleapis.com/v0/b/kurumarket-f4a15.appspot.com/o/posts");
    }

    public static String storageUriToName(String uri){
        String[] list = uri.split("\\?");
        String[] list2 = list[0].split("posts");
        String name = list2[list2.length-1].replace("%2F","/");
        return name;
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }


}

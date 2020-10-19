package com.cpoc.softpostest;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import java.util.List;

public class Utils {

    public static final String PREF_SESSIONID = "sessionId";


    public static boolean isPackageInstalled(String targetPackage, PackageManager pm) {
        List<ApplicationInfo> packages;

        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(targetPackage))
                return true;
        }
        return false;
    }

    public static void setToken(Context context, String token) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_SESSIONID, token);
        editor.apply();
        editor.commit();
    }


    public static String getToken(Context context) {
        return getSharedPreferences(context).getString(PREF_SESSIONID, null);
    }


    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

}

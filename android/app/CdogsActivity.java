package org.libsdl.app;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CdogsActivity extends SDLActivity {
    private static final String TAG = "C-Dogs";
    private static final String ASSET_PREFS = "cdogs_asset_install";
    private static final String ASSET_UPDATE_KEY = "apk_last_update";
    private static final String[] GAME_ASSET_DIRS = {
        "data", "graphics", "sounds", "music", "dogfights", "missions"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            installGameAssetsIfNeeded();
        } catch (IOException e) {
            Log.e(TAG, "Unable to install required C-Dogs game assets", e);
            throw new RuntimeException("Unable to install C-Dogs game assets", e);
        }

        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private void installGameAssetsIfNeeded() throws IOException {
        long packageUpdateTime = 0L;
        try {
            packageUpdateTime = getPackageManager()
                .getPackageInfo(getPackageName(), 0).lastUpdateTime;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Could not read package update time; reinstalling assets", e);
        }

        SharedPreferences prefs = getSharedPreferences(ASSET_PREFS, MODE_PRIVATE);
        if (packageUpdateTime != 0L &&
            prefs.getLong(ASSET_UPDATE_KEY, Long.MIN_VALUE) == packageUpdateTime) {
            return;
        }

        AssetManager assets = getAssets();
        File storageRoot = getFilesDir();
        Log.i(TAG, "Installing C-Dogs game assets into " + storageRoot);
        for (String dir : GAME_ASSET_DIRS) {
            copyAssetTree(assets, dir, storageRoot);
        }

        if (packageUpdateTime != 0L) {
            prefs.edit().putLong(ASSET_UPDATE_KEY, packageUpdateTime).commit();
        }
        Log.i(TAG, "C-Dogs game assets installed");
    }

    private void copyAssetTree(AssetManager assets, String assetPath, File storageRoot)
            throws IOException {
        String[] children = assets.list(assetPath);
        if (children == null) {
            throw new IOException("Unable to list APK asset " + assetPath);
        }

        File destination = new File(storageRoot, assetPath);
        if (children.length == 0) {
            copyAssetFile(assets, assetPath, destination);
            return;
        }

        if (!destination.isDirectory() && !destination.mkdirs()) {
            throw new IOException("Unable to create asset directory " + destination);
        }
        for (String child : children) {
            copyAssetTree(assets, assetPath + "/" + child, storageRoot);
        }
    }

    private void copyAssetFile(AssetManager assets, String assetPath, File destination)
            throws IOException {
        File parent = destination.getParentFile();
        if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
            throw new IOException("Unable to create asset directory " + parent);
        }

        try (InputStream in = assets.open(assetPath);
             OutputStream out = new FileOutputStream(destination, false)) {
            byte[] buffer = new byte[16384];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }
    }

    @Override
    protected String[] getLibraries() {
        return new String[] { "SDL2", "SDL2_mixer", "main" };
    }
}

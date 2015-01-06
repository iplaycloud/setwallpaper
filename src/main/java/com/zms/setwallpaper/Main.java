package com.zms.setwallpaper;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class Main extends ActionBarActivity {
    private Button btnSetWallpaper;
    private Button btnShortcut;
    private String strShortcut; // 快捷方式名称
    private Bitmap bmWallpaper; // 壁纸
    private int sdkVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btnSetWallpaper = (Button) findViewById(R.id.btnSetWallpaper);
        btnShortcut = (Button) findViewById(R.id.btnShortCut);
        strShortcut = getString(R.string.app_name);
        sdkVersion = android.os.Build.VERSION.SDK_INT;
        btnSetWallpaper.setOnClickListener(new onClickListenerImp());
        btnShortcut.setOnClickListener(new onClickListenerImp());
        bmWallpaper = BitmapFactory.decodeResource(Main.this.getResources(), R.drawable.wallpaper);

    }

    private class onClickListenerImp implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == btnSetWallpaper) {
                try {
                    Main.this.setWallpaper(bmWallpaper);
                    Toast.makeText(Main.this,"Set Wallpaper Success.",Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(Main.this,"Set Wallpaper Failed.",Toast.LENGTH_SHORT).show();
                }
            } else if (v == btnShortcut) {
                if (isShortcutExist()) {
                    delShortcut();
                }
                addShortcut();
            }
        }
    }

    private boolean isShortcutExist() {
        boolean isInstallShortcut = false;
        final ContentResolver contentResolver = Main.this.getContentResolver();
        String AUTHORITY;

        if (sdkVersion < 8) {
            AUTHORITY = "com.android.launcher.settings";
        } else if (sdkVersion < 19) {
            AUTHORITY = "com.android.launcher2.settings";
        } else {
            AUTHORITY = "com.android.launcher3.settings";
        }

        final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
        Cursor cursor = contentResolver.query(CONTENT_URI, new String[]{"title", "iconResource"}, "title=?",
                new String[]{strShortcut.trim()}, null);
        if (cursor != null && cursor.getCount() > 0) {
            isInstallShortcut = true;
        }
        return isInstallShortcut;
    }

    /**
     * Create Shortcut
     */
    private void addShortcut() {
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, strShortcut);//快捷方式的名称
        shortcut.putExtra("duplicate", false); //不允许重复创建

        Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
        shortcutIntent.setClassName(this, this.getClass().getName());
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);

        //快捷方式的图标
        Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
        sendBroadcast(shortcut);
        Toast.makeText(Main.this, "Create Shortcut!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Delete Shortcut
     */
    private void delShortcut() {
        Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");

        //快捷方式的名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, strShortcut);
        String appClass = this.getPackageName() + "." + this.getLocalClassName();
        ComponentName comp = new ComponentName(this.getPackageName(), appClass);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp));

        sendBroadcast(shortcut);
        Toast.makeText(Main.this, "Delete Shortcut!", Toast.LENGTH_SHORT).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

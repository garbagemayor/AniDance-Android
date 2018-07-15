package anidance.anidance_android;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class FileModeActivity extends AppCompatActivity {

    public static String TAG = "FileModeActivity";

    private Uri mFileUri = null;
    MediaPlayer mMediaPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_file_mode);

        //浏览文件按钮
        findViewById(R.id.file_mode_browse_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });

        //试听按钮
        findViewById(R.id.file_mode_listen_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFileUri != null) {
                    mMediaPlayer = new MediaPlayer();
                    try {
                        mMediaPlayer.setDataSource(FileModeActivity.this, mFileUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mMediaPlayer.prepareAsync();
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mMediaPlayer.start();
                        }
                    });
                }
            }
        });
    }

    //浏览文件返回时
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "requestCode = " + resultCode);
            if (requestCode == 1) {
                mFileUri = data.getData();
                String fileDisplayName = getDisplayNameFromUri(FileModeActivity.this, mFileUri);
                ((TextView) FileModeActivity.this.findViewById(R.id.file_mode_path_tv)).setText(fileDisplayName);
            }
        }
    }

    //从Uri获取不带路径的文件名
    private static String getDisplayNameFromUri(Context context, Uri uri) {
        if (uri == null || uri.getScheme() == null) {
            return null;
        } else if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            return uri.getLastPathSegment();
        }
        Cursor cursor = context.getContentResolver().query( uri, new String[] {MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null );
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
        return cursor.getString(columnIndex);
    }
}

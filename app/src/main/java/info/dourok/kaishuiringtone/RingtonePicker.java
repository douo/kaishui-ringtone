package info.dourok.kaishuiringtone;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ipaulpro.afilechooser.FileChooserActivity;

import java.io.File;

import info.dourok.kaishuiringtone.R;

public class RingtonePicker extends FileChooserActivity {

    private RingtoneHelper mRingtoneHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRingtoneHelper = new RingtoneHelper(this);
        setFilterByMimeType(true);
        System.out.println(LogUtils.dumpIntent(getIntent()));
    }

    @Override
    protected void finishWithResult(File file) {
        Intent i = new Intent();
        Uri fileUri = Uri.fromFile(file);
        Uri uri = mRingtoneHelper.insertRingtoneAudio(fileUri);
        i.putExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI,uri);
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public String getMimeType() {
        return "audio/*";
    }
}

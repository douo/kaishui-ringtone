package info.dourok.kaishuiringtone;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity implements RingtoneListFragment.OnFragmentInteractionListener {
    private RingtoneManager mRingtoneManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRingtoneManager = new RingtoneManager(this);
        //dumpRM();
    }

    private void dumpRM() {
        d("TYPE_RINGTONE:" + RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
        d(RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE));
        d("TYPE_NOTIFICATION:" + RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        d(RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION));
        d("TYPE_ALARM:" + RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        d(RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM));
        mRingtoneManager.setType(RingtoneManager.TYPE_ALL);
        Cursor cursor = mRingtoneManager.getCursor();
        while (cursor.moveToNext()) {
            int c = cursor.getColumnCount();
            for (int i = 0; i < c; i++) {
                String name = cursor.getColumnName(i);
                String value = cursor.getString(i);
                d(name + ":" + value);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_test:
                testRingtonePicker();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void testRingtonePicker() {
        Intent intent = new Intent(
                RingtoneManager.ACTION_RINGTONE_PICKER);

        // 设置类型为来电
        // intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
        // RingtoneManager.TYPE_RINGTONE);

        // 列表中不显示"默认铃声"选项，默认是显示的
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT,
                false);

        // 列表中不显示"静音"选项，默认是显示该选项，如果默认"静音"项被用户选择，
        // 则EXTRA_RINGTONE_PICKED_URI 为null
        // intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT,false);

        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_INCLUDE_DRM,
                true);

        // 设置列表对话框的标题，不设置，默认显示"铃声"
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "设置来电铃声");
        startActivityForResult(intent, 0x12);
    }

    private static final String TAG = "KAISHUI_RINGTONE";

    private void d(Object obj) {
        Log.d(TAG, obj == null ? "null" : obj.toString());
    }


    @Override
    public void onFragmentInteraction(String id) {

    }
}

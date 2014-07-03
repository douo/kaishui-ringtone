package info.dourok.kaishuiringtone;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends Activity implements View.OnClickListener{

    private static final int RQ_CHOOSING_FILE = 0x123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(this);
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
        if (id == R.id.action_file) {
            chooseFile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void chooseFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent, RQ_CHOOSING_FILE);
    }

    @Override
    public void onClick(View v) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
    }

    private void onpenChooser(){
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

    /**
     * 设置铃声之后的回调函数
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        } else {
            // 得到我们选择的铃声,如果选择的是"静音"，那么将会返回null
            Uri uri = data
                    .getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            Toast.makeText(this, uri + "", Toast.LENGTH_SHORT).show();
            if (uri != null) {
                switch (requestCode) {
                    case 0x12:
                        RingtoneManager.setActualDefaultRingtoneUri(this,
                                RingtoneManager.TYPE_ALARM, uri);
                        break;
                }
            }
        }
    }
}

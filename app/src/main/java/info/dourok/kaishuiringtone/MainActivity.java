package info.dourok.kaishuiringtone;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;

import java.io.File;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener{

    private static final int RQ_CHOOSING_FILE = 0x123;
    private  RingtoneManager mRingtoneManager;
    private ListView mListView;
    private RingtoneCursorAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnItemClickListener(this);
        mRingtoneManager = new RingtoneManager(this);
        queryMediaRingtones();
        //dumpRM();
    }

    private void dumpRM(){
        d("TYPE_RINGTONE:"+RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
        d(RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE));
        d("TYPE_NOTIFICATION:"+RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        d(RingtoneManager.getActualDefaultRingtoneUri(this,RingtoneManager.TYPE_NOTIFICATION));
        d("TYPE_ALARM:"+RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        d(RingtoneManager.getActualDefaultRingtoneUri(this,RingtoneManager.TYPE_ALARM));
        mRingtoneManager.setType(RingtoneManager.TYPE_ALL);
        Cursor cursor = mRingtoneManager.getCursor();
        while(cursor.moveToNext()){
            int c = cursor.getColumnCount();
            for(int i=0;i< c;i++){
                String name = cursor.getColumnName(i);
                String value = cursor.getString(i);
                d(name+":"+value);
            }
        }
    }

    private void queryMediaRingtones(){
        Cursor managedCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                constructBooleanTrueWhereClause(new String[]{MediaStore.Audio.AudioColumns.IS_RINGTONE,MediaStore.Audio.AudioColumns.IS_NOTIFICATION,MediaStore.Audio.AudioColumns.IS_ALARM}), null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        mAdapter = new RingtoneCursorAdapter(this,managedCursor);
        mListView.setAdapter(mAdapter);
    }

    private void refresh(){
        Cursor managedCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                constructBooleanTrueWhereClause(new String[]{MediaStore.Audio.AudioColumns.IS_RINGTONE,MediaStore.Audio.AudioColumns.IS_NOTIFICATION,MediaStore.Audio.AudioColumns.IS_ALARM}), null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        mAdapter.changeCursor(managedCursor);
    }

    private static String constructBooleanTrueWhereClause(String [] columns) {

        if (columns == null) return null;

        StringBuilder sb = new StringBuilder();
        sb.append("(");

        for (int i = columns.length - 1; i >= 0; i--) {
            sb.append(columns[i]).append("=1 or ");
        }

        if (columns.length > 0) {
            // Remove last ' or '
            sb.setLength(sb.length() - 4);
        }

        sb.append(")");

        return sb.toString();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                chooseFile();
                return true;
            case R.id.action_test:
                testRingtonePicker();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void chooseFile(){
        Intent getContentIntent = new Intent(this,FileChooserActivity.class);
        getContentIntent.setType("audio/*");
        getContentIntent.putExtra(FileChooserActivity.KEY_FILTER_BY_MIME_TYPE,true);
        startActivityForResult(getContentIntent, RQ_CHOOSING_FILE);
    }

    private Uri insertRingtoneAudio(Uri fileUri){
        String path = FileUtils.getPath(this, fileUri);
        if (path != null && FileUtils.isLocal(path)) {
            File file = new File(path);
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            MimeTypeMap mtm =  MimeTypeMap.getSingleton();
            mmr.setDataSource(file.getAbsolutePath());
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
            values.put(MediaStore.MediaColumns.TITLE, mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
            values.put(MediaStore.MediaColumns.SIZE, file.length());
            values.put(MediaStore.MediaColumns.MIME_TYPE,  mtm.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.toURI().toString())));
            values.put(MediaStore.Audio.Media.ARTIST, mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            values.put(MediaStore.Audio.Media.DURATION, mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
            values.put(MediaStore.Audio.Media.IS_ALARM, true);
            values.put(MediaStore.Audio.Media.IS_MUSIC, true);
            Uri uri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
            d("content:"+uri);
            //删除原有data
            getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + file.getAbsolutePath() + "\"", null);
            Uri newUri = getContentResolver().insert(uri, values);
            d("new:"+newUri);
            refresh();
            return newUri;
        }
        return null;
    }

    private void testRingtonePicker(){
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
        switch (requestCode) {
            case RQ_CHOOSING_FILE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    insertRingtoneAudio(uri);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(r !=null){
            r.stop();
        }

    }

    private static final String TAG = "KAISHUI_RINGTONE";
    Ringtone r ;
    Uri uri;
    private void d(Object obj){
        Log.d(TAG, obj == null ? "null" : obj.toString());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) mAdapter.getItem(position);
        for(int i =0;i<cursor.getColumnCount();i++){
            d(cursor.getColumnName(i) + ":" + cursor.getString(i));
        }
        if(r!=null){
            r.stop();
            r = null;
        }

        Uri _uri = Uri.withAppendedPath( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
        if(!_uri.equals(uri)){
            uri = _uri;
            r = RingtoneManager.getRingtone(this, uri);
            try {
                r.play();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }else{
            uri = null;
        }


    }
}

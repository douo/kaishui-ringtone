package info.dourok.kaishuiringtone;

import android.content.ContentValues;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.ipaulpro.afilechooser.utils.FileUtils;

import java.io.File;

/**
 * Created by charry on 2014/7/5.
 */
public class RingtoneHelper {
    private Context mContext;
    private MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    public RingtoneHelper(Context context) {
        this.mContext = context;
    }

    public Uri insertRingtoneAudio(Uri fileUri){
        return insertRingtoneAudio(fileUri,true,true,true,true);
    }

    public Uri insertRingtoneAudio(Uri fileUri,boolean ringtone,boolean notificatin,boolean alarm,boolean music) {
        String path = FileUtils.getPath(mContext, fileUri);
        if (path == null || !FileUtils.isLocal(path)) {
            throw new IllegalArgumentException("path is invalid:" + path);
        }
        File file = new File(path);
        MimeTypeMap mtm = MimeTypeMap.getSingleton();
        mmr.setDataSource(file.getAbsolutePath());
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        values.put(MediaStore.MediaColumns.SIZE, file.length());
        values.put(MediaStore.MediaColumns.MIME_TYPE, mtm.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.toURI().toString())));
        values.put(MediaStore.Audio.Media.ARTIST, mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        values.put(MediaStore.Audio.Media.DURATION, mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        values.put(MediaStore.Audio.Media.IS_RINGTONE, ringtone);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, notificatin);
        values.put(MediaStore.Audio.Media.IS_ALARM, alarm);
        values.put(MediaStore.Audio.Media.IS_MUSIC, music);
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
        d("content:" + uri);
        //删除原有data
        mContext.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + file.getAbsolutePath() + "\"", null);
        Uri newUri = mContext.getContentResolver().insert(uri, values);
        d("new:" + newUri);
        return newUri;
    }

    private void d(Object obj) {
        Log.d("RingtoneHelper", obj == null ? "null" : obj.toString());
    }
}

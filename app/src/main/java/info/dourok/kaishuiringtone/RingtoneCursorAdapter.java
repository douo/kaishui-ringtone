package info.dourok.kaishuiringtone;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by charry on 2014/7/4.
 */
public class RingtoneCursorAdapter extends CursorAdapter{
    public RingtoneCursorAdapter(Context context,Cursor cursor){
        super(context,cursor);
    }


    private static final class ViewHolder{
        private TextView title;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_ringtone, null);
        TextView title = (TextView) view.findViewById(R.id.textView);
        holder.title = title;
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
        holder.title.setText(title);
    }
}

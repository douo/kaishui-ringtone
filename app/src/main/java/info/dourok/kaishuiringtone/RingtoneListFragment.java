package info.dourok.kaishuiringtone;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.ListFragment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import info.dourok.kaishuiringtone.dummy.DummyContent;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class RingtoneListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int RQ_CHOOSING_FILE = 0x123;
    private static final int LOADER_ID =0x1;

    private OnFragmentInteractionListener mListener;
    private RingtoneCursorAdapter mAdapter;
    private RingtoneHelper mRingtoneHelper;

    public static RingtoneListFragment newInstance() {
        RingtoneListFragment fragment = new RingtoneListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RingtoneListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAdapter = new RingtoneCursorAdapter(getActivity(),null);
        if (getArguments() != null) {

        }
        setListAdapter(mAdapter);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mRingtoneHelper = new RingtoneHelper(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID,null,this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(r !=null){
            r.stop();
        }
        mListener = null;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.ringtone_list,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                startActivityForResult(RingtoneHelper.createChooseRingtoneFileIntent(getActivity()),RQ_CHOOSING_FILE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    Ringtone r ;
    Uri uri;
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor cursor = (Cursor) mAdapter.getItem(position);
        for(int i =0;i<cursor.getColumnCount();i++){
            d(cursor.getColumnName(i) + ":" + cursor.getString(i));
        }
        if(r!=null){
            r.stop();
            r = null;
        }

        Uri _uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
        if(!_uri.equals(uri)){
            uri = _uri;
            r = RingtoneManager.getRingtone(getActivity(), uri);
            try {
                r.play();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else{
            uri = null;
        }
    }

    /**
     * 设置铃声之后的回调函数
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RQ_CHOOSING_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    mRingtoneHelper.insertRingtoneAudio(uri);
                    getLoaderManager().restartLoader(LOADER_ID, null, this);
                }
                break;
        }
    }


    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String whereClause = constructBooleanTrueWhereClause(new String[]{MediaStore.Audio.AudioColumns.IS_RINGTONE,MediaStore.Audio.AudioColumns.IS_NOTIFICATION,MediaStore.Audio.AudioColumns.IS_ALARM});
        return  new CursorLoader(getActivity(), MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                RingtoneHelper.RINGTONE_PROJECTION, whereClause, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
             mAdapter.swapCursor(data);
     }

    @Override
    public void onLoaderReset(Loader loader) {
        if(mAdapter!=null){
            mAdapter.swapCursor(null);
        }
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

    private void d(Object obj){
        Log.d("RingtoneListFragment", obj == null ? "null" : obj.toString());
    }
}

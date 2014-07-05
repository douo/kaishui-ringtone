/*
 * Copyright (C) 2013 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ipaulpro.afilechooser;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import java.io.File;
import java.util.List;

/**
 * Fragment that displays a list of Files in a given path.
 * 
 * @version 2013-12-11
 * @author paulburke (ipaulpro)
 */
public class FileListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<List<File>> {
    /**
     * Interface to listen for events.
     */
    public interface Callbacks {
        /**
         * Called when a file is selected from the list.
         *
         * @param file The file selected
         */
        public void onFileSelected(File file);

        public boolean filterByMimeType();

        public String getMimeType();
    }

    private static final int LOADER_ID = 0;

    private FileListAdapter mAdapter;
    private String mPath;
    private Callbacks mListener;

    /**
     * Create a new instance with the given file path.
     *
     * @param path The absolute path of the file (directory) to display.
     * @return A new Fragment with the given file path.
     */
    public static FileListFragment newInstance(String path) {
        FileListFragment fragment = new FileListFragment();
        Bundle args = new Bundle();
        args.putString(FileChooserActivity.PATH, path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FileListFragment.Callbacks");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAdapter = new FileListAdapter(getActivity());
        mPath = getArguments() != null ? getArguments().getString(
                FileChooserActivity.PATH) : Environment
                .getExternalStorageDirectory().getAbsolutePath();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setEmptyText(getString(R.string.empty_directory));
        setListAdapter(mAdapter);
        setListShown(false);

        getLoaderManager().initLoader(LOADER_ID, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        FileListAdapter adapter = (FileListAdapter) l.getAdapter();
        if (adapter != null) {
            clearFilter();
            File file = adapter.getItem(position);
            mPath = file.getAbsolutePath();
            mListener.onFileSelected(file);
        }
    }

    @Override
    public Loader<List<File>> onCreateLoader(int id, Bundle args) {

        if(mListener.filterByMimeType()){
            String type = mListener.getMimeType();
            if(type !=null){
                return new FileLoader(getActivity(), mPath, type);
            }
        }
        return new FileLoader(getActivity(), mPath);
    }

    @Override
    public void onLoadFinished(Loader<List<File>> loader, List<File> data) {
        mAdapter.setListItems(data);
        if (isResumed())
            setListShown(true);
        else
            setListShownNoAnimation(true);
    }

    @Override
    public void onLoaderReset(Loader<List<File>> loader) {
        mAdapter.clear();
    }

    private MenuItem mFilterMenuItem;
    private SearchView.OnQueryTextListener mOnQueryTextListener = new SearchView.OnQueryTextListener() {
        public boolean onQueryTextChange(String newText) {
            if (mAdapter != null) {
                mAdapter.getFilter().filter(newText);
            }
            return true;
        }

        public boolean onQueryTextSubmit(String query) {
            if (mAdapter != null) {
                mAdapter.getFilter().filter(query);
            }
            return true;
        }
    };

    private void clearFilter(){
        if(mFilterMenuItem!=null){
            mFilterMenuItem.collapseActionView();
            mAdapter.getFilter().filter("");
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            inflater.inflate(R.menu.file_list, menu);
            MenuItem item = menu.findItem(R.id.action_filter);
            item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    SearchView searchView = (SearchView) item.getActionView();
                    if (null != searchView) {
                        searchView.setOnQueryTextListener(mOnQueryTextListener);
                    }
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item){
                    SearchView searchView = (SearchView) item.getActionView();
                    if (null != searchView) {
                        searchView.setOnQueryTextListener(null);
                        mAdapter.getFilter().filter("");
                    }
                    return true;
                }
            });
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) item.getActionView();
            if (null != searchView) {
                searchView
                        .setSearchableInfo(searchManager
                                .getSearchableInfo(getActivity()
                                        .getComponentName()));
                searchView.setIconifiedByDefault(false);
            }
            mFilterMenuItem = item;
        }
        super.onCreateOptionsMenu(menu, inflater);
    }
}

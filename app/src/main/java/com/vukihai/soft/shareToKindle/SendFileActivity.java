package com.vukihai.soft.shareToKindle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SendFileActivity extends AppCompatActivity implements DeleteListDialog.NoticeDialogListener {
    private Intent mIntent;
    private String mAction;
    private String mMime;
    private RecyclerView filesRecyclerView;
    private RecyclerView.Adapter filesAdapter;
    private RecyclerView.LayoutManager filesLayoutManager;
    SharedPreferences mSharedPreferences;
    List<Uri> fileUriList;
    private Intent chooseFileIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);
        fileUriList = new ArrayList<>();
        mSharedPreferences = getSharedPreferences("ShareToKindle", Context.MODE_PRIVATE);
        loadRef();
        initView();

        mIntent = getIntent();
        mAction = mIntent.getAction();
        mMime = mIntent.getType();
        if (Intent.ACTION_SEND.equals(mAction) && mMime != null) {
            handle();
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(mAction) && mMime != null) {
            handleMultiple();
        }
    }

    private void initView() {
//        listUriTextView = findViewById(R.id.tv_list_uri);
        filesRecyclerView = findViewById(R.id.recycle_view_files);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.file_sharing));
        }

        filesLayoutManager = new LinearLayoutManager(this);
        filesRecyclerView.setLayoutManager(filesLayoutManager);
        filesAdapter = new FilesAdapter(fileUriList);
        filesRecyclerView.setAdapter(filesAdapter);
    }

    @Override
    public boolean onNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
//        fileUriList.clear();
//        filesAdapter.notifyDataSetChanged();
    }

    private void loadRef() {
        String curFileListString = mSharedPreferences.getString("curFileList", "");
        Log.d("vukihai", " 1" + curFileListString);
        String[] tmp = curFileListString.split(",");
        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i].length() != 0)
                fileUriList.add(Uri.parse(tmp[i]));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveToRef();
    }

    private void saveToRef() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String tmp = "";
        for (int i = 0; i < fileUriList.size(); i++) {
            tmp += fileUriList.get(i).toString() + ",";
        }
        editor.putString("curFileList", tmp);
        editor.apply();
        Log.d("vukihai", "2" + tmp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                addFile();
                break;
            case R.id.clear_all:
                clearAllFile();
                break;
        }
        return true;
    }

    private void clearAllFile() {
        DeleteListDialog deleteListDialog = new DeleteListDialog();
        deleteListDialog.show(getSupportFragmentManager(), "DeleteListDialog");
    }

    private void addFile() {
        if (chooseFileIntent == null)
            chooseFileIntent = new Intent()
                    .setType("*/*")
                    .setAction(Intent.ACTION_GET_CONTENT);
        chooseFileIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(chooseFileIntent, getString(R.string.choose_files)), 123);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("vukihai", "requested!");
        if(requestCode == 123) {
            if(resultCode == Activity.RESULT_OK) {
                if(data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    int currentItem = 0;
                    while(currentItem < count) {
                        Uri imageUri = data.getClipData().getItemAt(currentItem).getUri();
                        currentItem = currentItem + 1;
                        fileUriList.add(imageUri);
                    }
                } else if(data.getData() != null) {
                    fileUriList.add(data.getData());
                }
            }
        }
        removeDuplicate();
        saveToRef();
        filesAdapter.notifyDataSetChanged();
    }
    void removeDuplicate() {
        for (int i = 0; i < fileUriList.size(); i++) {
            for (int j = i + 1; j < fileUriList.size(); j++) {
                if (i < fileUriList.size() && j < fileUriList.size() && fileUriList.get(i).toString().equals(fileUriList.get(j).toString())) {
                    fileUriList.remove(j);
                }
            }
        }
    }

    private void handle() {
        Uri uri = mIntent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (uri != null) {
            fileUriList.add(uri);
        }
        removeDuplicate();
        saveToRef();
    }

    private void handleMultiple() {
        List<Uri> uris = mIntent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (uris != null) {
            //update ui
            for (int i = 0; i < uris.size(); i++)
                if (uris.get(i) != null) {
                    fileUriList.add(uris.get(i));
                }
        }
        removeDuplicate();
        saveToRef();
    }

    @Override
    public void deleteItem() {
        fileUriList.clear();
        filesAdapter.notifyDataSetChanged();
    }
}

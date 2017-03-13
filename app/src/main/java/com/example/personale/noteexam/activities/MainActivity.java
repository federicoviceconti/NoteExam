package com.example.personale.noteexam.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.personale.noteexam.R;
import com.example.personale.noteexam.controller.adapter.NoteAdapter;
import com.example.personale.noteexam.controller.utilities.Field;
import com.example.personale.noteexam.model.Note;

/**
 * Created by personale on 13/03/2017.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    RecyclerView recyclerView;
    FloatingActionButton fabAdd;
    EditText searchEt;
    NoteAdapter adapter;
    private int stateLayout;
    private int stateOrder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fabAdd = (FloatingActionButton) findViewById(R.id.main_add_fab);
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_list);
        searchEt = (EditText) findViewById(R.id.main_search_et);
        initializeComponent();

        fabAdd.setOnClickListener(this);
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(adapter != null) {
                    adapter.searchNote(s.toString());
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.name_layout_preferences), Context.MODE_PRIVATE).edit();
        editor.putInt(getString(R.string.state_layout_preferences), stateLayout);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_main_layout:
                setLayout(item);
                break;
            case R.id.menu_main_order:
                setOrder(item);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            adapter.operationOnNote((Note) data.getParcelableExtra(Field.NOTE_OBJECT), data.getIntExtra(Field.POSITION, -1), requestCode);

            if(requestCode == Field.ADD){
                recyclerView.scrollToPosition(0);
            }
        } else {
            Toast.makeText(this, Field.ERROR_FIELD, Toast.LENGTH_SHORT).show();
        }

        searchEt.setText("");
    }

    private void initializeComponent() {

        stateLayout = getSharedPreferences(getString(R.string.name_layout_preferences),
                Context.MODE_PRIVATE).getInt(getString(R.string.state_layout_preferences), 10);

        if(adapter == null){
            adapter = new NoteAdapter(this);
            recyclerView.setAdapter(adapter);
        }

        if(recyclerView.getLayoutManager() == null){
            recyclerView.setLayoutManager(
                    stateLayout == Field.LINEAR_LAYOUT ? new LinearLayoutManager(this) : new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }

        setOrder();
        adapter.setAllNotes();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_add_fab:
                startActivityForResult(new Intent(this, AddNoteActivity.class), Field.ADD);
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_menu:
                adapter.deleteNote(adapter.getPosition());
                break;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater menuInflater = ((MainActivity)v.getContext()).getMenuInflater();
        menuInflater.inflate(R.menu.menu_item, menu);
    }

    private void setLayout(MenuItem item) {
        switch (stateLayout){
            case Field.STAGGERED_LAYOUT:
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                stateLayout = Field.LINEAR_LAYOUT;
                break;
            case Field.LINEAR_LAYOUT:
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                stateLayout = Field.STAGGERED_LAYOUT;
                break;
        }

        item.setIcon(stateLayout == Field.LINEAR_LAYOUT ? R.drawable.linear : R.drawable.staggered);
    }

    public void setOrder(MenuItem order) {
        if(setItemOrder() == Field.ORDER_DESC){
            order.setIcon(R.drawable.ic_desc);
        } else {
            order.setIcon(R.drawable.ic_asc);
        }
    }

    public void setOrder(){
        setItemOrder();
    }

    public int setItemOrder(){
        switch (stateOrder) {
            default:
                System.out.println("OK");
                stateOrder = Field.ORDER_DESC;
            case Field.ORDER_DESC:
                adapter.order(stateOrder);
                stateOrder = Field.ORDER_ASC;
                break;
            case Field.ORDER_ASC:
                adapter.order(stateOrder);
                stateOrder = Field.ORDER_DESC;
                break;
        }

        return stateOrder;
    }
}

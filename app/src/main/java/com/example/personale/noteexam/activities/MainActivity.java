package com.example.personale.noteexam.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;

/**
 * Created by personale on 13/03/2017.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int SIGN_IN = 156;
    public NoteAdapter adapter;
    RecyclerView recyclerView;
    FloatingActionButton fabAdd;
    EditText searchEt;
    private int stateLayout;
    private int stateOrder;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth.AuthStateListener authStateListener;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    private boolean internetConnectionAbsent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();

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

        authStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null) {
                    signIn(user.getDisplayName());
                } else {
                    startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                            ))
                            .build(), SIGN_IN);
                }
            }
        };
    }

    private void signIn(String name) {
        Toast.makeText(this, "Welcome " + name + "!", Toast.LENGTH_SHORT).show();
    }

    private void signOut() {
        Toast.makeText(this, "Goodbye!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.name_layout_preferences), Context.MODE_PRIVATE).edit();
        editor.putInt(getString(R.string.state_layout_preferences), stateLayout);
        editor.apply();

        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(checkConnection()){
            firebaseAuth.addAuthStateListener(authStateListener);
        }
    }

    private boolean checkConnection(){
        NetworkInfo info = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return  info != null && info.isConnected();
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
            case R.id.save_db:
                saveAsJson();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode){
            case Activity.RESULT_OK:
                if(requestCode == SIGN_IN){
                    Toast.makeText(this, "Signed in successfully!", Toast.LENGTH_SHORT).show();

                } else if(requestCode == Field.ADD || requestCode == Field.EDIT){
                    adapter.operationOnNote((Note) data.getParcelableExtra(Field.NOTE_OBJECT), data.getIntExtra(Field.POSITION, -1), requestCode);

                    if(requestCode == Field.ADD){
                        recyclerView.scrollToPosition(0);
                    }
                } else {
                    Toast.makeText(this, Field.ERROR_SIGN_IN, Toast.LENGTH_SHORT).show();
                }

                break;
            case Activity.RESULT_CANCELED:
                break;
        }
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
            order.setIcon(R.drawable.ic_asc);
        } else {
            order.setIcon(R.drawable.ic_desc);
        }
    }

    public void setOrder(){
        setItemOrder();
    }

    public int setItemOrder(){
        switch (stateOrder) {
            default:
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

    public void saveAsJson(){
        for(Note n : adapter.getAllNotes()){
            databaseReference.push().setValue(n);
        }
    }
}

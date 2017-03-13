package com.example.personale.noteexam.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;

import com.example.personale.noteexam.R;
import com.example.personale.noteexam.controller.utilities.Field;
import com.example.personale.noteexam.controller.utilities.Utilities;
import com.example.personale.noteexam.model.Note;
import com.rarepebble.colorpicker.ColorPickerView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by personale on 13/03/2017.
 */

public class AddNoteActivity extends AppCompatActivity implements View.OnClickListener {

    Intent i;
    Date d;
    EditText bodyEt, titleEt, dateExpiration;
    Date expirationDate;
    private int colorChoosed = Color.GRAY;
    private boolean isSpecial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add);

        titleEt = (EditText) findViewById(R.id.add_title_et);
        bodyEt = (EditText) findViewById(R.id.add_body_et);
        dateExpiration = (EditText) findViewById(R.id.add_date_expiration);
        dateExpiration.setOnClickListener(this);
        initializeComponent(getIntent().getFlags(), getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_add, menu);

        setSpecial(menu.getItem(1), isSpecial);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_note:
                addNote();
                break;
            case R.id.color_note:
                colorPicker();
                break;
            case R.id.add_mark:
                setSpecial(item, isSpecial = !isSpecial);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setSpecial(MenuItem item, boolean isSpecial) {
        if (isSpecial) {
            item.setIcon(R.drawable.ic_bookmark_white_full);
        } else {
            item.setIcon(R.drawable.ic_bookmark_white_empty);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        i = new Intent();
        setResult(Activity.RESULT_CANCELED, i);
        finish();
    }

    public void createCalendar() {
        CalendarView calendarView;
        AlertDialog.Builder calendarBuilder = Utilities.createCalendar(this, calendarView = new CalendarView(this));

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                expirationDate = new Date(year, month, dayOfMonth);
                dateExpiration.setText(new SimpleDateFormat("dd-MMM-yy").format(new Date(year, month, dayOfMonth)));
            }
        });

        calendarBuilder.create().show();
    }

    public void colorPicker() {
        final ColorPickerView colorPickerView;
        AlertDialog.Builder colorPicker = Utilities.createPicker(this, colorPickerView = new ColorPickerView(this));
        colorPicker.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                colorChoosed = colorPickerView.getColor() == Color.WHITE ? Color.GRAY : colorPickerView.getColor();
                setColorActivity();
            }
        });

        colorPicker.create().show();
    }

    private void setColorActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorChoosed));
            findViewById(R.id.linear_add).setBackgroundColor(colorChoosed);
        }
    }

    public void addNote() {
        i = new Intent();
        int id = getIntent().getParcelableExtra(Field.NOTE_OBJECT) != null ? ((Note) getIntent().getParcelableExtra(Field.NOTE_OBJECT)).getId() : -1;

        if (!titleEt.getText().toString().isEmpty() || !titleEt.getText().toString().isEmpty()) {
            d = new Date();

            i.putExtra(Field.POSITION, getIntent().getIntExtra(Field.POSITION, -1));
            i.putExtra(Field.NOTE_OBJECT, new Note.BuilderNote()
                    .set_id(id)
                    .set_specialNote(isSpecial)
                    .set_color(colorChoosed)
                    .set_title(titleEt.getText().toString())
                    .set_description(bodyEt.getText().toString())
                    .set_creationDate(new Date(System.currentTimeMillis()))
                    .set_expirationDate(expirationDate)
                    .set_lastEditDate(new Date(System.currentTimeMillis()))
                    .build());

            setResult(Activity.RESULT_OK, i);
        } else {
            setResult(Activity.RESULT_CANCELED, i);
        }

        finish();
    }

    public void initializeComponent(int flag, Intent intent) {

        if (flag == Field.EDIT) {
            //Note initialization
            Note n = intent.getParcelableExtra(Field.NOTE_OBJECT);

            //Setting for edit
            titleEt.setText(n.getTitle());
            bodyEt.setText(n.getDescription());

            if (n.getExpirationDate().before(new Date(System.currentTimeMillis()))) {
                dateExpiration.setText("Set new date!");
            } else {
                dateExpiration.setText(new SimpleDateFormat("dd-MMM-yy").format(n.getExpirationDate()));
            }

            isSpecial = n.isSpecialNote();
            colorChoosed = n.getColor();
            setColorActivity();
        } else if (getIntent().getAction() != null && getIntent().getAction().equalsIgnoreCase(Intent.ACTION_SEND)) {
            bodyEt.setText(getIntent().getStringExtra(Intent.EXTRA_TEXT));
        }
    }

    @Override
    public void onClick(View v) {
        createCalendar();
    }
}

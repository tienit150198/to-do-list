package com.example.todolist;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AddNoteActivity extends AppCompatActivity {

    private static boolean type = false;
    private static int id = -1, priority = 0;
    Toolbar toolbar;
    TextView tvDate, tvTime, tvPriority;
    ImageView imgDate, imgTime;
    NoteModify instance;
    CheckBox cbCompleted, cbHightlight;
    EditText edtContent;
    LinearLayout lnPickDate, lnPickTime, lnPriority;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        initialization();
        addListener();
        getDataInIntent();
    }

    private void addListener() {
        lnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate();
            }
        });
        lnPickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickTime();
            }
        });
        lnPriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(view);
            }
        });
    }

    // show alert Priority
    private void showAlertDialog(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
        final String[] data = {"High", "Medium", "Low", "None"};
        alert.setTitle("Priority");
        alert.setSingleChoiceItems(data, 3 - priority, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tvPriority.setText(data[i]);
                priority = 3 - i;
                dialogInterface.dismiss();
            }
        });
        alert.show();
    }

    private void initialization() {
        toolbar = findViewById(R.id.tbModify);
        tvDate = findViewById(R.id.tvPickDate);
        tvTime = findViewById(R.id.tvPickTime);
        tvPriority = findViewById(R.id.tvPriority);
        lnPickDate = findViewById(R.id.lnPickDate);
        lnPickTime = findViewById(R.id.lnPickTime);
        lnPriority = findViewById(R.id.lnPriority);
        imgDate = findViewById(R.id.imgPickDate);
        imgTime = findViewById(R.id.imgPickTime);
        cbCompleted = findViewById(R.id.cbCompleted);
        cbHightlight = findViewById(R.id.cbHightLight);
        edtContent = findViewById(R.id.edtContent);

        calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        instance = NoteModify.getInstance(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void getDataInIntent() {
        Intent it = getIntent();

        type = it.getBooleanExtra("type", false);
        if (type) {
            int idIntent = it.getIntExtra("id", -1);

            Note note = NoteModify.getInstance(this).getCursorInId(idIntent);
            id = note.getNodeId();
            edtContent.setText(note.getContent());
            tvDate.setText(Utilities.dateToString(note.getDeadline()));
            tvTime.setText(Utilities.timeToString(note.getDeadline()));

            if (note.isCompleted()) {
                cbCompleted.setChecked(true);
            }

            if (note.isHightlight()) {
                cbHightlight.setChecked(true);
            }
            priority = note.getPriority();
            switch (priority) {
                case 3:
                    tvPriority.setText("High");
                    break;
                case 2:
                    tvPriority.setText("Medium");
                    break;
                case 1:
                    tvPriority.setText("Low");
                    break;
                default:
                    tvPriority.setText("None");
                    break;
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_add, menu);
        return true;
    }

    // get date from DatePickerDialog
    private void pickDate() {
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                DateFormat simpleFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.KOREAN);
                Toast.makeText(AddNoteActivity.this, i2 + "-" + i1 + "-" + i, Toast.LENGTH_SHORT).show();
                calendar.set(i, i1, i2);
                tvDate.setText(simpleFormatter.format(calendar.getTime()));
            }
        }, year, month, date).show();
    }

    // get date from TimePickerDialog
    private void pickTime() {
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                DateFormat simpleFormatter = new SimpleDateFormat("HH:mm:ss", Locale.KOREAN);
                calendar.set(0, 0, 0, i, i1);
                tvTime.setText(simpleFormatter.format(calendar.getTime()));
            }
        }, hour, minute, true).show();
    }

    // get item selected toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idSelected = item.getItemId();
        switch (idSelected) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.itAdd:
                if (!type) {
                    addNote();
                } else {
                    updateNote();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateNote() {
        Note noteUpdate = new Note();
        noteUpdate.setNodeId(id);
        noteUpdate.setCompleted(cbCompleted.isChecked());
        noteUpdate.setHightlight(cbHightlight.isChecked());
        noteUpdate.setContent(edtContent.getText().toString());

        String dateTime = tvDate.getText().toString() + " " + tvTime.getText().toString();
        if (!dateTime.trim().equals("")) {
            noteUpdate.setDeadline(Utilities.stringToDate(dateTime));
        }

        Calendar today = Calendar.getInstance();
        today.clear(Calendar.HOUR);
        today.clear(Calendar.MINUTE);
        today.clear(Calendar.SECOND);
        Date todayDate = today.getTime();

        noteUpdate.setDateCreate(todayDate);
        noteUpdate.setPriority(priority);

        NoteModify.getInstance(this).updateNote(id, noteUpdate);    // call update

        finish();   // finish this activity
        refreshMain();  // refresh main
    }

    private void addNote() {
        Note noteAdd = new Note();
        noteAdd.setCompleted(cbCompleted.isChecked());
        noteAdd.setHightlight(cbHightlight.isChecked());
        noteAdd.setContent(edtContent.getText().toString());


        String dateTime = tvDate.getText().toString() + " " + tvTime.getText().toString();
        if (!dateTime.trim().equals("")) {
            noteAdd.setDeadline(Utilities.stringToDate(dateTime));
        }

        Calendar today = Calendar.getInstance();
        today.clear(Calendar.HOUR);
        today.clear(Calendar.MINUTE);
        today.clear(Calendar.SECOND);
        Date todayDate = today.getTime();

        noteAdd.setDateCreate(todayDate);
        noteAdd.setPriority(priority);

        NoteModify.getInstance(this).insertNote(noteAdd);

        finish();
        refreshMain();
    }

    private void refreshMain() {
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
    }
}

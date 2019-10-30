package com.example.todolist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fabAdd;
    String sql;
    private NoteAdapter noteAdapter;
    private NoteModify instance;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialization();
        addListener();

    }

    private void addListener() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        // set listener when user swiped left or right ==> delete row
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                removeItem((int) viewHolder.itemView.getTag());
            }
        }).attachToRecyclerView(recyclerView);

    }

    private void initialization() {
        // set toolbar main
        Toolbar toolbar = findViewById(R.id.tbMain);
        setSupportActionBar(toolbar);

        fabAdd = findViewById(R.id.fab_add);
        instance = NoteModify.getInstance(this);

        /* init recycler view */
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        noteAdapter = new NoteAdapter(this, instance.getCursorAllNotes());
        recyclerView.setAdapter(noteAdapter);

        //
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.custom_divider);
        dividerItemDecoration.setDrawable(drawable);

        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void removeItem(int id) {
        instance.deleteNote(id);
        refreshView();
    }

    private void addItem() {
        Intent intent = new Intent(this, AddNoteActivity.class);
        intent.putExtra("type", false);
        startActivity(intent);
    }

    public void refreshView() {
        noteAdapter.swapCursor(NoteModify.getInstance(this).getCursorAllNotes());
    }

    // create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idSelected = item.getItemId();
        switch (idSelected) {
            case R.id.itSort:
                showAlertDialog();
                break;
            case R.id.itCheck_completed_all:
                checkCompletedAll();
                break;
            case R.id.itRemove_completed:
                removeAllCompleted();
                break;
            case R.id.itUncheck_completed:
                uncheckCompleted();
                break;
            case R.id.itSetting:
                Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void showAlertDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final String[] data =
                {"Create (newest first)", "Create (oldest first)", "Priority",
                        "Alphabeltical(A to Z)", "Alphabeltical(Z to A)", "Highlighted", "Not Completed"};
        alert.setTitle("Sort list by");
        alert.setSingleChoiceItems(data, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        sql = DatabaseHandler.KEY_DATE_CREATE + " ASC";
                        break;
                    case 1:
                        sql = DatabaseHandler.KEY_DATE_CREATE + " DESC";
                        break;
                    case 2:
                        sql = DatabaseHandler.KEY_PRIORITY + " DESC";
                        break;
                    case 3:
                        sql = DatabaseHandler.KEY_CONTENT + " ASC";
                        break;
                    case 4:
                        sql = DatabaseHandler.KEY_CONTENT + " DESC";
                        break;
                    case 5:
                        sql = DatabaseHandler.KEY_HIGHTLIGHT + " DESC";
                        break;
                    case 6:
                        sql = DatabaseHandler.KEY_COMPLETED + " ASC";
                        break;
                }
                // perform sql
                noteAdapter.swapCursor(NoteModify.getInstance(MainActivity.this).getCursorAllNotesInSort(sql));
                dialogInterface.dismiss();
            }
        });
        alert.show();
    }

    private void uncheckCompleted() {
        Cursor cursor = noteAdapter.getCursor();
        cursor.moveToFirst();
        int i = 0;

        while (!cursor.isAfterLast()) {
            Note note = Utilities.cursorToNote(cursor, i++);
            if (note.isCompleted()) {
                note.setCompleted(!note.isCompleted());
                NoteModify.getInstance(this).updateNote(note.getNodeId(), note);
            }
            cursor.moveToNext();
        }

        cursor.close();
        refreshView();
    }

    private void removeAllCompleted() {
        NoteModify.getInstance(this).deleteNoteCompleted();
        noteAdapter.swapCursor(NoteModify.getInstance(this).getCursorAllNotes());
    }

    private void checkCompletedAll() {
        Cursor cursor = noteAdapter.getCursor();
        int i = 0;
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            Note note = Utilities.cursorToNote(cursor, i++);
            if (!note.isCompleted()) {
                note.setCompleted(!note.isCompleted());
                NoteModify.getInstance(this).updateNote(note.getNodeId(), note);
            }
            cursor.moveToNext();
        }
        cursor.close();
        refreshView();
    }
}

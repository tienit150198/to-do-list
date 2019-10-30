package com.example.todolist;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private static final int UNCOMPLETED = 0;
    private static final int COMPLETED = 1;
    private static final int HIGHTLIGHT = 2;
    private static final int HIGHTLIGHT_COMPETED = 3;
    private Cursor cursor;
    private Context context;

    public NoteAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        setHasStableIds(true);
    }

    public Cursor getCursor() {
        return this.cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    // set type
    @Override
    public int getItemViewType(int position) {
        if (cursor.moveToPosition(position)) {
            int completed = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_COMPLETED));
            int hightlight = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_HIGHTLIGHT));

            if (completed == 1) {
                if (hightlight == 1) {
                    return HIGHTLIGHT_COMPETED;
                }
                return COMPLETED;
            }

            if (hightlight == 1 && completed == 0) {
                return HIGHTLIGHT;
            }

            return UNCOMPLETED;
        }
        return UNCOMPLETED;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_row, parent, false);

        switch (viewType) {
            case UNCOMPLETED:
                view = inflater.inflate(R.layout.item_row, parent, false);
                break;
            case COMPLETED:
                view = inflater.inflate(R.layout.item_row_completed, parent, false);
                break;
            case HIGHTLIGHT:
                view = inflater.inflate(R.layout.item_row_hightlight, parent, false);
                break;
            case HIGHTLIGHT_COMPETED:
                view = inflater.inflate(R.layout.item_row_hightlight_completed, parent, false);
                break;
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }
        String content = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CONTENT));
        Date date = Utilities.stringToDate(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_DEADLINE)));
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_ID));

        holder.tvContent.setText(content);
        holder.tvDate.setText(Utilities.datetimeToString(date));
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return (cursor.getCount());
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null)
            cursor.close();

        cursor = newCursor;
        if (newCursor != null)
            notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent;
        TextView tvDate;
        ImageButton imgIcon;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            tvContent = itemView.findViewById(R.id.tvContent);
            tvDate = itemView.findViewById(R.id.tvDate);
            imgIcon = itemView.findViewById(R.id.imgCheck);

            imgIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    cursor.moveToPosition(position);

                    int id = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_ID));
                    Note note = NoteModify.getInstance(itemView.getContext()).getCursorInId(id);
                    note.setCompleted(!note.isCompleted());

                    NoteModify.getInstance(itemView.getContext()).updateNote(note.getNodeId(), note);
                    // refresh main
                    MainActivity mainActivity = (MainActivity) itemView.getContext();
                    mainActivity.refreshView();
                }
            });

            // edit note when user click item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    cursor.moveToPosition(position);

                    int id = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_ID));
                    Intent intent = new Intent(itemView.getContext(), AddNoteActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("type", true);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}

package com.example.personale.noteexam.controller.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.personale.noteexam.R;
import com.example.personale.noteexam.activities.AddNoteActivity;
import com.example.personale.noteexam.activities.MainActivity;
import com.example.personale.noteexam.controller.list.NoteList;
import com.example.personale.noteexam.controller.utilities.Field;
import com.example.personale.noteexam.model.Note;

/**
 * Created by personale on 13/03/2017.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteVH> {

    private final NoteList listNotes;
    private int position;

    public NoteAdapter(Context c){
        this.listNotes = new NoteList(c);
    }

    private RelativeLayout.LayoutParams setLayoutParam() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.item_title_tv);
        layoutParams.setMargins(6,6,6,6);

        return layoutParams;
    }

    @Override
    public NoteVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NoteVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_main, null));
    }

    @Override
    public void onBindViewHolder(NoteVH holder, int position) {
        Note n = getNoteByPos(position);

        if(!n.getTitle().isEmpty()){
            holder.title.setText(n.getTitle());

            if(n.isSpecialNote())
                holder.title.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        holder.itemView.getContext().getResources().getDrawable(R.drawable.ic_bookmark_black), null);
        }

        if(!n.getDescription().isEmpty()){
            holder.body.setLayoutParams(setLayoutParam());
            holder.body.setText(n.getDescription());
        } else {
            holder.body.setLayoutParams(new RelativeLayout.LayoutParams(0,0));
        }
        holder.cardView.setBackgroundColor(n.getColor());
        holder.cardView.setCardElevation(2);
    }

    @Override
    public int getItemCount() {
        return listNotes.getSize();
    }

    public void operationOnNote(Note note, int position, int requestCode) {
        switch (requestCode){
            case Field.ADD:
                listNotes.add(note);
                notifyItemInserted(0);
                break;
            case Field.EDIT:
                listNotes.edit(position, note);
                notifyItemChanged(position);
                break;
        }
    }

    public void searchNote(String searchedText){
        listNotes.searchIntoDb(searchedText);
        notifyDataSetChanged();
    }

    public void deleteNote(int pos){
        listNotes.delete(pos);
        notifyItemRemoved(pos);
    }

    private Note getNoteByPos(int pos){
        return listNotes.getNote(pos);
    }

    public void setAllNotes() {
        listNotes.loadNoteFromDb();
    }

    public int getPosition() {
        return position;
    }

    public NoteAdapter setPosition(int position) {
        this.position = position;
        return this;
    }

    public void order(int stateOrder) {
        listNotes.orderBy(stateOrder);
        notifyDataSetChanged();
    }

    class NoteVH extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        TextView title, body;
        CardView cardView;

        NoteVH(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.item_title_tv);
            body = (TextView) itemView.findViewById(R.id.item_body_tv);
            cardView = (CardView) itemView.findViewById(R.id.card);

            itemView.setOnClickListener(this);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.item_view:
                    Intent i = new Intent(v.getContext(), AddNoteActivity.class);
                    i.setFlags(Field.EDIT);
                    i.putExtra(Field.POSITION, getAdapterPosition());
                    i.putExtra(Field.NOTE_OBJECT, getNoteByPos(getAdapterPosition()));
                    ((Activity)v.getContext()).startActivityForResult(i, Field.EDIT);
                    break;
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuInflater menuInflater = ((MainActivity)v.getContext()).getMenuInflater();
            menuInflater.inflate(R.menu.menu_item, menu);
            setPosition(getAdapterPosition());
        }
    }

}

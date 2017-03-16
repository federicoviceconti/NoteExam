package com.example.personale.noteexam.controller.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;

/**
 * Created by personale on 13/03/2017.
 */

abstract class SelectableAdapter extends RecyclerView.Adapter<NoteAdapter.NoteVH> {
    private SparseBooleanArray selectedItem;

    SelectableAdapter(){
        selectedItem = new SparseBooleanArray();
    }

    boolean addItem(int pos){
        if(!selectedItem.get(pos)){
            selectedItem.put(pos, true);
            notifyItemChanged(pos);
            return true;
        } else if(selectedItem.size() > 0){
            deleteItem(pos);
            notifyItemChanged(pos);
        } else {
            clearSelection();
        }

        return false;
    }

    protected boolean isItemSelected(int pos) {
        return selectedItem.get(selectedItem.keyAt(pos));
    }

    void deleteItem(int pos){
        if(pos < selectedItem.size() && selectedItem.get(pos)){
            selectedItem.delete(pos);
        }
    }

    int getSelectedSize(){
        return selectedItem.size();
    }

    void clearSelection(){
        selectedItem.clear();
        notifyDataSetChanged();
    }

    int[] getAllSelectedItem(){
        int[] res = new int[getSelectedSize()];

        for(int i = 0; i < getSelectedSize(); i++) {
            res[i] = selectedItem.keyAt(i);
        }

        return res;
    }
}

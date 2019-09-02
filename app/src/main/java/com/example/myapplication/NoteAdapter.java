package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder>   {

     private List<Note> mNotes=new ArrayList<>();
    private List<Note> notes;
    private NoteAdapterListener noteAdapterListener;
    private LayoutInflater mInflater;
    private Context context;




    class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        private NoteAdapterListener noteAdapterListener;
        //private List<Note> notes;
         List<Note> mNotes;
        private TextView textViewLat;
        private TextView textViewLng;
        private TextView textViewPriority;



        public NoteHolder(@NonNull View itemView,NoteAdapterListener noteAdapterListener) {
            super(itemView);

            textViewLat =itemView.findViewById(R.id.text_view_lat);
            textViewLng =itemView.findViewById(R.id.text_view_lng);
            textViewPriority =itemView.findViewById(R.id.text_view_priority);
            itemView.setOnClickListener(this);
            this.noteAdapterListener=noteAdapterListener;
        }

        @Override
        public void onClick(View view) {
            noteAdapterListener.noteAdapterClick(getAdapterPosition());
        }
    }

    public NoteAdapter(Context context,List<Note> myNotes,NoteAdapterListener noteAdapterListener){//


            mNotes = myNotes;
        this.context=context;
        this.noteAdapterListener=noteAdapterListener;
    }



    @Override
    public NoteAdapter.NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,parent,false);
        return new NoteHolder(itemView,noteAdapterListener);
    }



    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note currentNote =mNotes.get(position);
        holder.textViewLat.setText(currentNote.getLat());
        holder.textViewLng.setText(currentNote.getLng());
        holder.textViewPriority.setText(String.valueOf(currentNote.getPriority()));
    }

    @Override
    public int getItemCount() {
        if (mNotes==null)
            return 0;
        return mNotes.size();
    }


    public void setNotes(List<Note> notes){
        this.mNotes =notes;
        notifyDataSetChanged();
    }


    public interface NoteAdapterListener{
        void noteAdapterClick(int position);
    }

}

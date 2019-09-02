package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.NoteAdapterListener {

    private NoteViewModel noteViewModel;
    private  static final int ADD_NOTE_REQUEST=1;
    List<Note> notes;
    NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notes=new ArrayList<>();

        FloatingActionButton buttonAddNote=findViewById(R.id.button_add_note);
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,AddNoteActivity.class);
                startActivityForResult(intent,ADD_NOTE_REQUEST);
            }
        });


        RecyclerView recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter =new NoteAdapter(this,notes,this);
        recyclerView.setAdapter(adapter);


        noteViewModel= ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
               adapter.setNotes(notes);
            }
        });


        adapter.notifyDataSetChanged();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==ADD_NOTE_REQUEST && resultCode==RESULT_OK){
            String lat=data.getStringExtra(AddNoteActivity.EXTRA_LATITUDE);
            String lng=data.getStringExtra(AddNoteActivity.EXTRA_LONGETUDE);
            int priority=data.getIntExtra(AddNoteActivity.EXTRA_PRIORITY,1);

            Note note=new Note(lat,lng,priority);
            noteViewModel.insert(note);

            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Note not saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void noteAdapterClick(int position) {
        Note n=noteViewModel.getAllNotes().getValue().get(position);
        Uri navigationIntent=Uri.parse("google.navigation:q=" + n.getLat() +"," + n.getLng());
        Intent map=new Intent(Intent.ACTION_VIEW,navigationIntent);
        map.setPackage("com.google.android.apps.maps");
        startActivity(map);
    }
}

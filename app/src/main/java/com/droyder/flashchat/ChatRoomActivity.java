package com.droyder.flashchat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatRoomActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseReference databaseReference;
    private RoomListAdapter adapter;
    public static int nextRoomId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        listView= findViewById(R.id.chatroom_list);
    }

    public void addRoom(View v){

        String name = "Room "+(nextRoomId+1);
        InstantRoom room = new InstantRoom(name,LoginActivity.username,nextRoomId);

        databaseReference.child("rooms").push().setValue(room);

        InstantMessage msg = new InstantMessage("Welcome to the New ChatRoom : "+name,"Droyder");
        databaseReference.child(String.valueOf(nextRoomId)).push().setValue(msg);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter = new RoomListAdapter(this,databaseReference);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                nextRoomId = listView.getCount();

                Log.d("FlashChat", "next Room ID : "+nextRoomId +" "+position);
                Intent i = new Intent(ChatRoomActivity.this,MainChatActivity.class);
                i.putExtra("ID",Integer.toString(position));
                finish();
                startActivity(i);
            }
        });
//        Log.d("FlashChat", "nextId: "+nextRoomId );

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.cleanup();
    }
}

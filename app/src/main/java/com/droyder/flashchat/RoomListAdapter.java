package com.droyder.flashchat;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class RoomListAdapter extends BaseAdapter {

    private Activity activity;
    private DatabaseReference databaseReference;
    private ArrayList<DataSnapshot> snapshotArrayList;

    private ChildEventListener listener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            snapshotArrayList.add(dataSnapshot);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    RoomListAdapter(Activity activity, DatabaseReference databaseReference) {
        this.activity = activity;
        this.databaseReference = databaseReference.child("rooms");
        this.databaseReference.addChildEventListener(listener);
        this.snapshotArrayList = new ArrayList<>();
    }

    static class ViewHolder{
        TextView room, creator;
        LinearLayout.LayoutParams params;
    }

    @Override
    public int getCount() {
        ChatRoomActivity.nextRoomId = snapshotArrayList.size();
        return snapshotArrayList.size();
    }

    @Override
    public InstantRoom getItem(int position) {
        return snapshotArrayList.get(position).getValue(InstantRoom.class) ;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.chat_room_row,parent,false);

            final ViewHolder holder = new ViewHolder();
            holder.room= convertView.findViewById(R.id.room_view);
            holder.creator=convertView.findViewById(R.id.craetor_text);
            holder.params = (LinearLayout.LayoutParams) holder.room.getLayoutParams();
            convertView.setTag(holder);
        }

        final InstantRoom room = getItem(position);
        final ViewHolder holder = (ViewHolder) convertView.getTag();

        holder.room.setText(room.getName());
        holder.room.setLayoutParams(holder.params);

        holder.creator.setText(room.getCreator());
        Log.d("FLashChat", (String) holder.room.getText());
        return convertView;
    }

    void cleanup(){
        Log.d("FLashChat","Cleanup done");
        databaseReference.removeEventListener(listener);
    }
}

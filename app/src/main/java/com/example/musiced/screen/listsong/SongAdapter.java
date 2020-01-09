package com.example.musiced.screen.listsong;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.musiced.R;
import com.example.musiced.data.model.Song;
import com.example.musiced.utils.navigator.IOnClickListeners;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {

    public IOnClickListeners mListeners;
    private List<Song> mListsong;
    private Context mContext;

    public SongAdapter(List<Song> listsong, Context context) {
        this.mListsong = listsong;
        this.mContext = context;
        if (mContext instanceof IOnClickListeners) {
            this.mListeners = (IOnClickListeners) mContext;
        }
    }

    public void setOnclick(IOnClickListeners<Song> listeners) {
        mListeners = listeners;

    }

    public void setListSong(List<Song> listSong) {
        this.mListsong = listSong;
        notifyDataSetChanged();

    }

    public Song getPosition(int posi) {
        return mListsong.get(posi);

    }

    @NonNull
    @Override

    public SongAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycleview_song, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SongAdapter.MyViewHolder myViewHolder, final int i) {
        final Song song = mListsong.get(i);
        myViewHolder.tvNameSong.setText(song.getSongName());
        myViewHolder.tvNameSinger.setText(song.getSingerName());

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mListeners.onClick(song, getPosition(i));
                mListeners.onClick(song);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListsong == null ? 0 : mListsong.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvNameSong, tvNameSinger;
        CircleImageView ImageSong;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ImageSong = itemView.findViewById(R.id.image_song);
            tvNameSong = itemView.findViewById(R.id.name_song);
            tvNameSinger = itemView.findViewById(R.id.name_singer);
        }
    }
}

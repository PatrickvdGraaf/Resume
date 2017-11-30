package nl.graaf.patricksresume.views.projects.flashchat.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import nl.graaf.patricksresume.R;
import nl.graaf.patricksresume.views.projects.flashchat.models.InstantMessage;

/**
 * Created by Patrick van de Graaf on 11/30/2017.
 */

public class ChatListAdapter extends BaseAdapter{

    private Activity mActivity;
    private DatabaseReference mDatabaseReference;
    private String mDisplayName;
    private ArrayList<DataSnapshot> mSnapShotList;

    public ChatListAdapter(Activity activity, DatabaseReference ref, String name) {
        this.mActivity = activity;
        //TODO Change child name to static String
        this.mDatabaseReference = ref.child("messages");
        this.mDisplayName = name;
        mSnapShotList = new ArrayList<>();
    }

    static class ChatViewHolder {
        TextView authorName;
        TextView body;
        LinearLayout.LayoutParams params;

        ChatViewHolder(View view) {
            authorName = view.findViewById(R.id.author);
            body = view.findViewById(R.id.message);
            params = (LinearLayout.LayoutParams) authorName.getLayoutParams();
        }
    }

    @Override
    public int getCount() {
        return mSnapShotList.size();
    }

    @Override
    public InstantMessage getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            LayoutInflater inflater = mActivity.getLayoutInflater();
            view = inflater.inflate(R.layout.list_row_chat_msg, viewGroup, false);
            final ChatViewHolder holder = new ChatViewHolder(view);
            view.setTag(holder);
        }

        final InstantMessage message = getItem(i);
        final ChatViewHolder holder = (ChatViewHolder) view.getTag();

        String author = message.getAuthor();
        holder.authorName.setText(author);

        String msg = message.getMessage();
        holder.body.setText(msg);
        return view;
    }
}

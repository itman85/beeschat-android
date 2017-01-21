package com.omebee.android.beeschat.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omebee.android.beeschat.R;
import com.omebee.android.beeschat.itemdata.RecentItemData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phannguyen on 9/18/16.
 */
public class RecentMsgAdapter extends BaseAdapter {
    private List<RecentItemData> recentMessages;
    private LayoutInflater layoutInflater;

    public RecentMsgAdapter(Activity activity) {
        layoutInflater = activity.getLayoutInflater();
        recentMessages = new ArrayList<RecentItemData>();
    }

    public void addLastMessage(RecentItemData recentMessage) {
        recentMessages.add(recentMessage);
        notifyDataSetChanged();
    }

    public void addLastMessage(List<RecentItemData> recentMessagesList) {
        recentMessages.addAll(recentMessagesList);
        notifyDataSetChanged();
    }

    public void addOrUpdateAdapter(RecentItemData recentMessage) {
        boolean isUpdated = false;
        for(RecentItemData recentItem: recentMessages){
            if(recentItem.getRecentMessageId().equals(recentMessage.getRecentMessageId())){
                recentItem.updateData(recentMessage);
                isUpdated = true;
                notifyDataSetChanged();
                return;
            }
        }

        if(!isUpdated){
            recentMessages.add(recentMessage);
            notifyDataSetChanged();
        }

    }


    @Override
    public int getCount() {
        return recentMessages.size();
    }

    @Override
    public Object getItem(int i) {
        return recentMessages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }



    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        RecentItemData data = (RecentItemData) getItem(i);


        //show message on left or right, depending on if
        //it's incoming or outgoing
        if (convertView == null) {
            int res = R.layout.recent_message_item;
            convertView = layoutInflater.inflate(res, viewGroup, false);
        }

        TextView txtLastMessage = (TextView) convertView.findViewById(R.id.lastMessage);
        txtLastMessage.setText(data.getLastMessage());

        TextView txtUserName = (TextView) convertView.findViewById(R.id.userName);
        txtUserName.setText(data.getUserName());

        return convertView;
    }
}

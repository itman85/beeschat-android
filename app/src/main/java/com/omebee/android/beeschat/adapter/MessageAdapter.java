package com.omebee.android.beeschat.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omebee.android.beeschat.R;
import com.omebee.android.beeschat.itemdata.MessageItemData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phannguyen on 9/12/16.
 */
public class MessageAdapter extends BaseAdapter{

    private List<MessageItemData> messages;
    private LayoutInflater layoutInflater;

    public MessageAdapter(Activity activity) {
        layoutInflater = activity.getLayoutInflater();
        messages = new ArrayList<MessageItemData>();
    }

    public void addLastMessage(MessageItemData message,boolean isNotifyChange) {
        messages.add(message);
        if(isNotifyChange)
            notifyDataSetChanged();
    }

    public void addLastMessage(List<MessageItemData> messagesList,boolean isNotifyChange) {
        messages.addAll(messagesList);
        if(isNotifyChange)
            notifyDataSetChanged();
    }

    public void addFirstMessage(List<MessageItemData> messagesList,boolean isNotifyChange) {
        messages.addAll(0,messagesList);
        if(isNotifyChange)
            notifyDataSetChanged();
    }

    public void addLastMessage(MessageItemData message) {
        messages.add(message);
        notifyDataSetChanged();
    }

    public void addLastMessage(List<MessageItemData> messagesList) {
        messages.addAll(messagesList);
        notifyDataSetChanged();
    }

    public void addFirstMessage(List<MessageItemData> messagesList) {
        messages.addAll(0,messagesList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int i) {
        return messages.get(i).getDirection();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageItemData data = (MessageItemData) getItem(i);
        int direction = data.getDirection();

        //show message on left or right, depending on if
        //it's incoming or outgoing
        if (convertView == null) {
            int res = 0;
            if (direction == MessageItemData.DIRECTION_INCOMING) {
                res = R.layout.message_left;
            } else if (direction == MessageItemData.DIRECTION_OUTGOING) {
                res = R.layout.message_right;
            }
            convertView = layoutInflater.inflate(res, viewGroup, false);
        }

        TextView txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
        txtMessage.setText(data.getTextBody());

        return convertView;
    }
}

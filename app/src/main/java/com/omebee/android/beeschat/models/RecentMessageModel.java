package com.omebee.android.beeschat.models;

import com.google.firebase.database.DataSnapshot;
import com.omebee.android.beeschat.firebase.FirebaseConstant;
import com.omebee.android.beeschat.models.base.BaseModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by phannguyen on 9/14/16.
 */
public class RecentMessageModel implements BaseModel {
    private String conversationId;
    private String lastMessage;
    private int unreadCount;
    private long lastMessageDate;
    private String lastMessageUserId;
    private String recentMessageId;

    public RecentMessageModel() {
    }

    public RecentMessageModel(String conversationId, String lastMessage, int unreadCount, long lastMessageDate, String lastMessageUserId, String recentMessageId) {
        this.conversationId = conversationId;
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
        this.lastMessageDate = lastMessageDate;
        this.lastMessageUserId = lastMessageUserId;
        this.recentMessageId = recentMessageId;
    }

    public RecentMessageModel(MessageModel messageModel) {
        if(messageModel!=null){
            this.conversationId = messageModel.getConversationId();
            this.lastMessage = messageModel.getMessageBody();
            this.unreadCount = 0;
            this.lastMessageDate = messageModel.getMessageCreatedDate();
            this.lastMessageUserId = messageModel.getSenderId();
            this.recentMessageId = messageModel.getConversationId();
        }
    }
    public RecentMessageModel(DataSnapshot snapshot) throws Exception {
        try {
            this.conversationId = snapshot.child(FirebaseConstant.CONVERSATION_ID_FIELD).getValue(String.class);
            this.lastMessage = snapshot.child(FirebaseConstant.LAST_MESSAGE_FIELD).getValue(String.class);
            this.unreadCount = snapshot.child(FirebaseConstant.UNREAD_COUNT_FIELD).getValue(Integer.class);
            this.lastMessageDate = snapshot.child(FirebaseConstant.MESSAGE_CREATED_DATE_FIELD).getValue(Long.class);
            this.lastMessageUserId = snapshot.child(FirebaseConstant.LAST_MESSAGE_USER_ID_FIELD).getValue(String.class);
            this.recentMessageId = snapshot.child(FirebaseConstant.RECENT_MESSAGE_ID_FIELD).getValue(String.class);
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseConstant.LAST_MESSAGE_FIELD, lastMessage);
        result.put(FirebaseConstant.LAST_MESSAGE_USER_ID_FIELD, lastMessageUserId);
        result.put(FirebaseConstant.MESSAGE_CREATED_DATE_FIELD, lastMessageDate);
        result.put(FirebaseConstant.UNREAD_COUNT_FIELD, unreadCount);
        result.put(FirebaseConstant.CONVERSATION_ID_FIELD, conversationId);
        result.put(FirebaseConstant.RECENT_MESSAGE_ID_FIELD, recentMessageId);
        return result;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public long getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(long lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public String getLastMessageUserId() {
        return lastMessageUserId;
    }

    public void setLastMessageUserId(String lastMessageUserId) {
        this.lastMessageUserId = lastMessageUserId;
    }

    public String getRecentMessageId() {
        return recentMessageId;
    }

    public void setRecentMessageId(String recentMessageId) {
        this.recentMessageId = recentMessageId;
    }
}

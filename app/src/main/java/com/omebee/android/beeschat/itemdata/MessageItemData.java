package com.omebee.android.beeschat.itemdata;

import com.omebee.android.beeschat.firebase.FirebaseConstant;

/**
 * Created by phannguyen on 9/12/16.
 */
public class MessageItemData {

    public static final int DIRECTION_INCOMING = 0;
    public static final int DIRECTION_OUTGOING = 1;

    private String messageId;
    private String textBody;
    private String senderId;
    private int direction;
    private long createdTime;
    private String conversationId;
    private int messageStatus;
    private FirebaseConstant.MessageType messageType;


    public MessageItemData() {

    }

    public MessageItemData(String messageId, String textBody, String senderId, int direction) {
        this.messageId = messageId;
        this.textBody = textBody;
        this.senderId = senderId;
        this.direction = direction;
    }

    public MessageItemData(String senderId, String textBody, int direction) {
        this();
        this.senderId = senderId;
        this.setTextBody(textBody);
        this.direction = direction;
    }



    public void setTextBody(String textBody) {
        if(textBody == null) {
            throw new IllegalArgumentException("Must have non-null textBody.");
        } else {
            this.textBody = textBody;
        }
    }

    public String getTextBody() {
        return this.textBody;
    }

    public String getMessageId() {
        return this.messageId;
    }

    public int getDirection() {
        return direction;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    public FirebaseConstant.MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(FirebaseConstant.MessageType messageType) {
        this.messageType = messageType;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}

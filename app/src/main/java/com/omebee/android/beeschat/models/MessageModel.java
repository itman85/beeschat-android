package com.omebee.android.beeschat.models;

import com.google.firebase.database.DataSnapshot;
import com.omebee.android.beeschat.itemdata.MessageItemData;
import com.omebee.android.beeschat.firebase.FirebaseConstant;
import com.omebee.android.beeschat.firebase.FirebaseHelper;
import com.omebee.android.beeschat.models.base.BaseModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by phannguyen on 9/14/16.
 */
public class MessageModel implements BaseModel {

    private String conversationId;
    private String messageBody;
    private String messageId;
    private String senderId;
    private int messageStatus;
    private long messageCreatedDate;
    private FirebaseConstant.MessageType messageType;

    public MessageModel() {
    }

    public MessageModel(String conversationId, String messageBody, String messageId, String senderId, int messageStatus,
                        long messageCreatedDate, FirebaseConstant.MessageType messageType) {
        this.conversationId = conversationId;
        this.messageBody = messageBody;
        this.messageId = messageId;
        this.senderId = senderId;
        this.messageStatus = messageStatus;
        this.messageCreatedDate = messageCreatedDate;
        this.messageType = messageType;
    }

    public MessageModel(DataSnapshot snapshot) throws Exception {
        try {
            this.conversationId = snapshot.child(FirebaseConstant.CONVERSATION_ID_FIELD).getValue(String.class);
            this.messageBody = snapshot.child(FirebaseConstant.MESSAGE_BODY_FIELD).getValue(String.class);
            this.messageId = snapshot.child(FirebaseConstant.MESSAGE_ID_FIELD).getValue(String.class);
            this.senderId = snapshot.child(FirebaseConstant.SENDER_ID_FIELD).getValue(String.class);
            this.messageStatus = snapshot.child(FirebaseConstant.MESSAGE_STATUS_FIELD).getValue(Integer.class);
            this.messageCreatedDate = snapshot.child(FirebaseConstant.MESSAGE_CREATED_DATE_FIELD).getValue(Long.class);
            this.messageType = FirebaseConstant.MessageType.getEnum(snapshot.child(FirebaseConstant.CONVERSATION_ID_FIELD).getValue(String.class));
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }

    public MessageModel(MessageItemData data) {
        if(data !=null){
            this.conversationId = data.getConversationId();
            this.messageBody = data.getTextBody();
            //this.messageId = messageId;
            this.senderId = data.getSenderId();
            this.messageStatus = data.getMessageStatus();
            this.messageCreatedDate = data.getCreatedTime();
            this.messageType = data.getMessageType();
        }
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseConstant.MESSAGE_BODY_FIELD, messageBody);
        result.put(FirebaseConstant.MESSAGE_CREATED_DATE_FIELD, messageCreatedDate);
        result.put(FirebaseConstant.SENDER_ID_FIELD, senderId);
        result.put(FirebaseConstant.MESSAGE_STATUS_FIELD, messageStatus);
        result.put(FirebaseConstant.MESSAGE_TYPE_FIELD, messageType.toString());
        result.put(FirebaseConstant.CONVERSATION_ID_FIELD, conversationId);
        result.put(FirebaseConstant.MESSAGE_ID_FIELD, messageId);
        return result;
    }


    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    public long getMessageCreatedDate() {
        return messageCreatedDate;
    }

    public void setMessageCreatedDate(long messageCreatedDate) {
        this.messageCreatedDate = messageCreatedDate;
    }

    public FirebaseConstant.MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(FirebaseConstant.MessageType messageType) {
        this.messageType = messageType;
    }

    public MessageItemData convertToData(){
        int direction = MessageItemData.DIRECTION_OUTGOING;
        if(!senderId.equals(FirebaseHelper.Instance().getCurrentUserId()))
            direction = MessageItemData.DIRECTION_INCOMING;
        MessageItemData data = new MessageItemData(this.messageId,this.messageBody,this.senderId,direction);
        return data;
    }
}

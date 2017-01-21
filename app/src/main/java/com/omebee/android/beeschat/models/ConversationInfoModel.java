package com.omebee.android.beeschat.models;

import com.google.firebase.database.DataSnapshot;
import com.omebee.android.beeschat.firebase.FirebaseConstant;
import com.omebee.android.beeschat.models.base.BaseModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by phannguyen on 9/14/16.
 */
public class ConversationInfoModel implements BaseModel {
    private List<String> members;
    private String conversationTitle;
    private long conversationCreatedDate;
    private String  conversationCreatedUserId;
    private String conversationId;

    public ConversationInfoModel() {
    }

    public ConversationInfoModel(List<String> members, String conversationTitle, long conversationCreatedDate, String conversationCreatedUserId, String conversationId) {
        this.members = members;
        this.conversationTitle = conversationTitle;
        this.conversationCreatedDate = conversationCreatedDate;
        this.conversationCreatedUserId = conversationCreatedUserId;
        this.conversationId = conversationId;
    }

    public ConversationInfoModel(DataSnapshot snapshot) throws Exception {
        try {
            this.conversationId = snapshot.child(FirebaseConstant.CONVERSATION_ID_FIELD).getValue(String.class);
            this.members = (List<String>) snapshot.child(FirebaseConstant.CONVERSATION_MEMBERS_FIELD).getValue();
            this.conversationTitle = snapshot.child(FirebaseConstant.CONVERSATION_TITLE_FIELD).getValue(String.class);
            this.conversationCreatedDate = snapshot.child(FirebaseConstant.CONVERSATION_CREATED_DATE_FIELD).getValue(Long.class);
            this.conversationCreatedUserId = snapshot.child(FirebaseConstant.CONVERSATION_CREATED_USER_ID_FIELD).getValue(String.class);
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseConstant.CONVERSATION_TITLE_FIELD, conversationTitle);
        result.put(FirebaseConstant.CONVERSATION_CREATED_DATE_FIELD, conversationCreatedDate);
        result.put(FirebaseConstant.CONVERSATION_CREATED_USER_ID_FIELD, conversationCreatedUserId);
        result.put(FirebaseConstant.CONVERSATION_MEMBERS_FIELD, members);
        result.put(FirebaseConstant.CONVERSATION_ID_FIELD, conversationId);

        return result;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getConversationTitle() {
        return conversationTitle;
    }

    public void setConversationTitle(String conversationTitle) {
        this.conversationTitle = conversationTitle;
    }

    public long getConversationCreatedDate() {
        return conversationCreatedDate;
    }

    public void setConversationCreatedDate(long conversationCreatedDate) {
        this.conversationCreatedDate = conversationCreatedDate;
    }

    public String getConversationCreatedUserId() {
        return conversationCreatedUserId;
    }

    public void setConversationCreatedUserId(String conversationCreatedUserId) {
        this.conversationCreatedUserId = conversationCreatedUserId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getRecepientIdInSingleConversation(String currentUserId){
        if(members!=null && members.size()>0){
            for(String member:members){
                if(!member.equals(currentUserId)){
                    return member;
                }
            }
        }
        return null;
    }
}

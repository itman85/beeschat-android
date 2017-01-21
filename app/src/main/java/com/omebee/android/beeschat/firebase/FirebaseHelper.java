package com.omebee.android.beeschat.firebase;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omebee.android.beeschat.models.ConversationInfoModel;
import com.omebee.android.beeschat.models.MessageModel;
import com.omebee.android.beeschat.models.RecentMessageModel;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by phannguyen on 9/14/16.
 */
public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";
    private static FirebaseHelper instance;
    private static Object lock = new Object();
    private static String currentUserId;
    DatabaseReference fbDatabase = FirebaseDatabase.getInstance().getReference();

    public static FirebaseHelper Instance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new FirebaseHelper();
            }
        }
        return instance;
    }

    public  FirebaseHelper(){
        if(ParseUser.getCurrentUser()!=null)
            currentUserId = ParseUser.getCurrentUser().getObjectId();
    }

    public String getIdOfSingleConversation(String userId, String withUserId){
        int comp = userId.compareTo(withUserId);
        String conversationId = "";
        if(comp==1)
            conversationId = userId+ "-"+withUserId;
        else
            conversationId = withUserId+ "-"+userId;

        return conversationId;
    }

    public void createConversionMessage(MessageModel messageModel){
        DatabaseReference messageRef = fbDatabase.child(FirebaseConstant.CONVERSATIONS_MESSAGES_NODE).child(messageModel.getConversationId()).push();
        messageModel.setMessageId(messageRef.getKey());
        messageRef.setValue(messageModel.toMap(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError!=null)
                    Log.i(TAG, "save message error "+databaseError.getMessage());
                else
                    Log.i(TAG, "save message complete ");
            }
        });
        /*
        messageRef.updateChildren(messageModel.toMap(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.i(TAG, "save message compelete"+databaseError.getMessage());
            }
        });*/

    }

    public void createConversationInfo(ConversationInfoModel conversationInfoModel){
        Log.i(TAG, "Create conversation info for conversation id "+conversationInfoModel.getConversationId());
        Map<String, Object> childUpdates = new HashMap<>();
        String nodePath  = String.format("/%s/%s",FirebaseConstant.CONVERSATIONS_INFO_NODE,conversationInfoModel.getConversationId());
        childUpdates.put(nodePath, conversationInfoModel.toMap());

        fbDatabase.updateChildren(childUpdates);
        /*DatabaseReference conversationInfoRef = fbDatabase.child(FirebaseConstant.CONVERSATIONS_INFO_NODE).child(conversationInfoModel.getConversationId()).push();
        conversationInfoRef.setValue(conversationInfoModel.toMap(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.i(TAG, "save conversation info compelete"+databaseError.getMessage());
            }
        });*/

    }

    public void createRecentMessageForConversationsMembers(RecentMessageModel recentMessageModel, String senderId, List<String> members){
        //quick create or update recent message for sender
        createOrUpdateRecentMessageItem(recentMessageModel,senderId);
        //create or update for other members
        for(String other : members){
            if(!other.equals(senderId)){
                retrieveAndUpdateRecentMessageOfOther(recentMessageModel,other);
            }
        }
    }

    public void retrieveAndUpdateRecentMessageOfOther(final RecentMessageModel recentMessageModel, final String otherId){
        //find recent child of other user id has this conversation id
        fbDatabase.child(FirebaseConstant.RECENT_MESSAGES_NODE)
                .child(otherId).child(recentMessageModel.getConversationId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            RecentMessageModel currentRecentMessageModel = null;
                            Log.i(TAG, "Retrieve recent message for user id " + otherId);
                            try {
                                currentRecentMessageModel = new RecentMessageModel(dataSnapshot);
                            } catch (Exception e) {
                                e.printStackTrace();
                                currentRecentMessageModel = null;
                            }

                            if (currentRecentMessageModel != null) {
                                Log.i(TAG, "Update unread count for recent message of user id " + otherId);
                                recentMessageModel.setUnreadCount(currentRecentMessageModel.getUnreadCount() + 1);//inscrease unread message count
                            }
                        }
                        createOrUpdateRecentMessageItem(recentMessageModel,otherId);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    public void createOrUpdateRecentMessageItem(RecentMessageModel recentMessageModel,String ofUserId){
        Log.i(TAG, "Create or update recent message for user id "+ofUserId);
        Map<String, Object> childUpdates = new HashMap<>();
        String nodePath  = String.format("/%s/%s/%s",FirebaseConstant.RECENT_MESSAGES_NODE,ofUserId,recentMessageModel.getConversationId());
        childUpdates.put(nodePath, recentMessageModel.toMap());

        fbDatabase.updateChildren(childUpdates);

        /*DatabaseReference recentMessageRef = fbDatabase.child(FirebaseConstant.RECENT_MESSAGES_NODE)
                .child(ofUserId)
                .child(recentMessageModel.getConversationId()).push();
        recentMessageModel.setRecentMessageId(recentMessageRef.getKey());
        recentMessageRef.setValue(recentMessageModel.toMap(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.i(TAG, "save recent message info compelete"+databaseError.getMessage());
            }
        });*/
    }

    public DatabaseReference getFbDatabase() {
        return fbDatabase;
    }

    public String getCurrentUserId(){
        if(currentUserId==null){
            if(ParseUser.getCurrentUser()!=null)
                currentUserId = ParseUser.getCurrentUser().getObjectId();
        }
        return currentUserId;

    }

    public void logout(){
        ParseUser.logOut();
        currentUserId = null;
    }

}

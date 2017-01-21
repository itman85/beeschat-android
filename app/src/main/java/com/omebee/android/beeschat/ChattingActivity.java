package com.omebee.android.beeschat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.omebee.android.beeschat.adapter.MessageAdapter;
import com.omebee.android.beeschat.firebase.FirebaseConstant;
import com.omebee.android.beeschat.firebase.FirebaseHelper;
import com.omebee.android.beeschat.itemdata.MessageItemData;
import com.omebee.android.beeschat.models.ConversationInfoModel;
import com.omebee.android.beeschat.models.MessageModel;
import com.omebee.android.beeschat.models.RecentMessageModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by phannguyen on 9/12/16.
 */
public class ChattingActivity extends AppCompatActivity {

    private static final String TAG = "ChattingActivity" ;
    private ListView messagesList;
    private MessageAdapter messageAdapter;
    private EditText messageBodyField;
    private String messageBody;

    private String recipientId;
    private String currentUserId;
    private String conversationId;
    DatabaseReference thisConversationRef;
    ChildEventListener conversationChildListener;
    private boolean isCreatedConversationInfo= false;
    private String currentOldestMessageKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chatting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        messagesList = (ListView) findViewById(R.id.listMessages);
        messageAdapter = new MessageAdapter(this);
        messagesList.setAdapter(messageAdapter);
        buildMessageListOnScrollListener();

        messageBodyField = (EditText) findViewById(R.id.messageBodyField);

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        Intent intent = getIntent();
        recipientId = intent.getStringExtra("RECIPIENT_ID");
        currentUserId = FirebaseHelper.Instance().getCurrentUserId();
        getSupportActionBar().setTitle(intent.getStringExtra("RECIPIENT_NAME"));
        Log.i(TAG,currentUserId + " is chatting with " + recipientId);
        conversationId = FirebaseHelper.Instance().getIdOfSingleConversation(currentUserId,recipientId);
        thisConversationRef = FirebaseHelper.Instance().getFbDatabase().child(FirebaseConstant.CONVERSATIONS_MESSAGES_NODE)
                .child(conversationId);
        retrieveConversationInfo(conversationId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadConversationMessagesAtFirst(conversationId);
        buildConversationChildListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startListeningNewMessage(conversationId);

    }

    @Override
    protected void onPause() {
        super.onPause();
        thisConversationRef.removeEventListener(conversationChildListener);
    }

    int mScrollState = 0;
    boolean mIsLoadingMore = false;
    private int visibleThreshold = 1;
    public void onLoadMoreComplete() {
        mIsLoadingMore = false;
    }

    private void buildMessageListOnScrollListener(){
        messagesList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                //Log.i(TAG,"scrollState "+scrollState);
                mScrollState = scrollState;
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //Log.e(TAG,"onScroll START");
                if(!mIsLoadingMore && mScrollState == SCROLL_STATE_TOUCH_SCROLL && firstVisibleItem<visibleThreshold){
                    Log.e(TAG,"Load more message now");
                    mIsLoadingMore = true;
                    //loadMoreConversationMessageFromKey(currentOldestMessageKey);

                }
                /*Log.i(TAG,"firstVisibleItem "+firstVisibleItem);
                Log.i(TAG,"visibleItemCount "+visibleItemCount);
                Log.i(TAG,"totalItemCount "+totalItemCount);
                Log.e(TAG,"onScroll END");*/
            }
        });
    }

    private void retainListViewPositionAfterLoadMore(List<MessageItemData> messageItemDatas){
        //int lastViewedPosition = messagesList.getFirstVisiblePosition();
        //get offset of first visible view
        //View v = messagesList.getChildAt(0);
        //int topOffset = (v == null) ? 0 : v.getTop();
        messageAdapter.addFirstMessage(messageItemDatas);
        //messagesList.smoothScrollToPosition(messageItemDatas.size());

    }
    private void sendMessage() {
        messageBody = messageBodyField.getText().toString();
        if (messageBody.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_LONG).show();
            return;
        }

        messageBodyField.setText("");
        MessageItemData msgData = createOutGoingMessage(messageBody);
        //messageAdapter.addLastMessage(msg);
        MessageModel messageModel = new MessageModel(msgData);
        RecentMessageModel recentMessageModel = new RecentMessageModel(messageModel);
        FirebaseHelper.Instance().createConversionMessage(messageModel);
        FirebaseHelper.Instance().createRecentMessageForConversationsMembers(recentMessageModel,currentUserId, Arrays.asList(currentUserId,recipientId));
        if(!isCreatedConversationInfo){
            ConversationInfoModel infoModel = new ConversationInfoModel( Arrays.asList(currentUserId,recipientId),"No title",System.currentTimeMillis(),
                    currentUserId,conversationId);
            FirebaseHelper.Instance().createConversationInfo(infoModel);
            isCreatedConversationInfo = true;

        }

    }

    private MessageItemData createOutGoingMessage(String message){
        MessageItemData msgData = new MessageItemData();
        msgData.setConversationId(conversationId);
        msgData.setCreatedTime(System.currentTimeMillis());
        msgData.setMessageStatus(FirebaseConstant.MessageStatus.Sent.getValue());
        msgData.setTextBody(message);
        msgData.setMessageType(FirebaseConstant.MessageType.Text);
        msgData.setSenderId(currentUserId);
        msgData.setDirection(MessageItemData.DIRECTION_OUTGOING);

        return msgData;
    }



    private void loadConversationMessagesAtFirst(final String conversationId){
        Query conversationQuery = FirebaseHelper.Instance().getFbDatabase().child(FirebaseConstant.CONVERSATIONS_MESSAGES_NODE)
                .child(conversationId).orderByKey().limitToLast(5);//load 15 latest messages
        conversationQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Log.i(TAG,"Load message " + dataSnapshot.getChildrenCount() + " at first for conversation id "+conversationId);
                    final List<MessageItemData> messageDatas = new ArrayList<MessageItemData>();
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        MessageModel messageModel = null;
                        try {
                            messageModel = new MessageModel(messageSnapshot);
                        } catch (Exception e) {
                            e.printStackTrace();
                            messageModel = null;
                        }
                        if (messageModel != null) {
                            MessageItemData messageData = messageModel.convertToData();
                            messageDatas.add(messageData);
                        }
                    }
                    if (messageDatas.size() > 0) {
                        Log.i(TAG, "Add message adapter and reload message layout");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                messageAdapter.addLastMessage(messageDatas);
                                messagesList.smoothScrollToPosition(messageDatas.size());
                            }
                        });

                        currentOldestMessageKey = messageDatas.get(0).getMessageId();
                        Log.i(TAG, "currentOldestMessageKey:"+currentOldestMessageKey);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadMoreConversationMessageFromKey(String lastMessageKey){
        if(lastMessageKey == null || "".equals(lastMessageKey)) {
            onLoadMoreComplete();
            return;
        }

        Query conversationQuery = FirebaseHelper.Instance().getFbDatabase().child(FirebaseConstant.CONVERSATIONS_MESSAGES_NODE)
                .child(conversationId).orderByKey().endAt(lastMessageKey).limitToLast(6);//load 15 older messages
        conversationQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Log.i(TAG,"Load more message " + dataSnapshot.getChildrenCount() + " for conversation id "+conversationId);
                    final List<MessageItemData> messageDatas = new ArrayList<MessageItemData>();
                    int i = 0;
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        i++;
                        if(i == dataSnapshot.getChildrenCount()){//skip last child, because it duplicate the last message
                            break;
                        }
                        MessageModel messageModel = null;
                        try {
                            messageModel = new MessageModel(messageSnapshot);
                        } catch (Exception e) {
                            e.printStackTrace();
                            messageModel = null;
                        }
                        if (messageModel != null) {
                            MessageItemData messageData = messageModel.convertToData();
                            messageDatas.add(messageData);
                        }

                    }
                    if (messageDatas.size() > 0) {
                        Log.i(TAG, "Add first message adapter and reload message layout");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                retainListViewPositionAfterLoadMore(messageDatas);
                            }
                        });
                        currentOldestMessageKey = messageDatas.get(0).getMessageId();
                        Log.i(TAG, "currentOldestMessageKey:"+currentOldestMessageKey);
                    }

                }
                onLoadMoreComplete();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onLoadMoreComplete();
            }
        });
    }

    private void startListeningNewMessage(String conversationId){
        Log.i(TAG,"Start listening new message from this moment on conversation id "+conversationId);
        thisConversationRef.orderByChild(FirebaseConstant.MESSAGE_CREATED_DATE_FIELD).startAt(System.currentTimeMillis()).addChildEventListener(conversationChildListener);
    }

    private void buildConversationChildListener(){
        conversationChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //new message added
                Log.i(TAG,"Incomming new message on conversation id "+conversationId);
                MessageModel messageModel = null;
                try {
                    messageModel = new MessageModel(dataSnapshot);
                } catch (Exception e) {
                    e.printStackTrace();
                    messageModel = null;
                }
                if(messageModel!=null){
                    MessageItemData messageData = messageModel.convertToData();
                    messageAdapter.addLastMessage(messageData);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void retrieveConversationInfo(final String conversationId){
        /*FirebaseHelper.Instance().getFbDatabase().child(FirebaseConstant.CONVERSATIONS_INFO_NODE)
                .orderByKey().equalTo(conversationId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Log.i(TAG, "Retrieve conversation info for conversation id " + conversationId);
                    isCreatedConversationInfo = true;
                }else{
                    Log.i(TAG, "No created conversation info for conversation id " + conversationId);
                    isCreatedConversationInfo = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        final DatabaseReference ref = FirebaseHelper.Instance().getFbDatabase().child(FirebaseConstant.CONVERSATIONS_INFO_NODE).child(conversationId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i(TAG, "Retrieve conversation info for conversation id " + conversationId);
                    isCreatedConversationInfo = true;
                    ref.removeEventListener(this);
                } else {
                    Log.i(TAG, "No created conversation info for conversation id " + conversationId);
                    isCreatedConversationInfo = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}

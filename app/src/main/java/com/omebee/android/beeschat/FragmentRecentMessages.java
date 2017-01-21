package com.omebee.android.beeschat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.omebee.android.beeschat.adapter.RecentMsgAdapter;
import com.omebee.android.beeschat.firebase.FirebaseConstant;
import com.omebee.android.beeschat.firebase.FirebaseHelper;
import com.omebee.android.beeschat.itemdata.RecentItemData;
import com.omebee.android.beeschat.models.ConversationInfoModel;
import com.omebee.android.beeschat.models.RecentMessageModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by phannguyen on 9/17/16.
 */
public class FragmentRecentMessages extends Fragment {

    private static final String TAG = "RecentMessagesList";
    private View mRootView;
    private ListView recentMsgList;
    private RecentMsgAdapter recentMsgAdapter;
    //private ArrayList<RecentItemData> recentsList;
    private  String currentUserId;
    ChildEventListener recentChildListener;
    private MainActivity mMainActivity;
    DatabaseReference currentUserRecentMsgRef;
    private Map<String,ConversationInfoModel> conversationInfoMap = new HashMap<>();//key is conversationId

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mRootView==null) {
            mRootView = inflater.inflate(R.layout.fragment_recent_messages_list, container, false);
        }
        mMainActivity = (MainActivity) this.getActivity();
        recentMsgList = (ListView) mRootView.findViewById(R.id.recentMsgListView);
        recentMsgAdapter = new RecentMsgAdapter(this.getActivity());
        recentMsgList.setAdapter(recentMsgAdapter);
        currentUserId = FirebaseHelper.Instance().getCurrentUserId();
        currentUserRecentMsgRef = FirebaseHelper.Instance().getFbDatabase().child(FirebaseConstant.RECENT_MESSAGES_NODE)
                .child(currentUserId);
        setRecentListItemOnclick();
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadAllRecentMessages(currentUserId);
        buildRecentChildListener();
        startListeningNewRecentMessage(currentUserId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        currentUserRecentMsgRef.removeEventListener(recentChildListener);
    }

    private void setRecentListItemOnclick(){
        recentMsgList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RecentItemData itemdata = (RecentItemData) recentMsgList.getAdapter().getItem(i);
                if(conversationInfoMap.containsKey(itemdata.getConversionId())){
                    Intent intent = new Intent(mMainActivity, ChattingActivity.class);
                    String recepientId = conversationInfoMap.get(itemdata.getConversionId()).getRecepientIdInSingleConversation(currentUserId);
                    if(recepientId!=null) {
                        intent.putExtra("RECIPIENT_ID", recepientId);
                        intent.putExtra("RECIPIENT_NAME",mMainActivity.getUsername(recepientId));
                        startActivity(intent);
                    }
                }

            }
        });
    }
    private void startListeningNewRecentMessage(String userId){
        Log.i(TAG,"Start listening new recent message from this moment on user id "+userId);
        currentUserRecentMsgRef.orderByChild(FirebaseConstant.MESSAGE_CREATED_DATE_FIELD).startAt(System.currentTimeMillis())
                .addChildEventListener(recentChildListener);
    }

    private void loadAllRecentMessages(final String userId){
        Query recentMessageQuery = FirebaseHelper.Instance().getFbDatabase().child(FirebaseConstant.RECENT_MESSAGES_NODE)
                .child(userId).orderByKey();
        recentMessageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Log.i(TAG,"Load recent message " + dataSnapshot.getChildrenCount() + " at first for user id "+userId);
                    final List<RecentItemData> recentDatas = new ArrayList<RecentItemData>();
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        RecentMessageModel recentModel = null;
                        try {
                            recentModel = new RecentMessageModel(messageSnapshot);
                        } catch (Exception e) {
                            e.printStackTrace();
                            recentModel = null;
                        }
                        if (recentModel != null) {
                            retreiveConversationInfo(recentModel.getConversationId());
                            RecentItemData recentData = new RecentItemData(recentModel);
                            //get username from userid
                            String username =mMainActivity.getUsername(recentModel.getLastMessageUserId());
                            if(username==null)
                                username = "Unknown";
                            recentData.setUserName(username);
                            //get useravatar url
                            recentData.setUserAvatarUrl("");
                            //
                            recentDatas.add(recentData);
                        }
                    }
                    if (recentDatas.size() > 0) {
                        Log.i(TAG, "Add recent message adapter and reload recent message list");
                        recentMsgAdapter.addLastMessage(recentDatas);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void buildRecentChildListener(){
        recentChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //new recent added
                Log.i(TAG,"Incomming new recent on user id "+currentUserId);
                RecentMessageModel recentModel = null;
                try {
                    recentModel = new RecentMessageModel(dataSnapshot);
                } catch (Exception e) {
                    e.printStackTrace();
                    recentModel = null;
                }
                if(recentModel!=null){
                    retreiveConversationInfo(recentModel.getConversationId());
                    RecentItemData recentData = new RecentItemData(recentModel);
                    //get username from userid
                    String username =mMainActivity.getUsername(recentModel.getLastMessageUserId());
                    if(username==null)
                        username = "Unknown";
                    recentData.setUserName(username);
                    //get useravatar url
                    recentData.setUserAvatarUrl("");
                    //
                    recentMsgAdapter.addOrUpdateAdapter(recentData);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // message added
                Log.i(TAG,"A recent change on user id "+currentUserId);
                RecentMessageModel recentModel = null;
                try {
                    recentModel = new RecentMessageModel(dataSnapshot);
                } catch (Exception e) {
                    e.printStackTrace();
                    recentModel = null;
                }
                if(recentModel!=null){
                    RecentItemData recentData = new RecentItemData(recentModel);
                    //get username from userid
                    String username =mMainActivity.getUsername(recentModel.getLastMessageUserId());
                    if(username==null)
                        username = "Unknown";
                    recentData.setUserName(username);
                    //get useravatar url
                    recentData.setUserAvatarUrl("");
                    //
                    recentMsgAdapter.addOrUpdateAdapter(recentData);
                }
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

    private void retreiveConversationInfo(final String conversationId){
        FirebaseHelper.Instance().getFbDatabase().child(FirebaseConstant.CONVERSATIONS_INFO_NODE).child(conversationId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Log.i(TAG,"retreiveConversationInfo fro conversation id "+conversationId);
                            ConversationInfoModel conversationInfoModel = null;
                            try {
                                conversationInfoModel = new ConversationInfoModel(dataSnapshot);
                            } catch (Exception e) {
                                e.printStackTrace();
                                conversationInfoModel = null;
                            }

                            if(conversationInfoModel!=null){
                                conversationInfoMap.put(conversationId,conversationInfoModel);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }



}

package com.omebee.android.beeschat.itemdata;

import com.omebee.android.beeschat.models.RecentMessageModel;

/**
 * Created by phannguyen on 9/18/16.
 */
public class RecentItemData {
    private String conversionId;
    private String userAvatarUrl;
    private String userName;
    private String userId;
    private String lastMessage;
    private long time;
    private int unReadCount;
    private String recentMessageId;

    public RecentItemData() {
    }

    public RecentItemData(String userAvatar, String userName, String lastMessage, long time, int unReadCount) {
        this.userAvatarUrl = userAvatar;
        this.userName = userName;
        this.lastMessage = lastMessage;
        this.time = time;
        this.unReadCount = unReadCount;
    }

    public RecentItemData(RecentMessageModel recentMessageModel) {
        this.lastMessage = recentMessageModel.getLastMessage();
        this.time = recentMessageModel.getLastMessageDate();
        this.unReadCount = recentMessageModel.getUnreadCount();
        this.userId = recentMessageModel.getLastMessageUserId();
        this.conversionId = recentMessageModel.getConversationId();
        this.recentMessageId = recentMessageModel.getRecentMessageId();
    }

    public  void updateData(RecentItemData newData){
        this.userId = newData.getUserId();
        this.userAvatarUrl = newData.getUserAvatarUrl();
        this.userName = newData.getUserName();
        this.lastMessage = newData.getLastMessage();
        this.time = newData.getTime();
        this.unReadCount = newData.getUnReadCount();
    }
    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    public String getRecentMessageId() {
        return recentMessageId;
    }

    public void setRecentMessageId(String recentMessageId) {
        this.recentMessageId = recentMessageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getConversionId() {
        return conversionId;
    }

    public void setConversionId(String conversionId) {
        this.conversionId = conversionId;
    }
}

package com.omebee.android.beeschat.firebase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by phannguyen on 9/14/16.
 */
public class FirebaseConstant {
    public static final String CONVERSATIONS_MESSAGES_NODE = "ConversationsMessages";
    public static final String CONVERSATIONS_INFO_NODE = "ConversationsInfo";
    public static final String RECENT_MESSAGES_NODE = "RecentMessages";

    public static final String CONVERSATION_ID_FIELD = "conversationId";
    public static final String MESSAGE_BODY_FIELD = "messageBody";
    public static final String MESSAGE_CREATED_DATE_FIELD = "messageCreatedDate";
    public static final String MESSAGE_ID_FIELD = "messageId";
    public static final String SENDER_ID_FIELD = "senderId";
    public static final String MESSAGE_STATUS_FIELD = "messageStatus";//1:sent, 2: deliveried, 3: seen, 4: deleted (maybe not)
    public static final String MESSAGE_TYPE_FIELD = "messageType";// Text, Picture, Location, Emoji

    public static final String CONVERSATION_TITLE_FIELD = "title";
    public static final String CONVERSATION_CREATED_DATE_FIELD = "createdDate";
    public static final String CONVERSATION_CREATED_USER_ID_FIELD = "createdUserId";
    public static final String USER_ID_FIELD = "userId";
    public static final String CONVERSATION_MEMBERS_FIELD = "members";
    public static final String RECENT_MESSAGE_ID_FIELD = "recentMessageId";
    public static final String LAST_MESSAGE_FIELD = "lastMessage";
    public static final String UNREAD_COUNT_FIELD = "unreadCount";
    public static final String LAST_MESSAGE_USER_ID_FIELD = "lastMessageUserId";


    public enum MessageType{
        Text("Text"),
        Picture("Picture"),
        Location("Location"),
        Emoji("Emoji");

        private static final Map<String, MessageType> enumsByValue = new HashMap<String, MessageType>();
        static {
            for (MessageType type : MessageType.values()) {
                enumsByValue.put(type.value, type);
            }
        }
        public static MessageType getEnum(String value) {
            return enumsByValue.get(value);
        }
        private final String value;
        private MessageType(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return this.getValue();
        }
    }

    public enum MessageStatus {
        Sent(1), Deliveried(2), Seen (3), Deleted(4);
        private static final Map<Integer, MessageStatus> enumsByValue = new HashMap<Integer, MessageStatus>();
        static {
            for (MessageStatus type : MessageStatus.values()) {
                enumsByValue.put(type.value, type);
            }
        }
        public static MessageStatus getEnum(int value) {
            return enumsByValue.get(value);
        }
        private final int value;
        private MessageStatus(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }



}

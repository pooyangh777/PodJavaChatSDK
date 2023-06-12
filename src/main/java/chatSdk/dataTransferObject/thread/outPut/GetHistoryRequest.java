package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import lombok.Getter;

import java.util.List;
@Getter
public class GetHistoryRequest extends BaseRequest {
    private Long threadId;
    private Integer offset;
    private Integer count;
    private Integer fromTime;
    private Integer fromTimeNanos;
    private Integer messageId;
    private Integer messageType;
    private String order;
    private String query;
    private Integer toTime;
    private String hashtag;
    private Integer toTimeNanos;
    private List<String> uniqueIds;
    private Integer userId;
    private Integer messageThreadId;
    private Integer firstMessageId;
    private Integer lastMessageId;
    private Integer senderId;
    private Integer historyTime;
    private Boolean allMentioned;
    private Boolean unreadMentioned;
    private Integer lastMessageTime;
    private Integer historyEndTime;
    private Boolean newMessages;

    public GetHistoryRequest(Builder builder) {
        super(builder.uniqueId, builder.subjectId, ChatMessageType.GET_HISTORY);
        this.threadId = builder.threadId;
        this.offset = builder.offset;
        this.count = builder.count;
        this.fromTime = builder.fromTime;
        this.fromTimeNanos = builder.fromTimeNanos;
        this.messageId = builder.messageId;
        this.messageType = builder.messageType;
        this.order = builder.order;
        this.query = builder.query;
        this.toTime = builder.toTime;
        this.hashtag = builder.hashtag;
        this.toTimeNanos = builder.toTimeNanos;
        this.uniqueIds = builder.uniqueIds;
        this.userId = builder.userId;
        this.messageThreadId = builder.messageThreadId;
        this.firstMessageId = builder.firstMessageId;
        this.lastMessageId = builder.lastMessageId;
        this.senderId = builder.senderId;
        this.historyTime = builder.historyTime;
        this.allMentioned = builder.allMentioned;
        this.unreadMentioned = builder.unreadMentioned;
        this.lastMessageTime = builder.lastMessageTime;
        this.historyEndTime = builder.historyEndTime;
        this.newMessages = builder.newMessages;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }

    public static class Builder {
        private String uniqueId;
        private Long subjectId;
        private Long threadId;
        private Integer offset = 0;
        private Integer count = 25;
        private Integer fromTime;
        private Integer fromTimeNanos;
        private Integer messageId;
        private Integer messageType;
        private String order;
        private String query;
        private Integer toTime;
        private String hashtag;
        private Integer toTimeNanos;
        private List<String> uniqueIds;
        private Integer userId;
        private Integer messageThreadId;
        private Integer firstMessageId;
        private Integer lastMessageId;
        private Integer senderId;
        private Integer historyTime;
        private Boolean allMentioned;
        private Boolean unreadMentioned;
        private Integer lastMessageTime;
        private Integer historyEndTime;
        private Boolean newMessages;

        public Long getThreadId() {
            return threadId;
        }

        public Builder setThreadId(Long threadId) {
            this.threadId = threadId;
            return this;
        }

        public Integer getOffset() {
            return offset;
        }

        public Builder setOffset(Integer offset) {
            this.offset = offset;
            return this;
        }

        public Integer getCount() {
            return count;
        }

        public Builder setCount(Integer count) {
            this.count = count;
            return this;
        }

        public Integer getFromTime() {
            return fromTime;
        }

        public Builder setFromTime(Integer fromTime) {
            this.fromTime = fromTime;
            return this;
        }

        public Integer getFromTimeNanos() {
            return fromTimeNanos;
        }

        public Builder setFromTimeNanos(Integer fromTimeNanos) {
            this.fromTimeNanos = fromTimeNanos;
            return this;
        }

        public Integer getMessageId() {
            return messageId;
        }

        public Builder setMessageId(Integer messageId) {
            this.messageId = messageId;
            return this;
        }

        public Integer getMessageType() {
            return messageType;
        }

        public Builder setMessageType(Integer messageType) {
            this.messageType = messageType;
            return this;
        }

        public String getOrder() {
            return order;
        }

        public Builder setOrder(String order) {
            this.order = order;
            return this;
        }

        public String getQuery() {
            return query;
        }

        public Builder setQuery(String query) {
            this.query = query;
            return this;
        }

        public Integer getToTime() {
            return toTime;
        }

        public Builder setToTime(Integer toTime) {
            this.toTime = toTime;
            return this;
        }

        public String getHashtag() {
            return hashtag;
        }

        public Builder setHashtag(String hashtag) {
            this.hashtag = hashtag;
            return this;
        }

        public Integer getToTimeNanos() {
            return toTimeNanos;
        }

        public Builder setToTimeNanos(Integer toTimeNanos) {
            this.toTimeNanos = toTimeNanos;
            return this;
        }

        public List<String> getUniqueIds() {
            return uniqueIds;
        }

        public Builder setUniqueIds(List<String> uniqueIds) {
            this.uniqueIds = uniqueIds;
            return this;
        }

        public Integer getUserId() {
            return userId;
        }

        public Builder setUserId(Integer userId) {
            this.userId = userId;
            return this;
        }

        public Integer getMessageThreadId() {
            return messageThreadId;
        }

        public Builder setMessageThreadId(Integer messageThreadId) {
            this.messageThreadId = messageThreadId;
            return this;
        }

        public Integer getFirstMessageId() {
            return firstMessageId;
        }

        public Builder setFirstMessageId(Integer firstMessageId) {
            this.firstMessageId = firstMessageId;
            return this;
        }

        public Integer getLastMessageId() {
            return lastMessageId;
        }

        public Builder setLastMessageId(Integer lastMessageId) {
            this.lastMessageId = lastMessageId;
            return this;
        }

        public Integer getSenderId() {
            return senderId;
        }

        public Builder setSenderId(Integer senderId) {
            this.senderId = senderId;
            return this;
        }

        public Integer getHistoryTime() {
            return historyTime;
        }

        public Builder setHistoryTime(Integer historyTime) {
            this.historyTime = historyTime;
            return this;
        }

        public Boolean getAllMentioned() {
            return allMentioned;
        }

        public Builder setAllMentioned(Boolean allMentioned) {
            this.allMentioned = allMentioned;
            return this;
        }

        public Boolean getUnreadMentioned() {
            return unreadMentioned;
        }

        public Builder setUnreadMentioned(Boolean unreadMentioned) {
            this.unreadMentioned = unreadMentioned;
            return this;
        }

        public Integer getLastMessageTime() {
            return lastMessageTime;
        }

        public Builder setLastMessageTime(Integer lastMessageTime) {
            this.lastMessageTime = lastMessageTime;
            return this;
        }

        public Integer getHistoryEndTime() {
            return historyEndTime;
        }

        public Builder setHistoryEndTime(Integer historyEndTime) {
            this.historyEndTime = historyEndTime;
            return this;
        }

        public Boolean getNewMessages() {
            return newMessages;
        }

        public Builder setNewMessages(Boolean newMessages) {
            this.newMessages = newMessages;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public Long getSubjectId() {
            return subjectId;
        }

        public Builder setSubjectId(Long subjectId) {
            this.subjectId = subjectId;
            return this;
        }

        public GetHistoryRequest build() {
            return new GetHistoryRequest(this);
        }
    }
}

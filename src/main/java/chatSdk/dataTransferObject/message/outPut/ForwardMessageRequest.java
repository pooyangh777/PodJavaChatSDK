package chatSdk.dataTransferObject.message.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.UUID;

public class ForwardMessageRequest extends BaseRequest {
    private long threadId;
    @Expose
    private final ArrayList<Long> messageIds;
    @Expose
    private ArrayList<String> uniqueIds;

    public ForwardMessageRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.FORWARD_MESSAGE);
        this.subjectId = builder.threadId;
        this.messageIds = builder.messageIds;
        this.uniqueIds = builder.uniqueIds;
        if (uniqueIds == null) {
            uniqueIds = new ArrayList<>();
            for (int i = 0; i < messageIds.size(); i++) {
                uniqueIds.add(UUID.randomUUID().toString());
            }
        }
    }

    @Override
    public String getChatMessageContent() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    public static class Builder {
        private long threadId;
        private ArrayList<Long> messageIds;
        private ArrayList<String> uniqueIds;
        private String uniqueId;

        public long getThreadId() {
            return threadId;
        }

        public Builder setThreadId(long threadId) {
            this.threadId = threadId;
            return this;
        }

        public ArrayList<Long> getMessageIds() {
            return messageIds;
        }

        public Builder setMessageIds(ArrayList<Long> messageIds) {
            this.messageIds = messageIds;
            return this;
        }

        public ArrayList<String> getUniqueIds() {
            return uniqueIds;
        }

        public Builder setUniqueIds(ArrayList<String> uniqueIds) {
            this.uniqueIds = uniqueIds;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public ForwardMessageRequest build() {
            return new ForwardMessageRequest(this);
        }
    }
}

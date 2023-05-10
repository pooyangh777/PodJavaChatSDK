package chatSdk.dataTransferObject.message.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.UUID;

public class ForwardMessageRequest extends BaseRequest {
    private long threadId;
    private final ArrayList<Long> messageIds;
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
            uniqueId = new Gson().toJson(uniqueIds);
        }
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(messageIds);
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

//    {
//        "peerName": "chat-server",
//            "priority": 1,
//            "content": {
//        "type": 22,
//                "token": "e1ef9b7839bc4dfd8c07dc68b99a326e.XzIwMjM1",
//                "tokenIssuer": 1,
//                "typeCode": "default",
//                "subjectId": 4049730,
//                "content": [
//                730629758,
//                730708651],
//        "uniqueId": [
//                 "17ded9b1-6a49-40d1-abd9-917ec7ef2590",
//                 "22c05849-c87f-453b-bb76-cf66cafc386c"]
//    },
//            "ttl": 10000,
//            "messageId": 39
//    }
}

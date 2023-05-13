package chatSdk.dataTransferObject.contacts.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;

public class GetContactsRequest extends BaseRequest {
    Long count;
    Long offset;

    public GetContactsRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.GET_CONTACTS);
        this.count = builder.count;
        this.offset = builder.offset;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }

    public static class Builder {
        Long count = 25l;
        Long offset = 0l;
        String uniqueId;

        public Long getCount() {
            return count;
        }

        public Builder setCount(Long count) {
            this.count = count;
            return this;
        }

        public Long getOffset() {
            return offset;
        }

        public Builder setOffset(Long offset) {
            this.offset = offset;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public GetContactsRequest build() {
            return new GetContactsRequest(this);
        }
    }
}

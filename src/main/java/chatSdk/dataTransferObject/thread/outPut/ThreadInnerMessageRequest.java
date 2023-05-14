package chatSdk.dataTransferObject.thread.outPut;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class ThreadInnerMessageRequest {
    private final String text;
    private final Integer messageType;
    private final String metadata;
    private final String systemMetadata;
    private final List<Long> forwardedMessageIds;
    private String uniqueId;

    public ThreadInnerMessageRequest(Builder builder) {
        this.text = builder.text;
        this.messageType = builder.messageType;
        this.metadata = builder.metadata;
        this.systemMetadata = builder.systemMetadata;
        this.forwardedMessageIds = builder.forwardedMessageIds;
        this.uniqueId = builder.uniqueId;
        if (uniqueId == null)
            uniqueId = UUID.randomUUID().toString();
    }

    public static class Builder {
        private String text;
        private Integer messageType;
        private String metadata;
        private String systemMetadata;
        private List<Long> forwardedMessageIds;
        private String uniqueId;

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public String getText() {
            return text;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public Integer getMessageType() {
            return messageType;
        }

        public Builder setMessageType(Integer messageType) {
            this.messageType = messageType;
            return this;
        }

        public String getMetadata() {
            return metadata;
        }

        public Builder setMetadata(String metadata) {
            this.metadata = metadata;
            return this;
        }

        public String getSystemMetadata() {
            return systemMetadata;
        }

        public Builder setSystemMetadata(String systemMetadata) {
            this.systemMetadata = systemMetadata;
            return this;
        }

        public List<Long> getForwardedMessageIds() {
            return forwardedMessageIds;
        }

        public Builder setForwardedMessageIds(List<Long> forwardedMessageIds) {
            this.forwardedMessageIds = forwardedMessageIds;
            return this;
        }

        public ThreadInnerMessageRequest build() {
            return new ThreadInnerMessageRequest(this);
        }
    }
}

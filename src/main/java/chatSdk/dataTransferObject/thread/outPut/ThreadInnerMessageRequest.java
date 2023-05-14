package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.message.outPut.Exclude;

import java.util.List;
import java.util.UUID;

public class ThreadInnerMessageRequest {
    private String text;
    @Exclude
    private Integer type;
    private String metadata;
    private String systemMetadata;
    private List<Long> forwardedMessageIds;
    private String uniqueId;

    public ThreadInnerMessageRequest(Builder builder) {
        this.text = builder.text;
        this.type = builder.type;
        this.metadata = builder.metadata;
        this.systemMetadata = builder.systemMetadata;
        this.forwardedMessageIds = builder.forwardedMessageIds;
        this.uniqueId = builder.uniqueId;
        if (uniqueId == null)
            uniqueId = UUID.randomUUID().toString();
    }

    public static class Builder {
        private String text;
        private int type;
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

        public int getType() {
            return type;
        }

        public Builder setType(int type) {
            this.type = type;
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

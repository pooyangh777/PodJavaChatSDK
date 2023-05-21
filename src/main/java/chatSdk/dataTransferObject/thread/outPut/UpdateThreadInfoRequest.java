package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.file.outPut.RequestUploadImage;
import lombok.Getter;

@Getter
public class UpdateThreadInfoRequest extends BaseRequest {
    private final String description;
    private final String metadata;
    private final RequestUploadImage threadImage;
    private Long threadId;
    private final String title;

    public UpdateThreadInfoRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.UPDATE_THREAD_INFO);
        this.description = builder.description;
        this.metadata = builder.metadata;
        this.threadImage = builder.threadImage;
        this.subjectId = builder.threadId;
        this.title = builder.title;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }

    public static class Builder {
        private String description;
        private String metadata;
        private RequestUploadImage threadImage;
        private Long threadId;
        private String title;
        private String uniqueId;

        public String getDescription() {
            return description;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public String getMetadata() {
            return metadata;
        }

        public Builder setMetadata(String metadata) {
            this.metadata = metadata;
            return this;
        }

        public RequestUploadImage getThreadImage() {
            return threadImage;
        }

        public Builder setThreadImage(RequestUploadImage threadImage) {
            this.threadImage = threadImage;
            return this;
        }

        public Long getThreadId() {
            return threadId;
        }

        public Builder setThreadId(Long threadId) {
            this.threadId = threadId;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public UpdateThreadInfoRequest build() {
            return new UpdateThreadInfoRequest(this);
        }
    }
}

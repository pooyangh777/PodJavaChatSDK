package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.inPut.Invitee;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateThreadWithMessageRequest extends BaseRequest {
    private final int type;
    private final String ownerSsoId;
    private final List<Invitee> invitees;
    private final String title;
    private final ThreadInnerMessageRequest message;
    private final String description;
    private final String image;

    public CreateThreadWithMessageRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.CREATE_THREAD);
        this.type = builder.type;
        this.ownerSsoId = builder.ownerSsoId;
        this.invitees = builder.invitees;
        this.title = builder.title;
        this.message = builder.message;
        this.description = builder.description;
        this.image = builder.image;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }

    public static class Builder {
        private int type;
        private String ownerSsoId;
        private List<Invitee> invitees;
        private String title;
        private ThreadInnerMessageRequest message;
        private String description;
        private String image;
        private String uniqueId;

        public int getType() {
            return type;
        }

        public Builder setType(int type) {
            this.type = type;
            return this;
        }

        public String getOwnerSsoId() {
            return ownerSsoId;
        }

        public Builder setOwnerSsoId(String ownerSsoId) {
            this.ownerSsoId = ownerSsoId;
            return this;
        }

        public List<Invitee> getInvitees() {
            return invitees;
        }

        public Builder setInvitees(List<Invitee> invitees) {
            this.invitees = invitees;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public ThreadInnerMessageRequest getMessage() {
            return message;
        }

        public Builder setMessage(ThreadInnerMessageRequest message) {
            this.message = message;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public String getImage() {
            return image;
        }

        public Builder setImage(String image) {
            this.image = image;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public CreateThreadWithMessageRequest build() {
            return new CreateThreadWithMessageRequest(this);
        }
    }
}

package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.inPut.Invitee;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateThreadRequest extends BaseRequest {
    private final int type;
    private final String ownerSsoId;
    private final List<Invitee> invitees;
    private final String title;
    private final String description;
    private final String image;
    private final String metadata;

    public CreateThreadRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.CREATE_THREAD);
        this.type = builder.type;
        this.ownerSsoId = builder.ownerSsoId;
        this.invitees = builder.invitees;
        this.title = builder.title;
        this.description = builder.description;
        this.image = builder.image;
        this.metadata = builder.metadata;
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
        private String description;
        private String image;
        private String metadata;
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

        public String getMetadata() {
            return metadata;
        }

        public Builder setMetadata(String metadata) {
            this.metadata = metadata;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public CreateThreadRequest build() {
            return new CreateThreadRequest(this);
        }
    }
}

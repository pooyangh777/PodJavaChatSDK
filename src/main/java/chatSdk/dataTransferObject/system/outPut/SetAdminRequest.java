package chatSdk.dataTransferObject.system.outPut;

import chatSdk.dataTransferObject.thread.outPut.BaseRequest;
import com.google.gson.Gson;

import java.util.ArrayList;

public class SetAdminRequest extends BaseRequest {
    private ArrayList<String> roles;

    public SetAdminRequest(Builder builder) {
        super(builder.uniqueId, builder.subjectId, builder.messageType);
        this.roles = builder.roles;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(roles);
    }

    public static class Builder {
        String uniqueId;
        Long subjectId;
        int messageType;
        ArrayList<String> roles;

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

        public int getMessageType() {
            return messageType;
        }

        public Builder setMessageType(int messageType) {
            this.messageType = messageType;
            return this;
        }

        public ArrayList<String> getRoles() {
            return roles;
        }

        public Builder setRoles(ArrayList<String> roles) {
            this.roles = roles;
            return this;
        }

        public SetAdminRequest build() {
            return new SetAdminRequest(this);
        }
    }
}

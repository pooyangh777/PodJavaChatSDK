package chatSdk.dataTransferObject.message.inPut;

import chatSdk.dataTransferObject.thread.inPut.Conversation;
import chatSdk.dataTransferObject.thread.inPut.ForwardInfo;
import chatSdk.dataTransferObject.thread.inPut.ReplyInfo;
import chatSdk.dataTransferObject.thread.inPut.Participant;
import com.google.gson.annotations.SerializedName;

public class Message {
    private Long id;
    private String uniqueId;
    private Long previousId;
    private String message;
    private Integer messageType;
    private Boolean edited;
    private Boolean editable;
    private Boolean deletable;
    private Participant participant;
    private Conversation conversation;
    private Long time;
    private Integer timeNanos;
    private Boolean delivered;
    private Boolean seen;
    private String metadata;
    private String systemMetadata;

    @SerializedName("replyInfoVO")
    private ReplyInfo replyInfo;
    private ForwardInfo forwardInfo;
    private boolean mentioned;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Long getPreviousId() {
        return previousId;
    }

    public void setPreviousId(Long previousId) {
        this.previousId = previousId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public Boolean isEdited() {
        return edited;
    }

    public void setEdited(Boolean edited) {
        this.edited = edited;
    }

    public Boolean isEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(Boolean deletable) {
        this.deletable = deletable;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getTimeNanos() {
        return timeNanos;
    }

    public void setTimeNanos(Integer timeNanos) {
        this.timeNanos = timeNanos;
    }

    public Boolean getDelivered() {
        return delivered;
    }

    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getSystemMetadata() {
        return systemMetadata;
    }

    public void setSystemMetadata(String systemMetadata) {
        this.systemMetadata = systemMetadata;
    }

    public ReplyInfo getReplyInfo() {
        return replyInfo;
    }

    public void setReplyInfo(ReplyInfo replyInfo) {
        this.replyInfo = replyInfo;
    }

    public ForwardInfo getForwardInfo() {
        return forwardInfo;
    }

    public void setForwardInfo(ForwardInfo forwardInfo) {
        this.forwardInfo = forwardInfo;
    }

    public boolean isMentioned() {
        return mentioned;
    }

    public void setMentioned(boolean mentioned) {
        this.mentioned = mentioned;
    }
}

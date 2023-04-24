package chatSdk.dataTransferObject.message.inPut;

public class ResultDeleteMessage {
    private DeleteMessageContent deletedMessage;

    public DeleteMessageContent getDeletedMessage() {
        return deletedMessage;
    }

    public void setDeletedMessage(DeleteMessageContent deleteMessageContent) {
        this.deletedMessage = deleteMessageContent;
    }
}
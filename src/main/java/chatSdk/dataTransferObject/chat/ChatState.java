package chatSdk.dataTransferObject.chat;

public enum ChatState {
    Connecting("Connecting"),
    Connected("Connected"),
    AsyncReady("AsyncReady"),
    ChatReady("ChatReady"),
    Closed("Closed");

    private final String value;

    ChatState(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}

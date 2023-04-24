package chatSdk.chat;

import chatSdk.chat.listeners.ChatCallback;
import chatSdk.dataTransferObject.chat.ChatState;
import chatSdk.dataTransferObject.system.outPut.ErrorOutPut;

public interface ChatListener extends ChatCallback {
    void onError(String content, ErrorOutPut error);

    void onChatState(ChatState state);

    void OnLogEvent(String log);
}

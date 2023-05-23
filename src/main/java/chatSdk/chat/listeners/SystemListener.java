package chatSdk.chat.listeners;

import chatSdk.dataTransferObject.ChatResponse2;
import chatSdk.dataTransferObject.system.inPut.ResultSetRole;

public interface SystemListener {
    default void OnSetRole(String content, ChatResponse2<ResultSetRole> chatResponse) {}
    default void OnRemoveRole(String content, ChatResponse2<ResultSetRole> chatResponse) {}
    default void handleCallbackError(Throwable cause) throws Exception {}
}

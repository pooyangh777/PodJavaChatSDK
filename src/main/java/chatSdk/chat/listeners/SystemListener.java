package chatSdk.chat.listeners;

import chatSdk.dataTransferObject.ChatResponse;
import chatSdk.dataTransferObject.system.inPut.ResultSetRole;

public interface SystemListener {
    default void OnSetRole(String content, ChatResponse<ResultSetRole> chatResponse) {}
    default void OnRemoveRole(String content, ChatResponse<ResultSetRole> chatResponse) {}
    default void handleCallbackError(Throwable cause) throws Exception {}
}

package chatSdk.chat.listeners;

import chatSdk.dataTransferObject.ChatResponse;
import chatSdk.dataTransferObject.message.inPut.ResultMessage;
import chatSdk.dataTransferObject.user.inPut.ResultBlock;
import chatSdk.dataTransferObject.user.inPut.ResultBlockList;
import chatSdk.dataTransferObject.user.inPut.ResultClearHistory;
import chatSdk.dataTransferObject.user.inPut.ResultHistory;

public interface UserListener {
    default void onBlock(String content, ChatResponse<ResultBlock> response) {}
    default void onUnBlock(String content, ChatResponse<ResultBlock> response) {}
    default void onSeen(String content, ChatResponse<ResultMessage> response) {}
    default void onDeliver(String content, ChatResponse<ResultMessage> response) {}
    default void onGetBlockList(String content, ChatResponse<ResultBlockList> response) {}
    default void onGetHistory(String content, ChatResponse<ResultHistory> history) {}
    default void OnClearHistory(String content, ChatResponse<ResultClearHistory> chatResponse) {}
    default void onLastSeenUpdated(String content) {}
}

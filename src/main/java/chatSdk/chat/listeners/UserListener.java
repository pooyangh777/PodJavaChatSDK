package chatSdk.chat.listeners;

import chatSdk.dataTransferObject.ChatResponse2;
import chatSdk.dataTransferObject.message.inPut.ResultMessage;
import chatSdk.dataTransferObject.user.inPut.ResultBlock;
import chatSdk.dataTransferObject.user.inPut.ResultBlockList;
import chatSdk.dataTransferObject.user.inPut.ResultClearHistory;
import chatSdk.dataTransferObject.user.inPut.ResultHistory;

public interface UserListener {
    default void onBlock(String content, ChatResponse2<ResultBlock> response) {}
    default void onUnBlock(String content, ChatResponse2<ResultBlock> response) {}
    default void onSeen(String content, ChatResponse2<ResultMessage> response) {}
    default void onDeliver(String content, ChatResponse2<ResultMessage> response) {}
    default void onGetBlockList(String content, ChatResponse2<ResultBlockList> response) {}
    default void OnClearHistory(String content, ChatResponse2<ResultClearHistory> chatResponse) {}
    default void onLastSeenUpdated(String content) {}
}

package chatSdk.chat.listeners;

import chatSdk.dataTransferObject.ChatResponse;
import chatSdk.dataTransferObject.ChatResponse2;
import chatSdk.dataTransferObject.contacts.inPut.Contact;
import chatSdk.dataTransferObject.message.inPut.ResultMessage;
import chatSdk.dataTransferObject.user.inPut.ResultBlock;
import chatSdk.dataTransferObject.user.inPut.ResultBlockList;
import chatSdk.dataTransferObject.user.inPut.ResultClearHistory;
import chatSdk.dataTransferObject.user.inPut.ResultHistory;

public interface UserListener {
    default void onBlock(ChatResponse<Contact> block) {}

    default void onUnblock(ChatResponse<Contact> unblock) {}
    default void onGetBlockList(ChatResponse<Contact[]> blockList) {}

    default void onLastSeenUpdated(String content) {}
}

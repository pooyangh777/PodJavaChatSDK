package chatSdk.chat.listeners;

import chatSdk.dataTransferObject.ChatResponse;
import chatSdk.dataTransferObject.message.inPut.ResultInteractMessage;
import chatSdk.dataTransferObject.message.inPut.ResultMessage;
import chatSdk.dataTransferObject.message.inPut.ResultNewMessage;
import chatSdk.dataTransferObject.thread.inPut.ResultParticipant;
import chatSdk.dataTransferObject.message.inPut.ResultDeleteMessage;

public interface MessageListener {
    default void onDeleteMessage(String content, ChatResponse<ResultDeleteMessage> response) {}
    default void onNewMessage(String content, ChatResponse<ResultNewMessage> response) {}
    default void onEditedMessage(String content, ChatResponse<ResultNewMessage> response) {}
    default void OnInteractMessage(String content, ChatResponse<ResultInteractMessage> chatResponse) {}
    default void OnDeliveredMessageList(String content, ChatResponse<ResultParticipant> response) {}
    default void OnSeenMessageList(String content, ChatResponse<ResultParticipant> response) {}
    default void onSent(String content, ChatResponse<ResultMessage> response) {}

}

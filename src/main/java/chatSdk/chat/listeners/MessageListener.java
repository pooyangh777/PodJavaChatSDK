package chatSdk.chat.listeners;

import chatSdk.dataTransferObject.ChatResponse2;
import chatSdk.dataTransferObject.message.inPut.ResultInteractMessage;
import chatSdk.dataTransferObject.message.inPut.ResultMessage;
import chatSdk.dataTransferObject.message.inPut.ResultNewMessage;
import chatSdk.dataTransferObject.thread.inPut.ResultParticipant;
import chatSdk.dataTransferObject.message.inPut.ResultDeleteMessage;

public interface MessageListener {
    default void onDeleteMessage(String content, ChatResponse2<ResultDeleteMessage> response) {}
    default void onNewMessage(String content, ChatResponse2<ResultNewMessage> response) {}
    default void onEditedMessage(String content, ChatResponse2<ResultNewMessage> response) {}
    default void OnInteractMessage(String content, ChatResponse2<ResultInteractMessage> chatResponse) {}
    default void OnDeliveredMessageList(String content, ChatResponse2<ResultParticipant> response) {}
    default void OnSeenMessageList(String content, ChatResponse2<ResultParticipant> response) {}
    default void onSent(String content, ChatResponse2<ResultMessage> response) {}

}

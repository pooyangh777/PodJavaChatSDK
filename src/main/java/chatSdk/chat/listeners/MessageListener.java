package chatSdk.chat.listeners;

import chatSdk.dataTransferObject.ChatResponse;
import chatSdk.dataTransferObject.ChatResponse2;
import chatSdk.dataTransferObject.GeneralResponse;
import chatSdk.dataTransferObject.message.inPut.*;
import chatSdk.dataTransferObject.thread.inPut.Participant;
import chatSdk.dataTransferObject.thread.inPut.ResultParticipant;

public interface MessageListener {
    default void onDeleteMessage(ChatResponse<Message> deleteMessage) {
    }

    default void onNewMessage(ChatResponse<Message> newMessage) {
    }

    default void onEditedMessage(ChatResponse<Message> editMessage) {
    }

    default void OnInteractMessage(String content, ChatResponse2<ResultInteractMessage> chatResponse) {
    }

    default void OnDeliveredMessageList(ChatResponse<Participant[]> deliveredMessageList) {
    }

    default void OnSeenMessageList(ChatResponse<Participant[]> seenMessageList) {
    }

    default void onSent(ChatResponse<ResultMessage> sent) {
    }

    default void onDelivered(ChatResponse<ResultMessage> delivered) {
    }

    default void onSeen(ChatResponse<ResultMessage> seen) {
    }
}

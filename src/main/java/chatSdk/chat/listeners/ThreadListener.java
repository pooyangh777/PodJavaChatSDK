package chatSdk.chat.listeners;

import chatSdk.dataTransferObject.ChatResponse;
import chatSdk.dataTransferObject.ChatResponse2;
import chatSdk.dataTransferObject.GeneralResponse;
import chatSdk.dataTransferObject.message.inPut.Message;
import chatSdk.dataTransferObject.thread.inPut.*;
import chatSdk.dataTransferObject.thread.outPut.OutPutThread;

public interface ThreadListener {
    default void onGetThread(ChatResponse<Conversation[]> thread) {
    }

    default void onGetHistory(ChatResponse<Message[]> messages) {
    }

    default void onClearHistory(ChatResponse<GeneralResponse> clearHistory) {
    }

    default void onThreadInfoUpdated(String content, ChatResponse2<ResultThread> response) {
    }

    default void onMuteThread(ChatResponse<GeneralResponse> mute) {
    }

    default void onUnmuteThread(ChatResponse<GeneralResponse> unmute) {
    }

    default void onCreateThread(ChatResponse<Conversation> createThread) {
    }


    default void onGetThreadParticipant(String content, ChatResponse2<ResultParticipant> response) {
    }

    default void onGetThreadParticipant2(ChatResponse<Participant[]> threadParticipants) {
    }

    default void onRenameThread(String content, OutPutThread outPutThread) {
    }

    default void onUpdateThreadInfo(ChatResponse<Conversation> updateThreadInfo) {
    }

    default void onThreadLeaveParticipant(ChatResponse<ResultLeaveThread> leaveThread) {
    }

    default void onThreadRemoveParticipant(ChatResponse<Participant[]> removeParticipants) {
    }

    default void onThreadAddParticipant(ChatResponse<Conversation> addThreadParticipants) {
    }

    default void onRemovedFromThread(ChatResponse<Conversation> removeFromThread) {
    }

    default void OnGetThreadAdmin(String content) {
    }

    default void onPinThread(ChatResponse<GeneralResponse> pin) {
    }

    default void onUnPinThread(ChatResponse<GeneralResponse> unPin) {
    }

    default void onRegisterAssistant(ChatResponse<Assistant[]> registerAssistant) {
    }

    default void onDeActiveAssistant(ChatResponse<Assistant[]> DeActiveAssistant) {
    }
}

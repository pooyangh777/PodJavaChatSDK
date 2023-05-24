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

    default void onRenameThread(String content, OutPutThread outPutThread) {
    }

    default void onUpdateThreadInfo(String threadJson, ChatResponse2<ResultThread> response) {
    }

    default void onThreadLeaveParticipant(String content, ChatResponse2<ResultLeaveThread> response) {
    }

    default void onThreadRemoveParticipant(String content, ChatResponse2<ResultParticipant> response) {
    }

    default void onThreadAddParticipant(String content, ChatResponse2<ResultAddParticipant> response) {
    }

    default void OnRemovedFromThread(String content, ChatResponse2<ResultThread> chatResponse) {
    }

    default void OnGetThreadAdmin(String content) {
    }

    default void onPinThread(ChatResponse<GeneralResponse> pin) {
    }

    default void onUnPinThread(ChatResponse<GeneralResponse> unPin) {
    }
}

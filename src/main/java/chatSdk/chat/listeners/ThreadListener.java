package chatSdk.chat.listeners;

import chatSdk.dataTransferObject.ChatResponse;
import chatSdk.dataTransferObject.thread.inPut.*;
import chatSdk.dataTransferObject.thread.outPut.OutPutThread;

public interface ThreadListener {
    default void onGetThread(String content, ChatResponse<ResultThreads> thread) {}
    default void onThreadInfoUpdated(String content, ChatResponse<ResultThread> response) {}
    default void onMuteThread(String content, ChatResponse<ResultMute> response) {}
    default void onUnmuteThread(String content, ChatResponse<ResultMute> response) {}
    default void onCreateThread(String content, ChatResponse<ResultThread> response) {}
    default void onGetThreadParticipant(String content, ChatResponse<ResultParticipant> response) {}
    default void onRenameThread(String content, OutPutThread outPutThread) {}
    default void onUpdateThreadInfo(String threadJson, ChatResponse<ResultThread> response) {}
    default void onThreadLeaveParticipant(String content, ChatResponse<ResultLeaveThread> response) {}
    default void onThreadRemoveParticipant(String content, ChatResponse<ResultParticipant> response) {}
    default void onThreadAddParticipant(String content, ChatResponse<ResultAddParticipant> response) {}
    default void OnRemovedFromThread(String content, ChatResponse<ResultThread> chatResponse) {}
    default void OnGetThreadAdmin(String content) {}
    default void onPin(String content, ChatResponse<ResultPin> response) {}
    default void onUnPin(String content, ChatResponse<ResultPin> response) {}
}

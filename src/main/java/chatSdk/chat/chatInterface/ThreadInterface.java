package chatSdk.chat.chatInterface;

import chatSdk.dataTransferObject.GeneralRequest;
import chatSdk.dataTransferObject.thread.outPut.*;

public interface ThreadInterface {
    String getThreads(GetThreadRequest requestThread);

    String muteThread2(GeneralRequest request);

    String unMuteThread2(GeneralRequest request);

    String unPinThread2(GeneralRequest request);

    String pinThread2(GeneralRequest request);

    String getThreadParticipants2(ThreadParticipantRequest request);

    String removeParticipants2(RemoveParticipantsRequest request);

    String addParticipants2(AddParticipantsRequest request);

    String closeThread(GeneralRequest request);

    String leaveThread2(LeaveThreadRequest request);

    String clearHistory2(ClearHistoryRequest request);

    String createThread2(CreateThreadRequest request);

    String createThreadWithMessage2(CreateThreadWithMessageRequest request);

    String getHistory2(GetHistoryRequest request);

    String getUserInfo2(GeneralRequest request);
}

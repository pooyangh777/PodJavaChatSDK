package chatSdk.chat.chatInterface;

import chatSdk.dataTransferObject.GeneralRequest;
import chatSdk.dataTransferObject.thread.outPut.*;

public interface ThreadInterface {
    String createTag(CreateTagRequest request);

    String getTagList(GeneralRequest request);

    String getTagParticipants(GetTagParticipantsRequest request);

    String addTagParticipants(AddTagParticipantsRequest request);

    String deleteTag(DeleteTagRequest request);

    String editTag(EditTagRequest request);

    String unArchiveThread(GeneralRequest request);

    String archiveThread(GeneralRequest request);

    String isThreadNamePublic(IsThreadNamePublicRequest request);

    String updateThreadInfo(UpdateThreadInfoRequest request);

    String changeThreadType(ChangeThreadTypeRequest request);

    String spam(GeneralRequest request);

    String getThreads(GetThreadRequest requestThread);

    String muteThread(GeneralRequest request);

    String unMuteThread(GeneralRequest request);

    String unPinThread(GeneralRequest request);

    String pinThread(GeneralRequest request);

    String getThreadParticipants(ThreadParticipantRequest request);

    String removeParticipants(RemoveParticipantsRequest request);

    String addParticipants(AddParticipantsRequest request);

    String closeThread(GeneralRequest request);

    String leaveThread(LeaveThreadRequest request);

    String clearHistory(ClearHistoryRequest request);

    String createThread(CreateThreadRequest request);

    String createThreadWithMessage(CreateThreadWithMessageRequest request);

    String getHistory(GetHistoryRequest request);

    String getUserInfo(GeneralRequest request);

    String registerAssistant(RegisterAssistantRequest request);
}

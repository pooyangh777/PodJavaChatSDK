package chatSdk.chat;

import asyncSdk.model.AsyncMessage;
import chatSdk.dataTransferObject.ChatResponse;
import chatSdk.dataTransferObject.ChatResponse2;
import chatSdk.dataTransferObject.GeneralResponse;
import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.contacts.inPut.Contact;
import chatSdk.dataTransferObject.message.inPut.*;
import chatSdk.dataTransferObject.message.outPut.DeliveryMessageRequest;
import chatSdk.dataTransferObject.system.inPut.Admin;
import chatSdk.dataTransferObject.system.inPut.Error;
import chatSdk.dataTransferObject.system.inPut.ResultSetRole;
import chatSdk.dataTransferObject.system.outPut.ErrorOutPut;
import chatSdk.dataTransferObject.thread.inPut.*;
import chatSdk.dataTransferObject.user.inPut.*;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OnReceiveMessageFactory {
    public ChatListener listener;
    private static final Logger logger = LogManager.getLogger(Chat.class);

    public void onReceivedMessage(AsyncMessage asyncMessage) {
        int messageType = 0;
        ChatMessage chatMessage = GsonFactory.gson.fromJson(asyncMessage.getContent(), ChatMessage.class);
        if (chatMessage != null) {
            messageType = chatMessage.getType();
        }
        logger.info("chatSdk onReceivedMessage" + Objects.requireNonNull(asyncMessage).getContent());
        switch (messageType) {
            case ChatMessageType.CHANGE_TYPE:
                break;
            case ChatMessageType.SENT:
                handleSent(chatMessage);
                break;
            case ChatMessageType.DELIVERY:
                handleDelivery(chatMessage);
                break;
            case ChatMessageType.SEEN:
                handleSeen(chatMessage);
                break;
            case ChatMessageType.ERROR:
                handleError(chatMessage);
                break;
            case ChatMessageType.FORWARD_MESSAGE:
                handleForwardMessage(chatMessage);
                break;
            case ChatMessageType.RELATION_INFO:
            case ChatMessageType.GET_STATUS:
            case ChatMessageType.USER_STATUS:
            case ChatMessageType.SPAM_PV_THREAD:
                break;
            case ChatMessageType.GET_THREADS:
                onGetThreads(chatMessage);
                break;
            case ChatMessageType.REMOVED_FROM_THREAD:
                handleRemoveFromThread(chatMessage);
                break;
            case ChatMessageType.LEAVE_THREAD:
                handleOutPutLeaveThread(chatMessage);
                break;
            case ChatMessageType.MESSAGE:
                handleNewMessage(chatMessage);
                break;
            case ChatMessageType.PING:
                handleOnPing();
                break;
            case ChatMessageType.REMOVE_PARTICIPANT:
                handleOutPutRemoveParticipant(chatMessage);
                break;
            case ChatMessageType.RENAME:
            case ChatMessageType.THREAD_PARTICIPANTS:
                ChatResponse2<ResultParticipant> resultParticipantChatResponse = reformatThreadParticipants(chatMessage);
                String jsonParticipant = GsonFactory.gson.toJson(resultParticipantChatResponse);
                listener.onGetThreadParticipant(jsonParticipant, resultParticipantChatResponse);
                break;
            case ChatMessageType.UN_MUTE_THREAD:
                onUnmuteThread(chatMessage);
                break;
            case ChatMessageType.MUTE_THREAD:
                onMuteThread(chatMessage);
                break;
            case ChatMessageType.UNPIN_THREAD:
                onUnpinThread(chatMessage);
                break;
            case ChatMessageType.PIN_THREAD:
                onPinThread(chatMessage);
                break;
            case ChatMessageType.USER_INFO:
                handleOnUserInfo(chatMessage);
                break;
            case ChatMessageType.DELETE_MESSAGE:
                handleOutPutDeleteMsg(chatMessage);
                break;
            case ChatMessageType.EDIT_MESSAGE:
                handleEditMessage(chatMessage);
                break;
            case ChatMessageType.UPDATE_THREAD_INFO:
                handleUpdateThreadInfo(chatMessage);
                break;
            case ChatMessageType.DELIVERED_MESSAGE_LIST:
                handleOutPutDeliveredMessageList(chatMessage);
                break;
            case ChatMessageType.SEEN_MESSAGE_LIST:
                handleOutPutSeenMessageList(chatMessage);
                break;
            case ChatMessageType.BLOCK:
                onBlock(chatMessage);
                break;
            case ChatMessageType.UNBLOCK:
                onUnblock(chatMessage);
                break;
            case ChatMessageType.GET_BLOCKED:
                onGetBlockList(chatMessage);
                break;
            case ChatMessageType.ADD_PARTICIPANT:
                handleAddParticipant(chatMessage);
                break;
            case ChatMessageType.GET_CONTACTS:
                onGetContacts(chatMessage);
                break;
            case ChatMessageType.CREATE_THREAD:
                onCreateThread(chatMessage);
                break;
            case ChatMessageType.GET_HISTORY:
                onGetHistory(chatMessage);
                break;
            case ChatMessageType.THREAD_INFO_UPDATED:
                handleThreadInfoUpdated(chatMessage);
                break;
            case ChatMessageType.LAST_SEEN_UPDATED:
                handleLastSeenUpdated(chatMessage);
                break;
            case ChatMessageType.SET_ROLE_TO_USER:
                handleSetRole(chatMessage);
                break;
            case ChatMessageType.REMOVE_ROLE_FROM_USER:
                handleRemoveRole(chatMessage);
                break;
            case ChatMessageType.CLEAR_HISTORY:
                handleClearHistory(chatMessage);
                break;
            case ChatMessageType.INTERACT_MESSAGE:
                handleInteractiveMessage(chatMessage);
                break;
        }
    }

    private void handleError(ChatMessage chatMessage) {
        Error serverError = GsonFactory.gson.fromJson(chatMessage.getContent(), Error.class);
        ErrorOutPut error = new ErrorOutPut();
        error.setErrorCode(serverError.getCode());
        error.setErrorMessage(serverError.getMessage());
        error.setHasError(true);
        listener.onError(serverError.getMessage(), error);
    }

    private void handleLastSeenUpdated(ChatMessage chatMessage) {
        listener.onLastSeenUpdated(chatMessage.getContent());
    }

    private void handleThreadInfoUpdated(ChatMessage chatMessage) {
        ResultThread resultThread = new ResultThread();
        Conversation conversation = GsonFactory.gson.fromJson(chatMessage.getContent(), Conversation.class);
        resultThread.setThread(conversation);
        ChatResponse2<ResultThread> chatResponse = new ChatResponse2<>();
        chatResponse.setResult(resultThread);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        listener.onThreadInfoUpdated(chatMessage.getContent(), chatResponse);
    }

    private void handleRemoveFromThread(ChatMessage chatMessage) {
        ChatResponse2<ResultThread> chatResponse = new ChatResponse2<>();
        ResultThread resultThread = new ResultThread();
        Conversation conversation = new Conversation();
        conversation.setId(chatMessage.getSubjectId());
        resultThread.setThread(conversation);
        String content = GsonFactory.gson.toJson(chatResponse);
        listener.OnRemovedFromThread(content, chatResponse);
    }

    /**
     * After the set Token, we send ping for checking client Authenticated or not
     * the (boolean)checkToken is for that reason
     */
    private void handleOnPing() {
    }

    /**
     * When the new message arrived we send the delivery message to the sender user.
     */
    private void handleNewMessage(ChatMessage chatMessage) {
        MessageVO messageVO = GsonFactory.gson.fromJson(chatMessage.getContent(), MessageVO.class);
        ChatResponse2<ResultNewMessage> chatResponse = new ChatResponse2<>();
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        chatResponse.setHasError(false);
        chatResponse.setErrorCode(0);
        chatResponse.setErrorMessage("");
        ResultNewMessage resultNewMessage = new ResultNewMessage();
        resultNewMessage.setMessageVO(messageVO);
        resultNewMessage.setThreadId(chatMessage.getSubjectId());
        chatResponse.setResult(resultNewMessage);
        String json = GsonFactory.gson.toJson(chatResponse);
        long ownerId = 0;
        if (messageVO != null) {
            ownerId = messageVO.getParticipant().getId();
        }

        boolean isMe = ownerId == Chat.getInstance().getUser().getId();
        if (!isMe && messageVO != null) {
            DeliveryMessageRequest request = new DeliveryMessageRequest.Builder()
                    .setMessageId(messageVO.getId())
                    .setThreadId(chatMessage.getSubjectId())
                    .build();
            Chat.getInstance().deliveryMessage(request);
        }
        listener.onNewMessage(json, chatResponse);
    }

    //TODO Problem in message id in forwardMessage
    private void handleSent(ChatMessage chatMessage) {
        ChatResponse2<ResultMessage> chatResponse = new ChatResponse2<>();
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setConversationId(chatMessage.getSubjectId());
        resultMessage.setMessageId(Long.parseLong(chatMessage.getContent()));
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        chatResponse.setResult(resultMessage);
        String json = GsonFactory.gson.toJson(chatResponse);
        listener.onSent(json, chatResponse);
    }

    private void handleSeen(ChatMessage chatMessage) {
        ChatResponse2<ResultMessage> chatResponse = new ChatResponse2<>();
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        chatResponse.setResult(GsonFactory.gson.fromJson(chatMessage.getContent(), ResultMessage.class));
        String json = GsonFactory.gson.toJson(chatResponse);
        listener.onSeen(json, chatResponse);
    }

    private void handleDelivery(ChatMessage chatMessage) {
        ChatResponse2<ResultMessage> chatResponse = new ChatResponse2<>();
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        chatResponse.setResult(GsonFactory.gson.fromJson(chatMessage.getContent(), ResultMessage.class));
        String json = GsonFactory.gson.toJson(chatResponse);
        listener.onDeliver(json, chatResponse);
    }

    private void handleForwardMessage(ChatMessage chatMessage) {
        MessageVO messageVO = GsonFactory.gson.fromJson(chatMessage.getContent(), MessageVO.class);
        ChatResponse2<ResultNewMessage> chatResponse = new ChatResponse2<>();
        ResultNewMessage resultMessage = new ResultNewMessage();
        resultMessage.setThreadId(chatMessage.getSubjectId());
        resultMessage.setMessageVO(messageVO);
        chatResponse.setResult(resultMessage);
        String json = GsonFactory.gson.toJson(chatResponse);
        long ownerId = 0;
        if (messageVO != null) {
            ownerId = messageVO.getParticipant().getId();
        }
        listener.onNewMessage(json, chatResponse);
        boolean isMe = ownerId == Chat.getInstance().getUser().getId();
        if (!isMe && messageVO != null) {
            DeliveryMessageRequest request = new DeliveryMessageRequest.Builder()
                    .setMessageId(messageVO.getId())
                    .setThreadId(chatMessage.getSubjectId())
                    .build();
            Chat.getInstance().deliveryMessage(request);
        }
    }

    private void handleEditMessage(ChatMessage chatMessage) {
        ChatResponse2<ResultNewMessage> chatResponse = new ChatResponse2<>();
        ResultNewMessage newMessage = new ResultNewMessage();
        MessageVO messageVO = GsonFactory.gson.fromJson(chatMessage.getContent(), MessageVO.class);
        newMessage.setMessageVO(messageVO);
        newMessage.setThreadId(chatMessage.getSubjectId());
        chatResponse.setResult(newMessage);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        String content = GsonFactory.gson.toJson(chatResponse);
        listener.onEditedMessage(content, chatResponse);
    }

    private void handleOutPutSeenMessageList(ChatMessage chatMessage) {
        ChatResponse2<ResultParticipant> chatResponse = new ChatResponse2<>();
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        ResultParticipant resultParticipant = new ResultParticipant();
        List<Participant> participants = GsonFactory.gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Participant>>() {
        }.getType());
        resultParticipant.setParticipants(participants);
        resultParticipant.setContentCount(chatMessage.getContentCount());
        chatResponse.setResult(resultParticipant);
        String content = GsonFactory.gson.toJson(chatResponse);
        listener.OnSeenMessageList(content, chatResponse);
    }


    private void handleOutPutDeliveredMessageList(ChatMessage chatMessage) {
        ChatResponse2<ResultParticipant> chatResponse = new ChatResponse2<>();
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        ResultParticipant resultParticipant = new ResultParticipant();
        List<Participant> participants = GsonFactory.gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Participant>>() {
        }.getType());
        resultParticipant.setParticipants(participants);
        resultParticipant.setContentCount(chatMessage.getContentCount());
        chatResponse.setResult(resultParticipant);
        String content = GsonFactory.gson.toJson(chatResponse);
        listener.OnDeliveredMessageList(content, chatResponse);
    }

    private void handleUpdateThreadInfo(ChatMessage chatMessage) {
        ChatResponse2<ResultThread> chatResponse = new ChatResponse2<>();
        Conversation conversation = GsonFactory.gson.fromJson(chatMessage.getContent(), Conversation.class);
        ResultThread resultThread = new ResultThread();
        resultThread.setThread(conversation);
        chatResponse.setErrorCode(0);
        chatResponse.setErrorMessage("");
        chatResponse.setHasError(false);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        chatResponse.setResult(resultThread);
        String threadJson = GsonFactory.gson.toJson(chatResponse);
        listener.onUpdateThreadInfo(threadJson, chatResponse);
    }


    private void handleOutPutLeaveThread(ChatMessage chatMessage) {
        ChatResponse2<ResultLeaveThread> chatResponse = new ChatResponse2<>();
        ResultLeaveThread leaveThread = GsonFactory.gson.fromJson(chatMessage.getContent(), ResultLeaveThread.class);
        long threadId = chatMessage.getSubjectId();
        leaveThread.setThreadId(threadId);
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setErrorMessage("");
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        chatResponse.setResult(leaveThread);
        String jsonThread = GsonFactory.gson.toJson(chatResponse);
        listener.onThreadLeaveParticipant(jsonThread, chatResponse);
    }

    private void handleAddParticipant(ChatMessage chatMessage) {
        Conversation conversation = GsonFactory.gson.fromJson(chatMessage.getContent(), Conversation.class);
        ChatResponse2<ResultAddParticipant> chatResponse = new ChatResponse2<>();
        ResultAddParticipant resultAddParticipant = new ResultAddParticipant();
        resultAddParticipant.setThread(conversation);
        chatResponse.setErrorCode(0);
        chatResponse.setErrorMessage("");
        chatResponse.setHasError(false);
        chatResponse.setResult(resultAddParticipant);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        String jsonAddParticipant = GsonFactory.gson.toJson(chatResponse);
        listener.onThreadAddParticipant(jsonAddParticipant, chatResponse);
    }

    private void handleOutPutDeleteMsg(ChatMessage chatMessage) {
        ChatResponse2<ResultDeleteMessage> chatResponse = new ChatResponse2<>();
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        MessageVO messageVO = GsonFactory.gson.fromJson(chatMessage.getContent(), MessageVO.class);
//        long messageId = Long.parseLong(chatMessage.getContent());
        ResultDeleteMessage resultDeleteMessage = new ResultDeleteMessage();
        DeleteMessageContent deleteMessage = new DeleteMessageContent();
        deleteMessage.setId(messageVO.getId());
        resultDeleteMessage.setDeletedMessage(deleteMessage);
        chatResponse.setResult(resultDeleteMessage);
        String jsonDeleteMsg = GsonFactory.gson.toJson(chatResponse);
        listener.onDeleteMessage(jsonDeleteMsg, chatResponse);
    }

    private void handleClearHistory(ChatMessage chatMessage) {
        ChatResponse2<ResultClearHistory> chatResponseClrHistory = new ChatResponse2<>();
        ResultClearHistory resultClearHistory = new ResultClearHistory();
        long clrHistoryThreadId = GsonFactory.gson.fromJson(chatMessage.getContent(), Long.class);
        resultClearHistory.setThreadId(clrHistoryThreadId);
        chatResponseClrHistory.setResult(resultClearHistory);
        chatResponseClrHistory.setUniqueId(chatMessage.getUniqueId());
        String jsonClrHistory = GsonFactory.gson.toJson(chatResponseClrHistory);
        listener.OnClearHistory(jsonClrHistory, chatResponseClrHistory);
    }

    private void handleInteractiveMessage(ChatMessage chatMessage) {
        ChatResponse2<ResultInteractMessage> responseInteractMessage = new ChatResponse2<>();
        ResultInteractMessage resultInteractMessage = GsonFactory.gson.fromJson(chatMessage.getContent(), ResultInteractMessage.class);
        responseInteractMessage.setResult(resultInteractMessage);
        String jsonResponse = GsonFactory.gson.toJson(responseInteractMessage);
        listener.OnInteractMessage(jsonResponse, responseInteractMessage);
    }

    private void handleSetRole(ChatMessage chatMessage) {
        ChatResponse2<ResultSetRole> chatResponse = new ChatResponse2<>();
        ResultSetRole resultSetRole = new ResultSetRole();
        ArrayList<Admin> admins = GsonFactory.gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Admin>>() {
        }.getType());
        resultSetRole.setAdmins(admins);
        chatResponse.setResult(resultSetRole);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        String responseJson = GsonFactory.gson.toJson(chatResponse);
        listener.OnSetRole(responseJson, chatResponse);
    }

    private void handleRemoveRole(ChatMessage chatMessage) {
        ChatResponse2<ResultSetRole> chatResponse = new ChatResponse2<>();
        ResultSetRole resultSetRole = new ResultSetRole();
        ArrayList<Admin> admins = GsonFactory.gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Admin>>() {
        }.getType());
        resultSetRole.setAdmins(admins);
        chatResponse.setResult(resultSetRole);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        String responseJson = GsonFactory.gson.toJson(chatResponse);
        listener.OnRemoveRole(responseJson, chatResponse);
    }

    private void handleOutPutRemoveParticipant(ChatMessage chatMessage) {
        ChatResponse2<ResultParticipant> chatResponse = reformatThreadParticipants(chatMessage);
        String jsonRmParticipant = GsonFactory.gson.toJson(chatResponse);
        listener.onThreadRemoveParticipant(jsonRmParticipant, chatResponse);
    }

    private void handleOnUserInfo(ChatMessage chatMessage) {
        ChatResponse2<ResultUserInfo> chatResponse = new ChatResponse2<>();
        UserInfo userInfo = GsonFactory.gson.fromJson(chatMessage.getContent(), UserInfo.class);
        String userInfoJson = reformatUserInfo(chatMessage, chatResponse, userInfo);
        listener.onUserInfo(userInfoJson, chatResponse);
    }

    private String reformatUserInfo(ChatMessage chatMessage, ChatResponse2<ResultUserInfo> outPutUserInfo, UserInfo userInfo) {
        ResultUserInfo result = new ResultUserInfo();
        Chat.getInstance().setUser(userInfo);
        result.setUser(userInfo);
        outPutUserInfo.setErrorCode(0);
        outPutUserInfo.setErrorMessage("");
        outPutUserInfo.setHasError(false);
        outPutUserInfo.setResult(result);
        outPutUserInfo.setUniqueId(chatMessage.getUniqueId());
        return GsonFactory.gson.toJson(outPutUserInfo);
    }

    /**
     * The replacement method is getMessageDeliveredList.
     */

    //TODO ChatContract cache
    //TODO Check if participant is null!!!
    private ChatResponse2<ResultParticipant> reformatThreadParticipants(ChatMessage chatMessage) {
        ArrayList<Participant> participants = GsonFactory.gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Participant>>() {
        }.getType());

        ChatResponse2<ResultParticipant> outPutParticipant = new ChatResponse2<>();
        outPutParticipant.setErrorCode(0);
        outPutParticipant.setErrorMessage("");
        outPutParticipant.setHasError(false);
        outPutParticipant.setUniqueId(chatMessage.getUniqueId());
        ResultParticipant resultParticipant = new ResultParticipant();
        resultParticipant.setContentCount(chatMessage.getContentCount());
        resultParticipant.setParticipants(participants);
        outPutParticipant.setResult(resultParticipant);
        return outPutParticipant;
    }

    private void onGetThreads(ChatMessage chatMessage) {
        ConversationResponse[] threads = GsonFactory.gson.fromJson(chatMessage.getContent(), ConversationResponse[].class);
        ChatResponse<ConversationResponse[]> response = new ChatResponse<>();
        response.setResult(threads);
        response.setUniqueId(chatMessage.getUniqueId());
        response.setSubjectId(chatMessage.getSubjectId());
        listener.onGetThread(response);
    }

    private void onGetHistory(ChatMessage chatMessage) {
        MessageVO[] messages = GsonFactory.gson.fromJson(chatMessage.getContent(), MessageVO[].class);
        ChatResponse<MessageVO[]> response = new ChatResponse<>();
        response.setResult(messages);
        response.setSubjectId(chatMessage.getSubjectId());
        listener.onGetHistory(response);
    }

    private void onGetContacts(ChatMessage chatMessage) {
        Contact[] contacts = GsonFactory.gson.fromJson(chatMessage.getContent(), Contact[].class);
        ChatResponse<Contact[]> response = new ChatResponse<>();
        response.setResult(contacts);
        response.setSubjectId(chatMessage.getSubjectId());
        listener.onGetContacts2(response);
    }

    private void onMuteThread(ChatMessage chatMessage) {
        ChatResponse<GeneralResponse> response = new ChatResponse<>();
        response.setSubjectId(chatMessage.getSubjectId());
        listener.onMuteThread(response);
    }

    private void onUnmuteThread(ChatMessage chatMessage) {
        ChatResponse<GeneralResponse> response = new ChatResponse<>();
        response.setSubjectId(chatMessage.getSubjectId());
        listener.onUnmuteThread(response);
    }

    private void onPinThread(ChatMessage chatMessage) {
        ChatResponse<GeneralResponse> response = new ChatResponse<>();
        response.setSubjectId(chatMessage.getSubjectId());
        listener.onPinThread(response);
    }

    private void onUnpinThread(ChatMessage chatMessage) {
        ChatResponse<GeneralResponse> response = new ChatResponse<>();
        response.setSubjectId(chatMessage.getSubjectId());
        listener.onUnPinThread(response);
    }

    private void onCreateThread(ChatMessage chatMessage) {
        ConversationResponse conversationResponse = GsonFactory.gson.fromJson(chatMessage.getContent(), ConversationResponse.class);
        ChatResponse<ConversationResponse> response = new ChatResponse<>();
        response.setSubjectId(chatMessage.getSubjectId());
        response.setResult(conversationResponse);
        listener.onCreateThread(response);
    }

    private void onBlock(ChatMessage chatMessage) {
        Contact contact = GsonFactory.gson.fromJson(chatMessage.getContent(), Contact.class);
        ChatResponse<Contact> response = new ChatResponse<>();
        response.setSubjectId(chatMessage.getSubjectId());
        response.setResult(contact);
        listener.onBlock(response);
    }

    private void onUnblock(ChatMessage chatMessage) {
        Contact contact = GsonFactory.gson.fromJson(chatMessage.getContent(), Contact.class);
        ChatResponse<Contact> response = new ChatResponse<>();
        response.setSubjectId(response.getSubjectId());
        response.setResult(contact);
        listener.onUnblock(response);
    }

    private void onGetBlockList(ChatMessage chatMessage) {
        Contact[] contacts = GsonFactory.gson.fromJson(chatMessage.getContent(), Contact[].class);
        ChatResponse<Contact[]> response = new ChatResponse<>();
        response.setSubjectId(chatMessage.getSubjectId());
        response.setResult(contacts);
        listener.onGetBlockList(response);
    }
}
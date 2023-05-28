package chatSdk.chat;

import asyncSdk.model.AsyncMessage;
import chatSdk.dataTransferObject.ChatResponse;
import chatSdk.dataTransferObject.ChatResponse2;
import chatSdk.dataTransferObject.GeneralResponse;
import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.chat.ChatState;
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
import org.springframework.util.NumberUtils;

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
                onSent(chatMessage);
                break;
            case ChatMessageType.DELIVERY:
                onDelivered(chatMessage);
                break;
            case ChatMessageType.SEEN:
                onSeen(chatMessage);
                break;
            case ChatMessageType.ERROR:
                handleError(chatMessage);
                break;
            case ChatMessageType.FORWARD_MESSAGE:
                onForwardMessage(chatMessage);
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
                onRemoveFromThread(chatMessage);
                break;
            case ChatMessageType.LEAVE_THREAD:
                onLeaveThread(chatMessage);
                break;
            case ChatMessageType.MESSAGE:
                onNewMessage(chatMessage);
                break;
            case ChatMessageType.PING:
                handleOnPing();
                break;
            case ChatMessageType.REMOVE_PARTICIPANT:
                onRemoveParticipants(chatMessage);
                break;
            case ChatMessageType.RENAME:
            case ChatMessageType.THREAD_PARTICIPANTS:
                onGetParticipants(chatMessage);
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
                onUserInfo(chatMessage);
                break;
            case ChatMessageType.DELETE_MESSAGE:
                onDeleteMessage(chatMessage);
                break;
            case ChatMessageType.EDIT_MESSAGE:
                onEditMessage(chatMessage);
                break;
            case ChatMessageType.UPDATE_THREAD_INFO:
                onUpdateThreadInfo(chatMessage);
                break;
            case ChatMessageType.DELIVERED_MESSAGE_LIST:
                onDeliveredMessageList(chatMessage);
                break;
            case ChatMessageType.SEEN_MESSAGE_LIST:
                onSeenMessageList(chatMessage);
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
                onAddParticipants(chatMessage);
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
                onClearHistory(chatMessage);
                break;
            case ChatMessageType.INTERACT_MESSAGE:
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
//        listener.onThreadInfoUpdated(chatMessage.getContent(), chatResponse);
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
        Message messageVO = GsonFactory.gson.fromJson(chatMessage.getContent(), Message.class);
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
//        listener.onNewMessage(json, chatResponse);
    }

//    //TODO Problem in message id in forwardMessage
//    private void handleSent(ChatMessage chatMessage) {
//        ChatResponse2<ResultMessage> chatResponse = new ChatResponse2<>();
//        ResultMessage resultMessage = new ResultMessage();
//        resultMessage.setConversationId(chatMessage.getSubjectId());
//        resultMessage.setMessageId(Long.parseLong(chatMessage.getContent()));
//        chatResponse.setUniqueId(chatMessage.getUniqueId());
//        chatResponse.setResult(resultMessage);
//        String json = GsonFactory.gson.toJson(chatResponse);
//        listener.onSent(json, chatResponse);
//    }

//    private void handleSeen(ChatMessage chatMessage) {
//        ChatResponse2<ResultMessage> chatResponse = new ChatResponse2<>();
//        chatResponse.setUniqueId(chatMessage.getUniqueId());
//        chatResponse.setResult(GsonFactory.gson.fromJson(chatMessage.getContent(), ResultMessage.class));
//        String json = GsonFactory.gson.toJson(chatResponse);
//        listener.onSeen(json, chatResponse);
//    }
//
//    private void handleDelivery(ChatMessage chatMessage) {
//        ChatResponse2<ResultMessage> chatResponse = new ChatResponse2<>();
//        chatResponse.setUniqueId(chatMessage.getUniqueId());
//        chatResponse.setResult(GsonFactory.gson.fromJson(chatMessage.getContent(), ResultMessage.class));
//        String json = GsonFactory.gson.toJson(chatResponse);
//        listener.onDelivered(json, chatResponse);
//    }

    private void handleForwardMessage(ChatMessage chatMessage) {
        Message messageVO = GsonFactory.gson.fromJson(chatMessage.getContent(), Message.class);
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
//        listener.onNewMessage(json, chatResponse);
        boolean isMe = ownerId == Chat.getInstance().getUser().getId();
        if (!isMe && messageVO != null) {
            DeliveryMessageRequest request = new DeliveryMessageRequest.Builder()
                    .setMessageId(messageVO.getId())
                    .setThreadId(chatMessage.getSubjectId())
                    .build();
            Chat.getInstance().deliveryMessage(request);
        }
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
//        listener.OnSeenMessageList(content, chatResponse);
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
//        listener.OnDeliveredMessageList(content, chatResponse);
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
//        listener.onUpdateThreadInfo(threadJson, chatResponse);
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
//        listener.onThreadLeaveParticipant(jsonThread, chatResponse);
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
//        listener.onThreadAddParticipant(jsonAddParticipant, chatResponse);
    }

    private void handleClearHistory(ChatMessage chatMessage) {
        ChatResponse2<ResultClearHistory> chatResponseClrHistory = new ChatResponse2<>();
        ResultClearHistory resultClearHistory = new ResultClearHistory();
        long clrHistoryThreadId = GsonFactory.gson.fromJson(chatMessage.getContent(), Long.class);
        resultClearHistory.setThreadId(clrHistoryThreadId);
        chatResponseClrHistory.setResult(resultClearHistory);
        chatResponseClrHistory.setUniqueId(chatMessage.getUniqueId());
        String jsonClrHistory = GsonFactory.gson.toJson(chatResponseClrHistory);
//        listener.OnClearHistory(jsonClrHistory, chatResponseClrHistory);
    }

//    private void handleInteractiveMessage(ChatMessage chatMessage) {
//        ChatResponse2<ResultInteractMessage> responseInteractMessage = new ChatResponse2<>();
//        ResultInteractMessage resultInteractMessage = GsonFactory.gson.fromJson(chatMessage.getContent(), ResultInteractMessage.class);
//        responseInteractMessage.setResult(resultInteractMessage);
//        String jsonResponse = GsonFactory.gson.toJson(responseInteractMessage);
//        listener.OnInteractMessage(jsonResponse, responseInteractMessage);
//    }

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

//    private void handleOutPutRemoveParticipant(ChatMessage chatMessage) {
//        ChatResponse2<ResultParticipant> chatResponse = reformatThreadParticipants(chatMessage);
////        String jsonRmParticipant = GsonFactory.gson.toJson(chatResponse);
////        listener.onThreadRemoveParticipant(jsonRmParticipant, chatResponse);
//    }

    private void onGetThreads(ChatMessage chatMessage) {
        ChatResponse<Conversation[]> response = decodedResponse(Conversation[].class, chatMessage);
        listener.onGetThread(response);
    }

    private void onGetHistory(ChatMessage chatMessage) {
        ChatResponse<Message[]> response = decodedResponse(Message[].class, chatMessage);
        listener.onGetHistory(response);
    }

    private void onGetContacts(ChatMessage chatMessage) {
        ChatResponse<Contact[]> response = decodedResponse(Contact[].class, chatMessage);
        listener.onGetContacts(response);
    }

    private void onMuteThread(ChatMessage chatMessage) {
        ChatResponse<GeneralResponse> response = decodedResponse(GeneralResponse.class, chatMessage);
        listener.onMuteThread(response);
    }

    private void onUnmuteThread(ChatMessage chatMessage) {
        ChatResponse<GeneralResponse> response = decodedResponse(GeneralResponse.class, chatMessage);
        listener.onUnmuteThread(response);
    }

    private void onPinThread(ChatMessage chatMessage) {
        ChatResponse<GeneralResponse> response = decodedResponse(GeneralResponse.class, chatMessage);
        listener.onPinThread(response);
    }

    private void onUnpinThread(ChatMessage chatMessage) {
        ChatResponse<GeneralResponse> response = decodedResponse(GeneralResponse.class, chatMessage);
        listener.onUnPinThread(response);
    }

    private void onCreateThread(ChatMessage chatMessage) {
        ChatResponse<Conversation> response = decodedResponse(Conversation.class, chatMessage);
        listener.onCreateThread(response);
    }

    private void onBlock(ChatMessage chatMessage) {
        ChatResponse<Contact> response = decodedResponse(Contact.class, chatMessage);
        listener.onBlock(response);
    }

    private void onUnblock(ChatMessage chatMessage) {
        ChatResponse<Contact> response = decodedResponse(Contact.class, chatMessage);
        listener.onUnblock(response);
    }

    private void onGetBlockList(ChatMessage chatMessage) {
        ChatResponse<Contact[]> response = decodedResponse(Contact[].class, chatMessage);
        listener.onGetBlockList(response);
    }

    private void onGetParticipants(ChatMessage chatMessage) {
        ChatResponse<Participant[]> response = decodedResponse(Participant[].class, chatMessage);
        listener.onGetThreadParticipant2(response);
    }

    private void onEditMessage(ChatMessage chatMessage) {
        ChatResponse<Message> response = decodedResponse(Message.class, chatMessage);
        listener.onEditedMessage(response);
    }

    private void onUserInfo(ChatMessage chatMessage) {
        ChatResponse<UserInfo> response = decodedResponse(UserInfo.class, chatMessage);
        if (Chat.getInstance().getUser() == null) {
            Chat.getInstance().setUser(response.getResult());
            Chat.getInstance().setState(ChatState.ChatReady);
            listener.onChatState(ChatState.ChatReady);
        }
        listener.onUserInfo(response);
    }

    private void onDeleteMessage(ChatMessage chatMessage) {
        ChatResponse<Message> response = decodedResponse(Message.class, chatMessage);
        listener.onDeleteMessage(response);
    }

    private void onSent(ChatMessage chatMessage) {
        listener.onSent(decodeMessageResult(chatMessage));
    }

    private void onDelivered(ChatMessage chatMessage) {
        listener.onDelivered(decodeMessageResult(chatMessage));
    }

    private void onSeen(ChatMessage chatMessage) {
        listener.onSeen(decodeMessageResult(chatMessage));
    }

    private ChatResponse<ResultMessage> decodeMessageResult(ChatMessage chatMessage) {
        ResultMessage resultMessage = new ResultMessage();
        try {
            resultMessage.setMessageId(NumberUtils.parseNumber(chatMessage.getContent(), Long.class));
        } catch (Exception e) {
            System.out.println("Error to convert to Long");
        }
        resultMessage.setConversationId(chatMessage.getSubjectId());
        resultMessage.setMessageTime(chatMessage.getTime());
        ChatResponse<ResultMessage> response = new ChatResponse<>();
        response.setResult(resultMessage);
        return response;
    }

    private void onClearHistory(ChatMessage chatMessage) {
        ChatResponse<GeneralResponse> response = decodedResponse(GeneralResponse.class, chatMessage);
        listener.onClearHistory(response);
    }

    private void onAddParticipants(ChatMessage chatMessage) {
        ChatResponse<Conversation> response = decodedResponse(Conversation.class, chatMessage);
        listener.onThreadAddParticipant(response);
    }

    private void onRemoveParticipants(ChatMessage chatMessage) {
        ChatResponse<Participant[]> response = decodedResponse(Participant[].class, chatMessage);
        listener.onThreadRemoveParticipant(response);
    }

    private void onLeaveThread(ChatMessage chatMessage) {
        ChatResponse<ResultLeaveThread> response = decodedResponse(ResultLeaveThread.class, chatMessage);
        listener.onThreadLeaveParticipant(response);
    }

    private void onDeliveredMessageList(ChatMessage chatMessage) {
        ChatResponse<Participant[]> response = decodedResponse(Participant[].class, chatMessage);
        listener.OnDeliveredMessageList(response);
    }

    private void onSeenMessageList(ChatMessage chatMessage) {
        ChatResponse<Participant[]> response = decodedResponse(Participant[].class, chatMessage);
        listener.OnSeenMessageList(response);
    }

    private void onNewMessage(ChatMessage chatMessage) {
        ChatResponse<Message> response = decodedResponse(Message.class, chatMessage);
        Long ownerId = 0L;
        if (response.getResult() != null) {
            ownerId = response.getResult().getParticipant().getId();
        }
        boolean isMe = true;
        if (Chat.getInstance().getUser().getId() != null) {
            isMe = Objects.equals(ownerId, Chat.getInstance().getUser().getId());
        }
        if (!isMe && response.getResult() != null) {
            DeliveryMessageRequest request = new DeliveryMessageRequest.Builder()
                    .setMessageId(response.getResult().getId())
                    .setThreadId(chatMessage.getSubjectId())
                    .build();
            Chat.getInstance().deliveryMessage(request);
        }
        listener.onNewMessage(response);
    }

    private void onUpdateThreadInfo(ChatMessage chatMessage) {
        ChatResponse<Conversation> response = decodedResponse(Conversation.class, chatMessage);
        listener.onUpdateThreadInfo(response);
    }

    private void onForwardMessage(ChatMessage chatMessage) {
        ChatResponse<Message> response = decodedResponse(Message.class, chatMessage);
        Long ownerId = 0L;
        if (response != null) {
            ownerId = response.getResult().getParticipant().getId();
        }
        boolean isMe = true;
        if (Chat.getInstance().getUser().getId() != null) {
            isMe = Objects.equals(ownerId, Chat.getInstance().getUser().getId());
        }
        if (!isMe && response != null) {
            DeliveryMessageRequest request = new DeliveryMessageRequest.Builder()
                    .setMessageId(response.getResult().getId())
                    .setThreadId(chatMessage.getSubjectId())
                    .build();
            Chat.getInstance().deliveryMessage(request);
        }
        listener.onNewMessage(response);
    }

    //dont work correct
    private void onRemoveFromThread(ChatMessage chatMessage) {
        ChatResponse<Conversation> response = decodedResponse(Conversation.class, chatMessage);
        listener.onRemovedFromThread(response);
    }

    private <T> ChatResponse<T> decodedResponse(Class<T> type, ChatMessage chatMessage) {
        T decodedContent = GsonFactory.gson.fromJson(chatMessage.getContent(), type);
        ChatResponse<T> response = new ChatResponse<>();
        response.setResult(decodedContent);
        response.setUniqueId(chatMessage.getUniqueId());
        response.setSubjectId(chatMessage.getSubjectId());
        response.setTime(chatMessage.getTime());
        response.setContentCount(chatMessage.getContentCount());
        return response;
    }
}
package chatSdk.chat;

import asyncSdk.model.AsyncMessage;
import chatSdk.dataTransferObject.ChatResponse;
import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.contacts.inPut.Contact;
import chatSdk.dataTransferObject.contacts.inPut.ResultContact;
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

import static asyncSdk.model.AsyncMessageType.Message;

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
                handleGetThreads(chatMessage);
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
                ChatResponse<ResultParticipant> resultParticipantChatResponse = reformatThreadParticipants(chatMessage);
                String jsonParticipant = GsonFactory.gson.toJson(resultParticipantChatResponse);
                listener.onGetThreadParticipant(jsonParticipant, resultParticipantChatResponse);
                break;
            case ChatMessageType.UN_MUTE_THREAD:
                handleUnMuteThread(chatMessage);
                break;
            case ChatMessageType.MUTE_THREAD:
                handleMuteThread(chatMessage);
                break;
            case ChatMessageType.UNPIN_THREAD:
                handleUnpinThread(chatMessage);
                break;
            case ChatMessageType.PIN_THREAD:
                handlePinThread(chatMessage);
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
                handleOutPutBlock(chatMessage);
                break;
            case ChatMessageType.UNBLOCK:
                handleUnBlock(chatMessage);
                break;
            case ChatMessageType.GET_BLOCKED:
                handleOutPutGetBlockList(chatMessage);
                break;
            case ChatMessageType.ADD_PARTICIPANT:
                handleAddParticipant(chatMessage);
                break;
            case ChatMessageType.GET_CONTACTS:
                handleGetContact(chatMessage);
                break;
            case ChatMessageType.CREATE_THREAD:
                handleCreateThread(chatMessage);
                break;
            case ChatMessageType.GET_HISTORY:
                handleOutPutGetHistory(chatMessage);
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
        ChatResponse<ResultThread> chatResponse = new ChatResponse<>();
        chatResponse.setResult(resultThread);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        listener.onThreadInfoUpdated(chatMessage.getContent(), chatResponse);
    }

    private void handleRemoveFromThread(ChatMessage chatMessage) {
        ChatResponse<ResultThread> chatResponse = new ChatResponse<>();
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
            ChatResponse<ResultNewMessage> chatResponse = new ChatResponse<>();
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
        ChatResponse<ResultMessage> chatResponse = new ChatResponse<>();
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setConversationId(chatMessage.getSubjectId());
        resultMessage.setMessageId(Long.parseLong(chatMessage.getContent()));
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        chatResponse.setResult(resultMessage);
        String json = GsonFactory.gson.toJson(chatResponse);
        listener.onSent(json, chatResponse);
    }

    private void handleSeen(ChatMessage chatMessage) {
        ChatResponse<ResultMessage> chatResponse = new ChatResponse<>();
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        chatResponse.setResult(GsonFactory.gson.fromJson(chatMessage.getContent(), ResultMessage.class));
        String json = GsonFactory.gson.toJson(chatResponse);
        listener.onSeen(json, chatResponse);
    }

    private void handleDelivery(ChatMessage chatMessage) {
        ChatResponse<ResultMessage> chatResponse = new ChatResponse<>();
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        chatResponse.setResult(GsonFactory.gson.fromJson(chatMessage.getContent(), ResultMessage.class));
        String json = GsonFactory.gson.toJson(chatResponse);
        listener.onDeliver(json, chatResponse);
    }

    private void handleForwardMessage(ChatMessage chatMessage) {
        MessageVO messageVO = GsonFactory.gson.fromJson(chatMessage.getContent(), MessageVO.class);
        ChatResponse<ResultNewMessage> chatResponse = new ChatResponse<>();
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

    private void handleUnpinThread(ChatMessage chatMessage) {
        ChatResponse<ResultPin> resultUnPinChatResponse = new ChatResponse<>();
        listener.onUnPin(chatMessage.getContent(), resultUnPinChatResponse);
    }

    private void handlePinThread(ChatMessage chatMessage) {
        ChatResponse<ResultPin> resultPinChatResponse = new ChatResponse<>();
        listener.onPin(chatMessage.getContent(), resultPinChatResponse);
    }

    private void handleUnMuteThread(ChatMessage chatMessage) {
        ChatResponse<ResultMute> chatResponseTemp = new ChatResponse<>();
        listener.onMuteThread(chatMessage.getContent(), chatResponseTemp);
    }

    private void handleMuteThread(ChatMessage chatMessage) {
        ChatResponse<ResultMute> chatResponse = new ChatResponse<>();
        listener.onUnmuteThread(chatMessage.getContent(), chatResponse);
    }

    private void handleEditMessage(ChatMessage chatMessage) {
        ChatResponse<ResultNewMessage> chatResponse = new ChatResponse<>();
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
            ChatResponse<ResultParticipant> chatResponse = new ChatResponse<>();
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
            ChatResponse<ResultParticipant> chatResponse = new ChatResponse<>();
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

    private void handleGetContact(ChatMessage chatMessage) {
        ChatResponse<ResultContact> chatResponse = reformatGetContactResponse(chatMessage);
        String contactJson = GsonFactory.gson.toJson(chatResponse);
        listener.onGetContacts(contactJson, chatResponse);
    }

    private void handleCreateThread(ChatMessage chatMessage) {
        ChatResponse<ResultThread> chatResponse = reformatCreateThread(chatMessage);
        String inviteJson = GsonFactory.gson.toJson(chatResponse);
        listener.onCreateThread(inviteJson, chatResponse);
    }

    private void handleGetThreads(ChatMessage chatMessage) {
        ChatResponse<ResultThreads> chatResponse = reformatGetThreadsResponse(chatMessage);
        String threadJson = GsonFactory.gson.toJson(chatResponse);
        listener.onGetThread(threadJson, chatResponse);
    }

    private void handleUpdateThreadInfo(ChatMessage chatMessage) {
        ChatResponse<ResultThread> chatResponse = new ChatResponse<>();
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
        ChatResponse<ResultLeaveThread> chatResponse = new ChatResponse<>();
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
        ChatResponse<ResultAddParticipant> chatResponse = new ChatResponse<>();
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
        ChatResponse<ResultDeleteMessage> chatResponse = new ChatResponse<>();
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

    private void handleOutPutBlock(ChatMessage chatMessage) {
        Contact contact = GsonFactory.gson.fromJson(chatMessage.getContent(), Contact.class);
        ChatResponse<ResultBlock> chatResponse = new ChatResponse<>();
        ResultBlock resultBlock = new ResultBlock();
        resultBlock.setContact(contact);
        chatResponse.setResult(resultBlock);
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        String jsonBlock = GsonFactory.gson.toJson(chatResponse);
        listener.onBlock(jsonBlock, chatResponse);
    }


    private void handleClearHistory(ChatMessage chatMessage) {
        ChatResponse<ResultClearHistory> chatResponseClrHistory = new ChatResponse<>();
        ResultClearHistory resultClearHistory = new ResultClearHistory();
        long clrHistoryThreadId = GsonFactory.gson.fromJson(chatMessage.getContent(), Long.class);
        resultClearHistory.setThreadId(clrHistoryThreadId);
        chatResponseClrHistory.setResult(resultClearHistory);
        chatResponseClrHistory.setUniqueId(chatMessage.getUniqueId());
        String jsonClrHistory = GsonFactory.gson.toJson(chatResponseClrHistory);
        listener.OnClearHistory(jsonClrHistory, chatResponseClrHistory);
    }

    private void handleInteractiveMessage(ChatMessage chatMessage) {
        ChatResponse<ResultInteractMessage> responseInteractMessage = new ChatResponse<>();
        ResultInteractMessage resultInteractMessage = GsonFactory.gson.fromJson(chatMessage.getContent(), ResultInteractMessage.class);
        responseInteractMessage.setResult(resultInteractMessage);
        String jsonResponse = GsonFactory.gson.toJson(responseInteractMessage);
        listener.OnInteractMessage(jsonResponse, responseInteractMessage);
    }

    private void handleSetRole(ChatMessage chatMessage) {
        ChatResponse<ResultSetRole> chatResponse = new ChatResponse<>();
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
        ChatResponse<ResultSetRole> chatResponse = new ChatResponse<>();
        ResultSetRole resultSetRole = new ResultSetRole();
        ArrayList<Admin> admins = GsonFactory.gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Admin>>() {
        }.getType());
        resultSetRole.setAdmins(admins);
        chatResponse.setResult(resultSetRole);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        String responseJson = GsonFactory.gson.toJson(chatResponse);
        listener.OnRemoveRole(responseJson, chatResponse);
    }

    private void handleUnBlock(ChatMessage chatMessage) {
        Contact contact = GsonFactory.gson.fromJson(chatMessage.getContent(), Contact.class);
        ChatResponse<ResultBlock> chatResponse = new ChatResponse<>();
        ResultBlock resultBlock = new ResultBlock();
        resultBlock.setContact(contact);
        chatResponse.setResult(resultBlock);
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        String jsonUnBlock = GsonFactory.gson.toJson(chatResponse);
        listener.onUnBlock(jsonUnBlock, chatResponse);
    }

    private void handleOutPutGetBlockList(ChatMessage chatMessage) {
        ChatResponse<ResultBlockList> chatResponse = new ChatResponse<>();
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        ResultBlockList resultBlockList = new ResultBlockList();
        List<Contact> contacts = GsonFactory.gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Contact>>() {
        }.getType());
        resultBlockList.setContentCount(chatMessage.getContentCount());
        resultBlockList.setContentCount(chatMessage.getContentCount());
        resultBlockList.setContacts(contacts);
        chatResponse.setResult(resultBlockList);
        String jsonGetBlock = GsonFactory.gson.toJson(chatResponse);
        listener.onGetBlockList(jsonGetBlock, chatResponse);
    }

    private void handleOutPutRemoveParticipant(ChatMessage chatMessage) {
        ChatResponse<ResultParticipant> chatResponse = reformatThreadParticipants(chatMessage);
        String jsonRmParticipant = GsonFactory.gson.toJson(chatResponse);
        listener.onThreadRemoveParticipant(jsonRmParticipant, chatResponse);
    }

    private void handleOnUserInfo(ChatMessage chatMessage) {
        ChatResponse<ResultUserInfo> chatResponse = new ChatResponse<>();
        UserInfo userInfo = GsonFactory.gson.fromJson(chatMessage.getContent(), UserInfo.class);
        String userInfoJson = reformatUserInfo(chatMessage, chatResponse, userInfo);
        listener.onUserInfo(userInfoJson, chatResponse);
    }

    private String reformatUserInfo(ChatMessage chatMessage, ChatResponse<ResultUserInfo> outPutUserInfo, UserInfo userInfo) {
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

    private void handleOutPutGetHistory(ChatMessage chatMessage) {
        List<MessageVO> messageVOS = GsonFactory.gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<MessageVO>>() {
        }.getType());
        ResultHistory resultHistory = new ResultHistory();
        ChatResponse<ResultHistory> chatResponse = new ChatResponse<>();
        resultHistory.setContentCount(chatMessage.getContentCount());
        resultHistory.setHistory(messageVOS);
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setErrorMessage("");
        chatResponse.setResult(resultHistory);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        String json = GsonFactory.gson.toJson(chatResponse);
        listener.onGetHistory(json, chatResponse);
    }

    /**
     * The replacement method is getMessageDeliveredList.
     */

    //TODO ChatContract cache
    //TODO Check if participant is null!!!
    private ChatResponse<ResultParticipant> reformatThreadParticipants(ChatMessage chatMessage) {
        ArrayList<Participant> participants = GsonFactory.gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Participant>>() {
        }.getType());

        ChatResponse<ResultParticipant> outPutParticipant = new ChatResponse<>();
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

    private ChatResponse<ResultThread> reformatCreateThread(ChatMessage chatMessage) {
        ChatResponse<ResultThread> chatResponse = new ChatResponse<>();
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        ResultThread resultThread = new ResultThread();
        Conversation conversation = GsonFactory.gson.fromJson(chatMessage.getContent(), Conversation.class);
        resultThread.setThread(conversation);
        chatResponse.setResult(resultThread);
        resultThread.setThread(conversation);
        return chatResponse;
    }

    /**
     * Reformat the get thread response
     */
    private ChatResponse<ResultThreads> reformatGetThreadsResponse(ChatMessage chatMessage) {
        ChatResponse<ResultThreads> outPutThreads = new ChatResponse<>();
        ArrayList<Conversation> conversations = GsonFactory.gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Conversation>>() {
        }.getType());
        ResultThreads resultThreads = new ResultThreads();
        resultThreads.setThreads(conversations);
        long contentCount = chatMessage.getContentCount() == null ? 0 : chatMessage.getContentCount();
        resultThreads.setContentCount(contentCount);
        outPutThreads.setErrorCode(0);
        outPutThreads.setErrorMessage("");
        outPutThreads.setHasError(false);
        outPutThreads.setUniqueId(chatMessage.getUniqueId());
        outPutThreads.setResult(resultThreads);
        return outPutThreads;
    }

    private ChatResponse<ResultContact> reformatGetContactResponse(ChatMessage chatMessage) {
        ResultContact resultContact = new ResultContact();
        ChatResponse<ResultContact> outPutContact = new ChatResponse<>();
        ArrayList<Contact> contacts = GsonFactory.gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Contact>>() {
        }.getType());
        resultContact.setContacts(contacts);
        resultContact.setContentCount(chatMessage.getContentCount());
        resultContact.setContentCount(chatMessage.getContentCount());
        outPutContact.setResult(resultContact);
        outPutContact.setErrorMessage("");
        outPutContact.setUniqueId(chatMessage.getUniqueId());
        return outPutContact;
    }
}

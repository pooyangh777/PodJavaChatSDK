package chatSdk.chat;

import asyncSdk.Async;
import asyncSdk.AsyncListener;
import asyncSdk.model.AsyncMessage;
import chatSdk.chat.chatInterface.ChatInterface;
import chatSdk.dataTransferObject.GeneralRequest;
import chatSdk.dataTransferObject.chat.*;
import chatSdk.dataTransferObject.contacts.inPut.Contact;
import chatSdk.dataTransferObject.system.inPut.Error;
import chatSdk.dataTransferObject.ChatResponse;
import chatSdk.dataTransferObject.contacts.inPut.*;
import chatSdk.dataTransferObject.map.inPut.MapLocation;
import asyncSdk.model.AsyncState;
import chatSdk.dataTransferObject.file.inPut.*;
import chatSdk.dataTransferObject.message.inPut.*;
import chatSdk.dataTransferObject.contacts.outPut.*;
import chatSdk.dataTransferObject.message.outPut.*;
import chatSdk.dataTransferObject.system.inPut.Admin;
import chatSdk.dataTransferObject.system.inPut.ResultSetRole;
import chatSdk.dataTransferObject.system.outPut.*;
import chatSdk.dataTransferObject.thread.inPut.*;
import chatSdk.dataTransferObject.thread.outPut.*;
import chatSdk.dataTransferObject.user.inPut.*;
import chatSdk.dataTransferObject.user.outPut.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import chatSdk.networking.api.ContactApi;
import chatSdk.networking.retrofithelper.RetrofitHelperPlatformHost;
import chatSdk.dataTransferObject.thread.inPut.Conversation;
import lombok.Getter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static asyncSdk.model.AsyncMessageType.Message;

/**
 * Created By Khojasteh on 7/29/2019
 */
public class Chat implements AsyncListener, ChatInterface {
    private static final int TOKEN_ISSUER = 1;

    private static final Logger logger = LogManager.getLogger(Chat.class);
    private static Async async;
    @Getter
    private static Chat instance;
    private final ChatListener listener;
    private static Gson gson;
    private long userId;
    private ContactApi contactApi;
    private long lastSentMessageTime;
    private ChatState state;
    private int signalIntervalTime;
    private int expireAmount;
    public ChatConfig config;

    private Chat(ChatConfig chatConfig, ChatListener listener) {
        this.config = chatConfig;
        this.listener = listener;
    }

    /**
     * Initialize the Chat
     **/

    public synchronized static Chat init(ChatConfig chatConfig, ChatListener listener) {
        if (instance == null) {
            async = new Async(chatConfig.getAsyncConfig());
            instance = new Chat(chatConfig, listener);
            instance.contactApi = RetrofitHelperPlatformHost.getInstance(chatConfig.getPlatformHost()).create(ContactApi.class);
            gson = new Gson();
        }
        return instance;
    }

    private static synchronized String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    /**
     * First we check the message type then we set the
     * callback for it.
     * Here its showed the raw log.
     */

    @Override
    public void onReceivedMessage(AsyncMessage asyncMessage) {
        int messageType = 0;
//        ChatMessage chatMessage = gson.fromJson(textMessage, ChatMessage.class);
        ChatMessage chatMessage = gson.fromJson(asyncMessage.getContent(), ChatMessage.class);
        String messageUniqueId = chatMessage != null ? chatMessage.getUniqueId() : null;
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
                handleResponseMessage(chatMessage, messageUniqueId);
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
            case ChatMessageType.UN_MUTE_THREAD:
            case ChatMessageType.MUTE_THREAD:
            case ChatMessageType.UNPIN_THREAD:
            case ChatMessageType.PIN_THREAD:
            case ChatMessageType.USER_INFO:
            case ChatMessageType.DELETE_MESSAGE:
            case ChatMessageType.EDIT_MESSAGE:
            case ChatMessageType.UPDATE_THREAD_INFO:
            case ChatMessageType.DELIVERED_MESSAGE_LIST:
            case ChatMessageType.SEEN_MESSAGE_LIST:
            case ChatMessageType.BLOCK:
            case ChatMessageType.UNBLOCK:
            case ChatMessageType.GET_BLOCKED:
            case ChatMessageType.ADD_PARTICIPANT:
            case ChatMessageType.GET_CONTACTS:
            case ChatMessageType.CREATE_THREAD:
            case ChatMessageType.GET_HISTORY:
                handleResponseMessage(chatMessage, messageUniqueId);
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


    @Override
    public void onError(Exception exception) {

    }

    @Override
    public void onStateChanged(AsyncState state, Async async) {
        switch (state) {
            case AsyncReady:
                this.state = ChatState.ChatReady;
                pingWithDelay();
                break;
            case Connecting:
                this.state = ChatState.Connecting;
                break;
            case Connected:
                this.state = ChatState.Connected;
                break;
            case Closed:
                this.state = ChatState.Closed;
                TokenExecutor.stopThread();
                break;
        }
        listener.onChatState(this.state);
    }

    //    @Override
//    public void onError(Exception asyncSdk.exception) {
//
//    }

    public void connect() throws Exception {
        try {
            async.setListener(this);
            setUserId(config.getChatId());
            gson = new Gson();
            async.connect();
        } catch (Throwable throwable) {
            showErrorLog(throwable.getMessage());
            listener.OnLogEvent(throwable.getMessage());
        }
    }

    private void pingWithDelay() {
        long lastSentMessageTimeout = 9 * 1000;
        lastSentMessageTime = new Date().getTime();

        PingExecutor.getInstance().
                scheduleAtFixedRate(() -> checkForPing(lastSentMessageTimeout),
                        0, 20000,
                        TimeUnit.MILLISECONDS);
    }

    private void checkForPing(long lastSentMessageTimeout) {
        long currentTime = new Date().getTime();
        if (currentTime - lastSentMessageTime > lastSentMessageTimeout) {
            ping();
        }
    }

    private void showInfoLog(String tag, String json) {
        if (config.isLoggable()) logger.log(Level.INFO, "{} \n \n {} ", tag, json);

        if (!Util.isNullOrEmpty(json)) {
            listener.OnLogEvent(json);
        }
    }

    private void showInfoLog(String json) {
        if (config.isLoggable()) logger.log(Level.INFO, "\n \n {}", json);
    }

    private void showErrorLog(String tag, String json) {
        if (config.isLoggable()) logger.log(Level.ERROR, " {} \n \n {} ", tag, json);

        if (!Util.isNullOrEmpty(json)) {
            listener.OnLogEvent(json);
        }
    }

    private void showErrorLog(String e) {
        if (config.isLoggable()) logger.log(Level.ERROR, "\n \n {} ", e);

    }

    private void showErrorLog(Throwable throwable) {
        if (config.isLoggable()) logger.log(Level.ERROR, "\n \n {} ", throwable.getMessage());
    }

    private void handleError(ChatMessage chatMessage) {

        Error error = gson.fromJson(chatMessage.getContent(), Error.class);

        String errorMessage = error.getMessage();
        long errorCode = error.getCode();

        getErrorOutPut(errorMessage, errorCode, chatMessage.getUniqueId());
    }

    private void handleLastSeenUpdated(ChatMessage chatMessage) {
        showInfoLog("LAST_SEEN_UPDATED", "");
        showInfoLog(chatMessage.getContent(), "");
        listener.onLastSeenUpdated(chatMessage.getContent());
    }

    private void handleThreadInfoUpdated(ChatMessage chatMessage) {
        ResultThread resultThread = new ResultThread();
        Conversation conversation = gson.fromJson(chatMessage.getContent(), Conversation.class);
        resultThread.setThread(conversation);
        ChatResponse<ResultThread> chatResponse = new ChatResponse<>();
        chatResponse.setResult(resultThread);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        listener.onThreadInfoUpdated(chatMessage.getContent(), chatResponse);
        showInfoLog("THREAD_INFO_UPDATED", chatMessage.getContent());
    }

    private void handleRemoveFromThread(ChatMessage chatMessage) {
        ChatResponse<ResultThread> chatResponse = new ChatResponse<>();
        ResultThread resultThread = new ResultThread();
        Conversation conversation = new Conversation();
        conversation.setId(chatMessage.getSubjectId());
        resultThread.setThread(conversation);
        String content = gson.toJson(chatResponse);
        showInfoLog("RECEIVED_REMOVED_FROM_THREAD", content);
        listener.OnRemovedFromThread(content, chatResponse);
    }

    /**
     * After the set Token, we send ping for checking client Authenticated or not
     * the (boolean)checkToken is for that reason
     */
    private void handleOnPing() {
        showInfoLog("RECEIVED_CHAT_PING", "");
    }

    /**
     * When the new message arrived we send the delivery message to the sender user.
     */
    private void handleNewMessage(ChatMessage chatMessage) {

        try {
            MessageVO messageVO = gson.fromJson(chatMessage.getContent(), MessageVO.class);

            ChatResponse<ResultNewMessage> chatResponse = new ChatResponse<>();
            chatResponse.setUniqueId(chatMessage.getUniqueId());
            chatResponse.setHasError(false);
            chatResponse.setErrorCode(0);
            chatResponse.setErrorMessage("");

            ResultNewMessage resultNewMessage = new ResultNewMessage();
            resultNewMessage.setMessageVO(messageVO);
            resultNewMessage.setThreadId(chatMessage.getSubjectId());

            chatResponse.setResult(resultNewMessage);

            String json = gson.toJson(chatResponse);

            long ownerId = 0;

            if (messageVO != null) {
                ownerId = messageVO.getParticipant().getId();
            }
            showInfoLog("RECEIVED_NEW_MESSAGE", json);

            if (ownerId != getUserId() && messageVO != null) {
                ChatMessage message = getChatMessage(messageVO);

                String asyncContent = gson.toJson(message);
                async.sendMessage(asyncContent, Message, null);

                showInfoLog("SEND_DELIVERY_MESSAGE");

                listener.OnLogEvent(asyncContent);
            }

            listener.onNewMessage(json, chatResponse);

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }

    }

    //TODO Problem in message id in forwardMessage
    private void handleSent(ChatMessage chatMessage) {
        ChatResponse<ResultMessage> chatResponse = new ChatResponse<>();
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setConversationId(chatMessage.getSubjectId());
        resultMessage.setMessageId(Long.parseLong(chatMessage.getContent()));
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        chatResponse.setResult(resultMessage);
        String json = gson.toJson(chatResponse);
        listener.onSent(json, chatResponse);
        showInfoLog("RECEIVED_SENT_MESSAGE", json);
    }

    private void handleSeen(ChatMessage chatMessage) {
        ChatResponse<ResultMessage> chatResponse = new ChatResponse<>();
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        chatResponse.setResult(gson.fromJson(chatMessage.getContent(), ResultMessage.class));
        String json = gson.toJson(chatResponse);
        listener.onSeen(json, chatResponse);
        showInfoLog("RECEIVED_SEEN_MESSAGE", json);
    }

    private void handleDelivery(ChatMessage chatMessage) {
        ChatResponse<ResultMessage> chatResponse = new ChatResponse<>();
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        chatResponse.setResult(gson.fromJson(chatMessage.getContent(), ResultMessage.class));
        String json = gson.toJson(chatResponse);
        listener.onDeliver(json, chatResponse);
        showInfoLog("RECEIVED_DELIVERED_MESSAGE", json);
    }

    private void handleForwardMessage(ChatMessage chatMessage) {
        MessageVO messageVO = gson.fromJson(chatMessage.getContent(), MessageVO.class);

        ChatResponse<ResultNewMessage> chatResponse = new ChatResponse<>();
        ResultNewMessage resultMessage = new ResultNewMessage();

        resultMessage.setThreadId(chatMessage.getSubjectId());
        resultMessage.setMessageVO(messageVO);

        chatResponse.setResult(resultMessage);

        String json = gson.toJson(chatResponse);

        long ownerId = 0;
        if (messageVO != null) {
            ownerId = messageVO.getParticipant().getId();
        }
        showInfoLog("RECEIVED_FORWARD_MESSAGE", json);
        listener.onNewMessage(json, chatResponse);

        if (ownerId != getUserId() && messageVO != null) {
            ChatMessage message = getChatMessage(messageVO);
            String asyncContent = gson.toJson(message);
            showInfoLog("SEND_DELIVERY_MESSAGE", asyncContent);
            async.sendMessage(asyncContent, Message, null);
        }
    }

    private void handleResponseMessage(ChatMessage chatMessage, String messageUniqueId) {

        try {
            switch (chatMessage.getType()) {
                case ChatMessageType.GET_HISTORY:
                    handleOutPutGetHistory(chatMessage);
                    break;
                case ChatMessageType.GET_CONTACTS:
                    handleGetContact(chatMessage, messageUniqueId);
                    break;
                case ChatMessageType.UPDATE_THREAD_INFO:
                    handleUpdateThreadInfo(chatMessage);
                    break;
                case ChatMessageType.CREATE_THREAD:
                    handleCreateThread(chatMessage);
                    break;
                case ChatMessageType.MUTE_THREAD:
                    handleMuteThread(chatMessage);
                    break;
                case ChatMessageType.UN_MUTE_THREAD:
                    handleUnMuteThread(chatMessage);
                    break;
                case ChatMessageType.PIN_THREAD:
                    handlePinThread(chatMessage);
                    break;
                case ChatMessageType.UNPIN_THREAD:
                    handleUnpinThread(chatMessage);
                    break;
                case ChatMessageType.EDIT_MESSAGE:
                    handleEditMessage(chatMessage);
                    break;
                case ChatMessageType.USER_INFO:
                    handleOnUserInfo(chatMessage);
                    break;
                case ChatMessageType.THREAD_PARTICIPANTS:
                    ChatResponse<ResultParticipant> resultParticipantChatResponse = reformatThreadParticipants(chatMessage);
                    String jsonParticipant = gson.toJson(resultParticipantChatResponse);
                    listener.onGetThreadParticipant(jsonParticipant, resultParticipantChatResponse);
                    showInfoLog("RECEIVE_PARTICIPANT", jsonParticipant);
                    break;
                case ChatMessageType.ADD_PARTICIPANT:
                    handleAddParticipant(chatMessage);
                    break;
                case ChatMessageType.LEAVE_THREAD:
                    handleOutPutLeaveThread(chatMessage);
                    break;
                case ChatMessageType.RENAME:
                    break;
                case ChatMessageType.DELETE_MESSAGE:
                    handleOutPutDeleteMsg(chatMessage);
                    break;
                case ChatMessageType.BLOCK:
                    handleOutPutBlock(chatMessage, messageUniqueId);
                    break;
                case ChatMessageType.UNBLOCK:
                    handleUnBlock(chatMessage, messageUniqueId);
                    break;
                case ChatMessageType.GET_BLOCKED:
                    handleOutPutGetBlockList(chatMessage);
                    break;
                case ChatMessageType.DELIVERED_MESSAGE_LIST:
                    handleOutPutDeliveredMessageList(chatMessage);
                    break;
                case ChatMessageType.SEEN_MESSAGE_LIST:
                    handleOutPutSeenMessageList(chatMessage);
                    break;
            }

        } catch (Throwable e) {
            showErrorLog(e.getCause().getMessage());
        }
    }

    private void handleUnpinThread(ChatMessage chatMessage) {
        ChatResponse<ResultPin> resultUnPinChatResponse = new ChatResponse<>();
        String unpinThreadJson = reformatPinThread(chatMessage, resultUnPinChatResponse);
        listener.onUnPin(unpinThreadJson, resultUnPinChatResponse);
        showInfoLog("RECEIVE_UN_PIN_THREAD", unpinThreadJson);
    }

    private void handlePinThread(ChatMessage chatMessage) {
        ChatResponse<ResultPin> resultPinChatResponse = new ChatResponse<>();
        String pingThreadJson = reformatPinThread(chatMessage, resultPinChatResponse);
        listener.onPin(pingThreadJson, resultPinChatResponse);
        showInfoLog("RECEIVE_PIN_THREAD", pingThreadJson);
    }

    private void handleUnMuteThread(ChatMessage chatMessage) {
        ChatResponse<ResultMute> chatResponseTemp = new ChatResponse<>();
        String unmuteThreadJson = reformatMuteThread(chatMessage, chatResponseTemp);
        listener.onMuteThread(unmuteThreadJson, chatResponseTemp);
        showInfoLog("RECEIVE_UN_MUTE_THREAD", unmuteThreadJson);
    }

    private void handleMuteThread(ChatMessage chatMessage) {
        ChatResponse<ResultMute> chatResponse = new ChatResponse<>();
        String muteThreadJson = reformatMuteThread(chatMessage, chatResponse);
        listener.onUnmuteThread(muteThreadJson, chatResponse);
        showInfoLog("RECEIVE_MUTE_THREAD", muteThreadJson);
    }

    private void handleEditMessage(ChatMessage chatMessage) {
        ChatResponse<ResultNewMessage> chatResponse = new ChatResponse<>();
        ResultNewMessage newMessage = new ResultNewMessage();
        MessageVO messageVO = gson.fromJson(chatMessage.getContent(), MessageVO.class);
        newMessage.setMessageVO(messageVO);
        newMessage.setThreadId(chatMessage.getSubjectId());
        chatResponse.setResult(newMessage);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        String content = gson.toJson(chatResponse);
        showInfoLog("RECEIVE_EDIT_MESSAGE", content);
        listener.onEditedMessage(content, chatResponse);
    }

    private void handleOutPutSeenMessageList(ChatMessage chatMessage) {
        try {
            ChatResponse<ResultParticipant> chatResponse = new ChatResponse<>();
            chatResponse.setUniqueId(chatMessage.getUniqueId());
            ResultParticipant resultParticipant = new ResultParticipant();
            List<Participant> participants = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Participant>>() {
            }.getType());
            resultParticipant.setParticipants(participants);
            resultParticipant.setContentCount(chatMessage.getContentCount());
            chatResponse.setResult(resultParticipant);
            String content = gson.toJson(chatResponse);
            listener.OnSeenMessageList(content, chatResponse);
            showInfoLog("RECEIVE_SEEN_MESSAGE_LIST", content);
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
    }


    private void handleOutPutDeliveredMessageList(ChatMessage chatMessage) {
        try {
            ChatResponse<ResultParticipant> chatResponse = new ChatResponse<>();
            chatResponse.setUniqueId(chatMessage.getUniqueId());
            ResultParticipant resultParticipant = new ResultParticipant();
            List<Participant> participants = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Participant>>() {
            }.getType());
            resultParticipant.setParticipants(participants);
            resultParticipant.setContentCount(chatMessage.getContentCount());
            chatResponse.setResult(resultParticipant);
            String content = gson.toJson(chatResponse);
            listener.OnDeliveredMessageList(content, chatResponse);
            showInfoLog("RECEIVE_DELIVERED_MESSAGE_LIST", content);
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
    }

    private void handleGetContact(ChatMessage chatMessage, String messageUniqueId) {
        ChatResponse<ResultContact> chatResponse = reformatGetContactResponse(chatMessage);
        String contactJson = gson.toJson(chatResponse);
        listener.onGetContacts(contactJson, chatResponse);
        showInfoLog("RECEIVE_GET_CONTACT", contactJson);
    }

    private void handleCreateThread(ChatMessage chatMessage) {
        ChatResponse<ResultThread> chatResponse = reformatCreateThread(chatMessage);
        String inviteJson = gson.toJson(chatResponse);
        listener.onCreateThread(inviteJson, chatResponse);
        showInfoLog("RECEIVE_CREATE_THREAD", inviteJson);
    }

    private void handleGetThreads(ChatMessage chatMessage) {
        ChatResponse<ResultThreads> chatResponse = reformatGetThreadsResponse(chatMessage);
        String threadJson = gson.toJson(chatResponse);
        listener.onGetThread(threadJson, chatResponse);
        showInfoLog("RECEIVE_GET_THREAD", threadJson);
    }

    private void handleUpdateThreadInfo(ChatMessage chatMessage) {
        ChatResponse<ResultThread> chatResponse = new ChatResponse<>();
        Conversation conversation = gson.fromJson(chatMessage.getContent(), Conversation.class);
        ResultThread resultThread = new ResultThread();
        resultThread.setThread(conversation);
        chatResponse.setErrorCode(0);
        chatResponse.setErrorMessage("");
        chatResponse.setHasError(false);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        chatResponse.setResult(resultThread);
        String threadJson = gson.toJson(chatResponse);
        listener.onUpdateThreadInfo(threadJson, chatResponse);
        showInfoLog("RECEIVE_UPDATE_THREAD_INFO", threadJson);
    }


    private void handleOutPutLeaveThread(ChatMessage chatMessage) {
        ChatResponse<ResultLeaveThread> chatResponse = new ChatResponse<>();
        ResultLeaveThread leaveThread = gson.fromJson(chatMessage.getContent(), ResultLeaveThread.class);
        long threadId = chatMessage.getSubjectId();
        leaveThread.setThreadId(threadId);
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setErrorMessage("");
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        chatResponse.setResult(leaveThread);
        String jsonThread = gson.toJson(chatResponse);
        listener.onThreadLeaveParticipant(jsonThread, chatResponse);
        showInfoLog("RECEIVE_LEAVE_THREAD", jsonThread);
    }

    private void handleAddParticipant(ChatMessage chatMessage) {
        Conversation conversation = gson.fromJson(chatMessage.getContent(), Conversation.class);
        ChatResponse<ResultAddParticipant> chatResponse = new ChatResponse<>();
        ResultAddParticipant resultAddParticipant = new ResultAddParticipant();
        resultAddParticipant.setThread(conversation);
        chatResponse.setErrorCode(0);
        chatResponse.setErrorMessage("");
        chatResponse.setHasError(false);
        chatResponse.setResult(resultAddParticipant);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        String jsonAddParticipant = gson.toJson(chatResponse);
        listener.onThreadAddParticipant(jsonAddParticipant, chatResponse);
        showInfoLog("RECEIVE_ADD_PARTICIPANT", jsonAddParticipant);
    }

    private void handleOutPutDeleteMsg(ChatMessage chatMessage) {
        ChatResponse<ResultDeleteMessage> chatResponse = new ChatResponse<>();
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        MessageVO messageVO = gson.fromJson(chatMessage.getContent(), MessageVO.class);
//        long messageId = Long.parseLong(chatMessage.getContent());
        ResultDeleteMessage resultDeleteMessage = new ResultDeleteMessage();
        DeleteMessageContent deleteMessage = new DeleteMessageContent();
        deleteMessage.setId(messageVO.getId());
        resultDeleteMessage.setDeletedMessage(deleteMessage);
        chatResponse.setResult(resultDeleteMessage);
        String jsonDeleteMsg = gson.toJson(chatResponse);
        listener.onDeleteMessage(jsonDeleteMsg, chatResponse);
        showInfoLog("RECEIVE_DELETE_MESSAGE", jsonDeleteMsg);
    }

    private void handleOutPutBlock(ChatMessage chatMessage, String messageUniqueId) {
        Contact contact = gson.fromJson(chatMessage.getContent(), Contact.class);
        ChatResponse<ResultBlock> chatResponse = new ChatResponse<>();
        ResultBlock resultBlock = new ResultBlock();
        resultBlock.setContact(contact);
        chatResponse.setResult(resultBlock);
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        String jsonBlock = gson.toJson(chatResponse);
        listener.onBlock(jsonBlock, chatResponse);
        showInfoLog("RECEIVE_BLOCK", jsonBlock);
    }


    private void handleClearHistory(ChatMessage chatMessage) {
        ChatResponse<ResultClearHistory> chatResponseClrHistory = new ChatResponse<>();
        ResultClearHistory resultClearHistory = new ResultClearHistory();
        long clrHistoryThreadId = gson.fromJson(chatMessage.getContent(), Long.class);
        resultClearHistory.setThreadId(clrHistoryThreadId);
        chatResponseClrHistory.setResult(resultClearHistory);
        chatResponseClrHistory.setUniqueId(chatMessage.getUniqueId());
        String jsonClrHistory = gson.toJson(chatResponseClrHistory);
        listener.OnClearHistory(jsonClrHistory, chatResponseClrHistory);
        showInfoLog("RECEIVE_CLEAR_HISTORY", jsonClrHistory);
    }

    private void handleInteractiveMessage(ChatMessage chatMessage) {
        ChatResponse<ResultInteractMessage> responseInteractMessage = new ChatResponse<>();
        ResultInteractMessage resultInteractMessage = gson.fromJson(chatMessage.getContent(), ResultInteractMessage.class);
        responseInteractMessage.setResult(resultInteractMessage);
        String jsonResponse = gson.toJson(responseInteractMessage);
        listener.OnInteractMessage(jsonResponse, responseInteractMessage);
        showInfoLog("RECEIVE_INTERACT_MESSAGE", jsonResponse);
    }

    private void handleSetRole(ChatMessage chatMessage) {
        ChatResponse<ResultSetRole> chatResponse = new ChatResponse<>();
        ResultSetRole resultSetRole = new ResultSetRole();
        ArrayList<Admin> admins = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Admin>>() {
        }.getType());
        resultSetRole.setAdmins(admins);
        chatResponse.setResult(resultSetRole);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        String responseJson = gson.toJson(chatResponse);
        listener.OnSetRole(responseJson, chatResponse);
        showInfoLog("RECEIVE_SET_RULE", responseJson);
    }

    private void handleRemoveRole(ChatMessage chatMessage) {
        ChatResponse<ResultSetRole> chatResponse = new ChatResponse<>();
        ResultSetRole resultSetRole = new ResultSetRole();
        ArrayList<Admin> admins = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Admin>>() {
        }.getType());
        resultSetRole.setAdmins(admins);
        chatResponse.setResult(resultSetRole);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        String responseJson = gson.toJson(chatResponse);
        listener.OnRemoveRole(responseJson, chatResponse);
        showInfoLog("RECEIVE_REMOVE_RULE", responseJson);
    }

    private void handleUnBlock(ChatMessage chatMessage, String messageUniqueId) {

        Contact contact = gson.fromJson(chatMessage.getContent(), Contact.class);
        ChatResponse<ResultBlock> chatResponse = new ChatResponse<>();
        ResultBlock resultBlock = new ResultBlock();
        resultBlock.setContact(contact);
        chatResponse.setResult(resultBlock);
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        String jsonUnBlock = gson.toJson(chatResponse);
        listener.onUnBlock(jsonUnBlock, chatResponse);
        showInfoLog("RECEIVE_UN_BLOCK", jsonUnBlock);
    }

    private void handleOutPutGetBlockList(ChatMessage chatMessage) {
        ChatResponse<ResultBlockList> chatResponse = new ChatResponse<>();
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        ResultBlockList resultBlockList = new ResultBlockList();
        List<Contact> contacts = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Contact>>() {
        }.getType());
        resultBlockList.setContentCount(chatMessage.getContentCount());
        resultBlockList.setContentCount(chatMessage.getContentCount());
        resultBlockList.setContacts(contacts);
        chatResponse.setResult(resultBlockList);
        String jsonGetBlock = gson.toJson(chatResponse);
        listener.onGetBlockList(jsonGetBlock, chatResponse);
        showInfoLog("RECEIVE_GET_BLOCK_LIST", jsonGetBlock);
    }

    private void handleOutPutRemoveParticipant(ChatMessage chatMessage) {
        ChatResponse<ResultParticipant> chatResponse = reformatThreadParticipants(chatMessage);
        String jsonRmParticipant = gson.toJson(chatResponse);
        listener.onThreadRemoveParticipant(jsonRmParticipant, chatResponse);
        showInfoLog("RECEIVE_REMOVE_PARTICIPANT", jsonRmParticipant);
    }

    private void handleOnUserInfo(ChatMessage chatMessage) {
        ChatResponse<ResultUserInfo> chatResponse = new ChatResponse<>();
        UserInfo userInfo = gson.fromJson(chatMessage.getContent(), UserInfo.class);
        String userInfoJson = reformatUserInfo(chatMessage, chatResponse, userInfo);
        listener.onUserInfo(userInfoJson, chatResponse);
        showInfoLog("RECEIVE_USER_INFO", userInfoJson);
    }

    private String reformatUserInfo(ChatMessage chatMessage, ChatResponse<ResultUserInfo> outPutUserInfo, UserInfo userInfo) {
        ResultUserInfo result = new ResultUserInfo();
        setUserId(userInfo.getId());
        result.setUser(userInfo);
        outPutUserInfo.setErrorCode(0);
        outPutUserInfo.setErrorMessage("");
        outPutUserInfo.setHasError(false);
        outPutUserInfo.setResult(result);
        outPutUserInfo.setUniqueId(chatMessage.getUniqueId());
        return gson.toJson(outPutUserInfo);
    }

    private void handleOutPutGetHistory(ChatMessage chatMessage) {
        List<MessageVO> messageVOS = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<MessageVO>>() {
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
        String json = gson.toJson(chatResponse);
        listener.onGetHistory(json, chatResponse);
        showInfoLog("RECEIVE_GET_HISTORY", json);
    }

    private String getErrorOutPut(String errorMessage, long errorCode, String uniqueId) {
        ErrorOutPut error = new ErrorOutPut(true, errorMessage, errorCode, uniqueId);
        String jsonError = gson.toJson(error);
        listener.onError(jsonError, error);
        listener.OnLogEvent(jsonError);
        showErrorLog("ErrorMessage :" + errorMessage + " *Code* " + errorCode + " *uniqueId* " + uniqueId);
        return jsonError;
    }


    private int getExpireAmount() {
        if (Util.isNullOrEmpty(expireAmount)) {
            expireAmount = 2 * 24 * 60 * 60;
        }
        return expireAmount;
    }

    /**
     * @param expireSecond participants and contacts have an expiry date in cache and after expireSecond
     *                     they are going to delete from the cache/
     */

    public void setExpireAmount(int expireSecond) {
        this.expireAmount = expireSecond;
    }

    /**
     * The replacement method is getMessageDeliveredList.
     */

    //TODO ChatContract cache
    //TODO Check if participant is null!!!
    private ChatResponse<ResultParticipant> reformatThreadParticipants(ChatMessage chatMessage) {

        ArrayList<Participant> participants = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Participant>>() {
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

    private ChatMessage getChatMessage(String contentThreadChat, String uniqueId, String typeCode) {
        ChatMessage chatMessage = new ChatMessage();

        chatMessage.setContent(contentThreadChat);
        chatMessage.setType(ChatMessageType.CREATE_THREAD);
        chatMessage.setToken(config.getToken());
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));

        if (typeCode != null && !typeCode.isEmpty()) {
            chatMessage.setTypeCode(typeCode);
        } else {
            chatMessage.setTypeCode(config.getTypeCode());
        }
        return chatMessage;
    }

    private ChatMessage getChatMessage(MessageVO jsonMessage) {
        ChatMessage message = new ChatMessage();

        message.setType(ChatMessageType.DELIVERY);
        message.setContent(String.valueOf(jsonMessage.getId()));
        message.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
        message.setToken(config.getToken());
        message.setUniqueId(generateUniqueId());
        //message.setTime(1000);
        return message;
    }

   /* private String reformatUserInfo(ChatMessage chatMessage, ChatResponse<ResultUserInfo> outPutUserInfo, UserInfo userInfo) {

        ResultUserInfo result = new ResultUserInfo();

        setUserId(userInfo.getId());
        result.setUser(userInfo);
        outPutUserInfo.setErrorCode(0);
        outPutUserInfo.setErrorMessage("");
        outPutUserInfo.setHasError(false);
        outPutUserInfo.setResult(result);
        outPutUserInfo.setUniqueId(chatMessage.getUniqueId());

        return gson.toJson(outPutUserInfo);
    }*/

    private String reformatMuteThread(ChatMessage chatMessage, ChatResponse<ResultMute> outPut) {
        ResultMute resultMute = new ResultMute();
        resultMute.setThreadId(Long.parseLong(chatMessage.getContent()));
        outPut.setResult(resultMute);
        outPut.setHasError(false);
        outPut.setErrorMessage("");
        outPut.setUniqueId(chatMessage.getUniqueId());
        gson.toJson(outPut);
        return gson.toJson(outPut);
    }


    private String reformatPinThread(ChatMessage chatMessage, ChatResponse<ResultPin> outPut) {
        ResultPin requestPinThread = new ResultPin();
        requestPinThread.setThreadId(Long.parseLong(chatMessage.getContent()));
        outPut.setResult(requestPinThread);
        outPut.setHasError(false);
        outPut.setErrorMessage("");
        outPut.setUniqueId(chatMessage.getUniqueId());
        gson.toJson(outPut);
        return gson.toJson(outPut);
    }

    private ChatResponse<ResultThread> reformatCreateThread(ChatMessage chatMessage) {

        ChatResponse<ResultThread> chatResponse = new ChatResponse<>();
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        ResultThread resultThread = new ResultThread();

        Conversation conversation = gson.fromJson(chatMessage.getContent(), Conversation.class);
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
        ArrayList<Conversation> conversations = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Conversation>>() {
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

    private String reformatError(boolean hasError, ChatMessage chatMessage, OutPutHistory outPut) {
        Error error = gson.fromJson(chatMessage.getContent(), Error.class);
        showErrorLog("RECEIVED_ERROR", chatMessage.getContent());
        showErrorLog("ErrorMessage", error.getMessage());
        showErrorLog("ErrorCode", String.valueOf(error.getCode()));
        outPut.setHasError(hasError);
        outPut.setErrorMessage(error.getMessage());
        outPut.setErrorCode(error.getCode());

        return gson.toJson(outPut);
    }

    private ChatResponse<ResultContact> reformatGetContactResponse(ChatMessage chatMessage) {
        ResultContact resultContact = new ResultContact();

        ChatResponse<ResultContact> outPutContact = new ChatResponse<>();

        ArrayList<Contact> contacts = gson.fromJson(chatMessage.getContent(), new TypeToken<ArrayList<Contact>>() {
        }.getType());

        resultContact.setContacts(contacts);
        resultContact.setContentCount(chatMessage.getContentCount());

        resultContact.setContentCount(chatMessage.getContentCount());

        outPutContact.setResult(resultContact);
        outPutContact.setErrorMessage("");
        outPutContact.setUniqueId(chatMessage.getUniqueId());

        return outPutContact;
    }

    public String getContentType(File file) throws IOException {
        MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
        return fileTypeMap.getContentType(file.getName());
    }

    private String createFileMetadata(String fileName,
                                      String hashCode,
                                      long fileId,
                                      String mimeType,
                                      long fileSize,
                                      String filePath) {

        MetaDataFile metaDataFile = new MetaDataFile();
        FileMetaDataContent metaDataContent = new FileMetaDataContent();

        metaDataContent.setId(fileId);
        metaDataContent.setName(fileName);
        metaDataContent.setMimeType(mimeType);
        metaDataContent.setSize(fileSize);

        if (hashCode != null) {
            metaDataContent.setHashCode(hashCode);
            metaDataContent.setLink(getFile(fileId, hashCode, true));

        } else {
            metaDataContent.setLink(filePath);
        }

        metaDataFile.setFile(metaDataContent);

        return gson.toJson(metaDataFile);
    }

    private String createImageMetadata(File fileUri, String hashCode, long imageId, int actualHeight, int actualWidth, String mimeType
            , long fileSize, String path, boolean isLocation, String center) {

        String originalName = fileUri.getName();
        FileImageMetaData fileMetaData = new FileImageMetaData();


        if (originalName.contains(".")) {
            String editedName = originalName.substring(0, originalName.lastIndexOf('.'));
            fileMetaData.setName(editedName);
        }
        fileMetaData.setHashCode(hashCode);
        fileMetaData.setId(imageId);
        fileMetaData.setOriginalName(originalName);
        fileMetaData.setActualHeight(actualHeight);
        fileMetaData.setActualWidth(actualWidth);
        fileMetaData.setMimeType(mimeType);
        fileMetaData.setSize(fileSize);
        if (!Util.isNullOrEmpty(hashCode)) {
            fileMetaData.setLink(getImage(imageId, hashCode, false));
        } else {
            fileMetaData.setLink(path);
        }
        if (isLocation) {
            MetadataLocationFile locationFile = new MetadataLocationFile();
            MapLocation mapLocation = new MapLocation();

            if (center.contains(",")) {
                String latitude = center.substring(0, center.lastIndexOf(','));
                String longitude = center.substring(center.lastIndexOf(',') + 1);
                mapLocation.setLatitude(Double.parseDouble(latitude));
                mapLocation.setLongitude(Double.parseDouble(longitude));
            }

            locationFile.setLocation(mapLocation);
            locationFile.setFile(fileMetaData);
            return gson.toJson(locationFile);

        } else {
            MetaDataImageFile metaData = new MetaDataImageFile();
            metaData.setFile(fileMetaData);
            return gson.toJson(metaData);
        }
    }

    /**
     * This method generate url that you can use to get your file
     */
    public String getFile(long fileId, String hashCode, boolean downloadable) {
        return config.getFileServer() + "/nzh/file/" + "?fileId=" + fileId + "&downloadable=" + downloadable + "&hashCode=" + hashCode;
    }

    /**
     * This method generate url based on your input params that you can use to get your image
     */
    public String getImage(long imageId, String hashCode, boolean downloadable) {
        String url;
        if (downloadable) {
            url = config.getFileServer() + "/nzh/image/" + "?imageId=" + imageId + "&downloadable=" + downloadable + "&hashCode=" + hashCode;
        } else {
            url = config.getFileServer() + "/nzh/image/" + "?imageId=" + imageId + "&hashCode=" + hashCode;
        }
        return url;
    }

    private long getUserId() {
        return userId;
    }

    private void setUserId(long userId) {
        this.userId = userId;
    }


    private int getSignalIntervalTime() {
        return signalIntervalTime;
    }


    public void setSignalIntervalTime(int signalIntervalTime) {
        this.signalIntervalTime = signalIntervalTime;
    }


    public interface GetThreadHandler {
        void onGetThread();
    }


    public interface SendTextMessageHandler {
        void onSent(String uniqueId, long threadId);

        void onSentResult(String content);

    }

    public String getThreads(GetThreadRequest requestThread) {
        sendAsyncMessage(requestThread);
        return requestThread.getUniqueId();
    }

    public String getHistory(GetHistoryRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String sendTextMessage(SendMessageRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String muteThread(GeneralRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String unMuteThread(GeneralRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String pinThread(GeneralRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String unPinThread(GeneralRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String getThreadParticipants(ThreadParticipantRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String getUserInfo(GeneralRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String editMessage(EditMessageRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String addAdmin(SetAdminRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String block(BlockRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String unBlock(UnBlockRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String deleteMessage(DeleteMessageRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String addParticipants(AddParticipantsRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String removeParticipants(RemoveParticipantsRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String closeThread(GeneralRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String leaveThread(LeaveThreadRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String pinMessage(PinMessageRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String clearHistory(ClearHistoryRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String deliveredMessageList(DeliveredMessageListRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String deliveryMessage(DeliveryMessageRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String forwardMessage(ForwardMessageRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String replyMessage(ReplyMessageRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String seenMessageList(SeenMessageListRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String seenMessage(SeenMessageRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String blockList(BlockListRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String unReadMessageCount(AllUnReadMessageCountRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String createThread(CreateThreadRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String createThreadWithMessage(CreateThreadWithMessageRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String getContacts(GetContactsRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String archiveThread(GeneralRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String createTag(CreateTagRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String unArchiveThread(GeneralRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String editTag(EditTagRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String exportMessage(ExportMessageRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String deleteTag(DeleteTagRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String isThreadNamePublic(IsThreadNamePublicRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String addTagParticipants(AddTagParticipantsRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String updateThreadInfo(UpdateThreadInfoRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String getTagParticipants(GetTagParticipantsRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String changeThreadType(ChangeThreadTypeRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String getTagList(GeneralRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String ping() {
        GeneralRequest request = new GeneralRequest
                .Builder()
                .setMessageType(ChatMessageType.PING)
                .build();
        if (state == ChatState.ChatReady) {
            sendAsyncMessage(request);
        }
        return request.getUniqueId();
    }

    private void sendAsyncMessage(BaseRequest request) {
        if (state == ChatState.ChatReady) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setToken(config.getToken());
            chatMessage.setUniqueId(request.getUniqueId());
            chatMessage.setType(request.getChatMessageType());// ping , getThread , getHistory , ...
            chatMessage.setContent(request.getChatMessageContent());
            chatMessage.setSubjectId(request.getSubjectId());
            chatMessage.setTypeCode(config.getTypeCode());   // we should send this everywhere but that is not send
            chatMessage.setMessageType(1); // video , text , picture , ...    //we must do something about this for not send in everywhere
            chatMessage.setRepliedTo(request.getRepliedTo());
            async.sendMessage(gson.toJson(chatMessage), Message, null);
        }
    }
}
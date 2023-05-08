package chatSdk.chat;

import asyncSdk.Async;
import asyncSdk.AsyncListener;
import asyncSdk.model.AsyncMessage;
import chatSdk.dataTransferObject.GeneralRequest;
import chatSdk.dataTransferObject.chat.*;
import chatSdk.dataTransferObject.contacts.inPut.Contact;
import chatSdk.dataTransferObject.system.inPut.Error;
import chatSdk.dataTransferObject.ChatResponse;
import chatSdk.dataTransferObject.contacts.inPut.*;
import chatSdk.dataTransferObject.map.inPut.MapLocation;
import asyncSdk.model.AsyncMessageType;
import asyncSdk.model.AsyncState;
import chatSdk.dataTransferObject.file.inPut.*;
import chatSdk.dataTransferObject.message.inPut.*;
import chatSdk.dataTransferObject.message.outPut.RequestEditMessage;
import chatSdk.dataTransferObject.contacts.outPut.*;
import chatSdk.dataTransferObject.file.outPut.RequestFileMessage;
import chatSdk.dataTransferObject.file.outPut.RequestReplyFileMessage;
import chatSdk.dataTransferObject.file.outPut.RequestUploadFile;
import chatSdk.dataTransferObject.file.outPut.RequestUploadImage;
import chatSdk.dataTransferObject.message.outPut.*;
import chatSdk.dataTransferObject.system.inPut.Admin;
import chatSdk.dataTransferObject.system.inPut.ResultSetRole;
import chatSdk.dataTransferObject.system.outPut.*;
import chatSdk.dataTransferObject.thread.inPut.*;
import chatSdk.dataTransferObject.thread.outPut.*;
import chatSdk.dataTransferObject.user.inPut.*;
import chatSdk.dataTransferObject.user.outPut.*;
import chatSdk.mainmodel.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import chatSdk.localModel.LFileUpload;
import chatSdk.localModel.SetRuleVO;
import chatSdk.networking.api.ContactApi;
import chatSdk.networking.api.FileApi;
import chatSdk.networking.retrofithelper.ApiListener;
import chatSdk.networking.retrofithelper.RetrofitHelperFileServer;
import chatSdk.networking.retrofithelper.RetrofitHelperPlatformHost;
import chatSdk.networking.retrofithelper.RetrofitUtil;
import chatSdk.dataTransferObject.thread.inPut.Conversation;
import lombok.Getter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import retrofit2.Call;
import retrofit2.Response;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static asyncSdk.model.AsyncMessageType.Message;

/**
 * Created By Khojasteh on 7/29/2019
 */
public class Chat implements AsyncListener {
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
            case ChatMessageType.INVITATION:
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

    /**
     * Send text message to the thread
     * All messages first send to Message Queue(Cache) and then send to chat server
     *
     * @param textMessage        String that we want to send to the thread
     * @param threadId           ID of the destination thread
     * @param jsonSystemMetadata It should be Json,if you don't have metaData you can set it to "null"
     */
    public String sendTextMessage(String textMessage, long threadId, Integer messageType, String jsonSystemMetadata, String typeCode) {

        String asyncContentWaitQueue;
        String uniqueId = generateUniqueId();

        try {

            ChatMessage chatMessageQueue = new ChatMessage();
            chatMessageQueue.setContent(textMessage);
            chatMessageQueue.setType(ChatMessageType.MESSAGE);
            chatMessageQueue.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            chatMessageQueue.setToken(config.getToken());

            if (jsonSystemMetadata != null) {
                chatMessageQueue.setSystemMetadata(jsonSystemMetadata);
            }

            chatMessageQueue.setUniqueId(uniqueId);
            chatMessageQueue.setSubjectId(threadId);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessageQueue);

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", config.getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            if (!Util.isNullOrEmpty(messageType)) {
                jsonObject.addProperty("messageType", messageType);
            } else {
                jsonObject.remove("messageType");
            }

            asyncContentWaitQueue = jsonObject.toString();

            if (state == ChatState.ChatReady) {
                sendAsyncMessage(asyncContentWaitQueue, "SEND_TEXT_MESSAGE");

            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);

                showErrorLog(jsonError);
            }
        } catch (Throwable throwable) {
            showErrorLog(throwable.getMessage());
        }
        return uniqueId;
    }

    /**
     * Its sent message but it gets Object as an attribute
     *
     * @param requestMessage this object has :
     *                       String textMessage {text of the message}
     *                       int messageType {type of the message}
     *                       String jsonMetaData {metadata of the message}
     *                       long threadId {The id of a thread that It's wanted to send  }
     */

    public String sendTextMessage(RequestMessage requestMessage) {
        String textMessage = requestMessage.getTextMessage();
        long threadId = requestMessage.getThreadId();
        int messageType = requestMessage.getMessageType();
        String jsonMetaData = requestMessage.getJsonMetaData();
        String typeCode = config.getTypeCode();

        return sendTextMessage(textMessage, threadId, messageType, jsonMetaData, typeCode);
    }

    private void sendAsyncMessage(String asyncContent, String logMessage) {
        if (state == ChatState.ChatReady) {
            showInfoLog(logMessage, asyncContent);
            try {
                async.sendMessage(asyncContent, AsyncMessageType.Message, null);
            } catch (Exception e) {
                showErrorLog(e.getMessage());
                return;
            }
            pingWithDelay();
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, null);
        }
    }

    /**
     * Get history of the thread
     * <p>
     * count  :  count of the messages
     * order  :  If order is empty [default = desc] and also you have two option [ asc | desc ]
     * and order must be lowered case
     * lastMessageId
     * FirstMessageId
     *
     * @param threadId ID of the thread that we want to get the history
     */
    @Deprecated
    public String getHistory(History history, long threadId, String typeCode) {
        String uniqueId;
        uniqueId = generateUniqueId();

        if (history.getCount() != 0) {
            history.setCount(history.getCount());
        } else {
            history.setCount(50);
        }

        if (history.getOffset() != 0) {
            history.setOffset(history.getOffset());
        } else {
            history.setOffset(0);
        }

        if (state == ChatState.ChatReady) {
            getHistoryMain(history, threadId, uniqueId, typeCode);
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * Gets history of the thread
     * <p>
     *
     * @Param count  :  count of the messages
     * @Param order  :  If order is empty [default = desc] and also you have two option [ asc | desc ]
     * lastMessageId
     * FirstMessageId
     * @Param long threadId   ID of the thread
     * @Param long fromTime    Start Time of the messages
     * @Param long fromTimeNanos  Start Time of the messages in Nano second
     * @Param long toTime         End time of the messages
     * @Param long toTimeNanos    End time of the messages
     * @Param @Deprecated long firstMessageId
     * @Param @Deprecated long lastMessageId
     * <p>
     * <p>
     * threadId ID of the thread that we want to get the history
     */

    public String getHistory(RequestGetHistory request) {
        String uniqueId = generateUniqueId();

        if (state == ChatState.ChatReady) {

            History history = new History.Builder()
                    .count(request.getCount())
                    .firstMessageId(request.getFirstMessageId())
                    .lastMessageId(request.getLastMessageId())
                    .offset(request.getOffset())
                    .fromTime(request.getFromTime())
                    .fromTimeNanos(request.getFromTimeNanos())
                    .toTime(request.getToTime())
                    .toTimeNanos(request.getToTimeNanos())
                    .order(request.getOrder())
                    .id(request.getId())
                    .uniqueIds(request.getUniqueIds())
                    .build();

            getHistoryMain(history, request.getThreadId(), uniqueId, config.getTypeCode());

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * It clears all messages in the thread
     *
     * @param requestClearHistory threadId  The id of the thread in which you want to clear all messages
     * @return uniqueId
     */
    public String clearHistory(RequestClearHistory requestClearHistory) {
        String uniqueId = generateUniqueId();
        long threadId = requestClearHistory.getThreadId();

        if (state == ChatState.ChatReady) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessageType.CLEAR_HISTORY);
            chatMessage.setToken(config.getToken());
            chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            chatMessage.setSubjectId(threadId);
            chatMessage.setUniqueId(uniqueId);

            chatMessage.setContent(gson.toJson(requestClearHistory));
//            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
//            jsonObject.remove("systemMetadata");
//            jsonObject.remove("metadata");
//            jsonObject.remove("repliedTo");
//            jsonObject.remove("contentCount");

            String typeCode = config.getTypeCode();

//            if (!Util.isNullOrEmpty(typeCode)) {
//                jsonObject.remove("typeCode");
//                jsonObject.addProperty("typeCode", typeCode);
//            } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
//                jsonObject.remove("typeCode");
//                jsonObject.addProperty("typeCode", config.getTypeCode());
//            } else {
//                jsonObject.remove("typeCode");
//            }

            String asyncContent = gson.toJson(chatMessage);

            sendAsyncMessage(asyncContent, "SEND_CLEAR_HISTORY");
        }
        return uniqueId;
    }

    /**
     * Get all contacts of the user
     */

    public String getContacts(RequestGetContact request) {
        Long offset = request.getOffset();
        Long count = request.getCount();
        String typeCode = config.getTypeCode();
        return getContacts(count.intValue(), offset, typeCode);
    }

    /**
     * Get all contacts of the user
     */
    public String getContacts(Integer count, Long offset, String typeCode) {
        return getContactMain(count, offset, typeCode);
    }

    /**
     * Add one contact to the contact list
     *
     * @param firstName       Notice: if just put fistName without lastName it's ok.
     * @param lastName        last name of the contact
     * @param cellphoneNumber Notice: If you just  put the cellPhoneNumber doesn't necessary to add email
     * @param email           email of the contact
     */
    public String addContact(String firstName, String lastName, String cellphoneNumber, String email, String typeCode) {

        if (Util.isNullOrEmpty(firstName)) {
            firstName = "";
        }
        if (Util.isNullOrEmpty(lastName)) {
            lastName = "";
        }
        if (Util.isNullOrEmpty(email)) {
            email = "";
        }
        if (Util.isNullOrEmpty(cellphoneNumber)) {
            cellphoneNumber = "";
        }

        String uniqueId = generateUniqueId();

        Call<Contacts> addContactService;

        if (state == ChatState.ChatReady) {

            if (!Util.isNullOrEmpty(typeCode)) {
                addContactService = contactApi.addContact(config.getToken(), TOKEN_ISSUER, firstName, lastName, email, uniqueId, cellphoneNumber, typeCode);

            } else {
                addContactService = contactApi.addContact(config.getToken(), TOKEN_ISSUER, firstName, lastName, email, uniqueId, cellphoneNumber, config.getTypeCode());
            }
            showInfoLog("ADD_CONTACT");

            RetrofitUtil.request(addContactService, new ApiListener<Contacts>() {
                @Override
                public void onSuccess(Contacts contacts) {
                    if (!contacts.getHasError()) {
                        ChatResponse<ResultAddContact> chatResponse = Util.getReformatOutPutAddContact(contacts, uniqueId);
                        String contactsJson = gson.toJson(chatResponse);
                        listener.onContactAdded(contactsJson, chatResponse);
                        showInfoLog("RECEIVED_ADD_CONTACT", contactsJson);
                    } else {
                        getErrorOutPut(contacts.getMessage(), contacts.getErrorCode(), uniqueId);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    showErrorLog(throwable.getMessage());
                }

                @Override
                public void onServerError(Response<Contacts> response) {
                    if (response.body() != null) {
                        showErrorLog(response.body().getMessage());
                    } else {
                        showErrorLog(response.raw().toString());
                    }
                }
            });

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * Add one contact to the contact list
     * <p>
     * firstName       Notice: if just put fistName without lastName it's ok.
     * lastName        last name of the contact
     * cellphoneNumber Notice: If you just  put the cellPhoneNumber doesn't necessary to add email
     * email           email of the contact
     */

    public String addContact(RequestAddContact request) {

        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        String email = request.getEmail();
        String cellphoneNumber = request.getCellphoneNumber();
        String typeCode = config.getTypeCode();

        return addContact(firstName, lastName, cellphoneNumber, email, typeCode);
    }

    /**
     * Remove contact with the user id
     *
     * @param userId id of the user that we want to remove from contact list
     */
    public String removeContact(long userId, String typeCode) {
        String uniqueId = generateUniqueId();

        Call<ContactRemove> removeContactObservable;

        if (state == ChatState.ChatReady) {

            if (!Util.isNullOrEmpty(typeCode)) {
                removeContactObservable = contactApi.removeContact(config.getToken(), TOKEN_ISSUER, userId, typeCode);
            } else {
                removeContactObservable = contactApi.removeContact(config.getToken(), TOKEN_ISSUER, userId, config.getTypeCode());
            }

            RetrofitUtil.request(removeContactObservable, new ApiListener<ContactRemove>() {
                @Override
                public void onSuccess(ContactRemove contactRemove) {
                    if (contactRemove != null) {
                        if (!contactRemove.getHasError()) {
                            ChatResponse<ResultRemoveContact> chatResponse = new ChatResponse<>();
                            chatResponse.setUniqueId(uniqueId);
                            ResultRemoveContact resultRemoveContact = new ResultRemoveContact();
                            resultRemoveContact.setResult(contactRemove.isResult());
                            chatResponse.setResult(resultRemoveContact);
                            String json = gson.toJson(chatResponse);
                            listener.onRemoveContact(json, chatResponse);
                            showInfoLog("RECEIVED_REMOVE_CONTACT", json);
                        } else {
                            getErrorOutPut(contactRemove.getErrorMessage(), contactRemove.getErrorCode(), uniqueId);
                        }
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    showErrorLog(throwable.getMessage());
                }

                @Override
                public void onServerError(Response<ContactRemove> response) {
                    if (response.body() != null) {
                        showErrorLog(response.body().getErrorMessage());
                    }
                }
            });
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * Remove contact with the user id
     * <p>
     * userId id of the user that we want to remove from contact list
     */

    public String removeContact(RequestRemoveContact request) {
        long userId = request.getUserId();
        String typeCode = config.getTypeCode();
        return removeContact(userId, typeCode);
    }

    /**
     * Update contacts
     * all params all required to update
     */
    public String updateContact(long userId, String firstName, String lastName, String cellphoneNumber, String email, String typeCode) {

        String uniqueId = generateUniqueId();

        if (Util.isNullOrEmpty(firstName)) {
            firstName = "";
        }

        if (Util.isNullOrEmpty(lastName)) {
            lastName = "";
        }

        if (Util.isNullOrEmpty(cellphoneNumber)) {
            cellphoneNumber = "";
        }

        if (Util.isNullOrEmpty(email)) {
            email = "";
        }

        if (state == ChatState.ChatReady) {
            Call<UpdateContact> updateContactObservable;

            if (!Util.isNullOrEmpty(typeCode)) {
                updateContactObservable = contactApi.updateContact(config.getToken(), TOKEN_ISSUER, userId, firstName, lastName, email, uniqueId, cellphoneNumber, typeCode);
            } else {
                updateContactObservable = contactApi.updateContact(config.getToken(), TOKEN_ISSUER, userId, firstName, lastName, email, uniqueId, cellphoneNumber, config.getTypeCode());
            }

            RetrofitUtil.request(updateContactObservable, new ApiListener<UpdateContact>() {
                @Override
                public void onSuccess(UpdateContact updateContact) {
                    if (updateContact != null) {
                        if (!updateContact.getHasError()) {
                            ChatResponse<ResultUpdateContact> chatResponse = new ChatResponse<>();
                            chatResponse.setUniqueId(uniqueId);
                            ResultUpdateContact resultUpdateContact = new ResultUpdateContact();
                            if (!Util.isNullOrEmpty(updateContact.getCount())) {
                                resultUpdateContact.setContentCount(updateContact.getCount());
                            }
                            resultUpdateContact.setContacts(updateContact.getResult());
                            chatResponse.setResult(resultUpdateContact);
                            String json = gson.toJson(chatResponse);
                            listener.onUpdateContact(json, chatResponse);
                            showInfoLog("RECEIVE_UPDATE_CONTACT", json);
                        } else {
                            String errorMsg = updateContact.getMessage();
                            int errorCodeMsg = updateContact.getErrorCode();
                            errorMsg = errorMsg != null ? errorMsg : "";
                            getErrorOutPut(errorMsg, errorCodeMsg, uniqueId);
                        }
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    showErrorLog(throwable.getMessage());
                }

                @Override
                public void onServerError(Response<UpdateContact> response) {
                    if (response.body() != null) {
                        showErrorLog(response.body().getMessage());
                    }
                }
            });

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

//TODO description

    /**
     * Update contacts
     * all of params all required
     */

    public String updateContact(RequestUpdateContact request) {
        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        String email = request.getEmail();
        String cellphoneNumber = request.getCellphoneNumber();
        long userId = request.getUserId();
        String typeCode = config.getTypeCode();

        return updateContact(userId, firstName, lastName, cellphoneNumber, email, typeCode);
    }

    /**
     * @param requestSearchContact
     * @return uniqueId
     */

    public String searchContact(RequestSearchContact requestSearchContact) {
        String uniqueId = generateUniqueId();
        String type_code;

        if (!Util.isNullOrEmpty(config.getTypeCode())) {
            type_code = config.getTypeCode();
        } else {
            type_code = config.getTypeCode();
        }

        String offset = (requestSearchContact.getOffset() == null) ? "0" : requestSearchContact.getOffset();
        String size = (requestSearchContact.getSize() == null) ? "50" : requestSearchContact.getSize();

        if (state == ChatState.ChatReady) {

            Call<SearchContactVO> searchContactCall = contactApi.searchContact(config.getToken(), TOKEN_ISSUER,
                    requestSearchContact.getId()
                    , requestSearchContact.getFirstName()
                    , requestSearchContact.getLastName()
                    , requestSearchContact.getEmail()
                    , offset
                    , size
                    , type_code
                    , requestSearchContact.getQuery()
                    , requestSearchContact.getCellphoneNumber());


            RetrofitUtil.request(searchContactCall, new ApiListener<SearchContactVO>() {
                @Override
                public void onSuccess(SearchContactVO searchContactVO) {
                    if (searchContactVO.getHasError())
                        getErrorOutPut(searchContactVO.getMessage(), ChatConstant.ERROR_CODE_UNKNOWN_EXCEPTION, uniqueId);
                    else {
                        ArrayList<Contact> contacts = new ArrayList<>(searchContactVO.getResult());
                        ResultContact resultContacts = new ResultContact();
                        resultContacts.setContacts(contacts);
                        ChatResponse<ResultContact> chatResponse = new ChatResponse<>();
                        chatResponse.setUniqueId(uniqueId);
                        chatResponse.setResult(resultContacts);
                        String content = gson.toJson(chatResponse);
                        listener.onSearchContact(content, chatResponse);
                        listener.OnLogEvent(content);
                        showInfoLog("RECEIVE_SEARCH_CONTACT");
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    showErrorLog(throwable.getMessage());
                }

                @Override
                public void onServerError(Response<SearchContactVO> response) {
                    if (response.body() != null) {
                        String message = response.body().getMessage() != null ? response.body().getMessage() : "";
                        int errorCode = response.body().getErrorCode() != null ? response.body().getErrorCode() : 0;
                        getErrorOutPut(message, errorCode, uniqueId);
                    }
                }
            });

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * It deletes message from the thread.
     *
     * @param messageId    ID of the message that you want to be removed.
     * @param deleteForAll If you want to delete message for everyone you can set it true if u don't want
     *                     you can set it false or even null.
     */
    public String deleteMessage(Long messageId, Boolean deleteForAll, String typeCode) {
        String uniqueId = generateUniqueId();
        if (state == ChatState.ChatReady) {
            deleteForAll = deleteForAll != null ? deleteForAll : false;
            BaseMessage baseMessage = new BaseMessage();
            JsonObject contentObj = new JsonObject();
            contentObj.addProperty("deleteForAll", deleteForAll);
            baseMessage.setContent(contentObj.toString());
            baseMessage.setToken(config.getToken());
            baseMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            baseMessage.setType(ChatMessageType.DELETE_MESSAGE);
            baseMessage.setUniqueId(uniqueId);
            baseMessage.setSubjectId(messageId);
            JsonObject jsonObject = (JsonObject) gson.toJsonTree(baseMessage);
            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", config.getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }
            String asyncContent = jsonObject.toString();
            sendAsyncMessage(asyncContent, "SEND_DELETE_MESSAGE");
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * forward message
     *
     * @param threadId   destination thread id
     * @param messageIds Array of message ids that we want to forward them
     */
    public List<String> forwardMessage(long threadId, ArrayList<Long> messageIds, String typeCode) {
        ArrayList<String> uniqueIds = new ArrayList<>();
        ArrayList<Callback> callbacks = new ArrayList<>();
        for (long messageId : messageIds) {
            String uniqueId = generateUniqueId();
            uniqueIds.add(uniqueId);
            Callback callback = new Callback();
            callback.setDelivery(true);
            callback.setSeen(true);
            callback.setSent(true);
            callback.setUniqueId(uniqueId);
            callbacks.add(callback);
        }

        if (state == ChatState.ChatReady) {
            ChatMessageForward chatMessageForward = new ChatMessageForward();
            chatMessageForward.setSubjectId(threadId);

            String jsonUniqueIds = Util.listToJson(uniqueIds, gson);

            chatMessageForward.setUniqueId(jsonUniqueIds);
            chatMessageForward.setContent(messageIds.toString());
            chatMessageForward.setToken(config.getToken());
            chatMessageForward.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            chatMessageForward.setType(ChatMessageType.FORWARD_MESSAGE);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessageForward);

            jsonObject.remove("contentCount");
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", config.getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, "SEND_FORWARD_MESSAGE");

        } else {
            if (Util.isNullOrEmpty(uniqueIds)) {
                for (String uniqueId : uniqueIds) {
                    getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
                }
            }
        }
        return uniqueIds;
    }

    /**
     * forward message
     * <p>
     * threadId   destination thread id
     * messageIds Array of message ids that we want to forward them
     */

    public List<String> forwardMessage(RequestForwardMessage request) {
        return forwardMessage(request.getThreadId(), request.getMessageIds(), config.getTypeCode());
    }

    /**
     * @param messageIds
     * @param deleteForAll
     */

    public List<String> deleteMultipleMessage(ArrayList<Long> messageIds, Boolean deleteForAll, String typeCode) {
        String uniqueId = generateUniqueId();

        List<String> uniqueIds = new ArrayList<>();

        if (state == ChatState.ChatReady) {

            deleteForAll = deleteForAll != null ? deleteForAll : false;

            BaseMessage baseMessage = new BaseMessage();


            for (Long id : messageIds) {

                String uniqueId1 = generateUniqueId();

                uniqueIds.add(uniqueId1);
            }

            JsonObject contentObj = new JsonObject();


            JsonElement messageIdsElement = gson.toJsonTree(messageIds, new TypeToken<List<Long>>() {
            }.getType());

            JsonElement uniqueIdsElement = gson.toJsonTree(uniqueIds, new TypeToken<List<String>>() {
            }.getType());


            contentObj.add("ids", messageIdsElement.getAsJsonArray());
            contentObj.add("uniqueIds", uniqueIdsElement.getAsJsonArray());
            contentObj.addProperty("deleteForAll", deleteForAll);


            baseMessage.setContent(contentObj.toString());
            baseMessage.setToken(config.getToken());
            baseMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            baseMessage.setType(ChatMessageType.DELETE_MESSAGE);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(baseMessage);


            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", config.getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }


            jsonObject.remove("subjectId");

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, "SEND_DELETE_MESSAGE");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueIds;
    }

    /**
     * DELETE MESSAGES IN THREAD
     * <p>
     * messageId    ID of the messages that you want to be removed.
     * deleteForAll If you want to delete messages for everyone you can set it true if u don't want
     * you can set it false or even null.
     */

    public List<String> deleteMultipleMessage(RequestDeleteMessage request) {

        return deleteMultipleMessage(request.getMessageIds(), request.isDeleteForAll(), config.getTypeCode());
    }

    /**
     * DELETE MESSAGE IN THREAD
     * <p>
     * messageId    ID of the message that you want to be removed.
     * deleteForAll If you want to delete message for everyone you can set it true if u don't want
     * you can set it false or even null.
     */

    public String deleteMessage(RequestDeleteMessage request) {
        if (request.getMessageIds().size() > 1) {
            return getErrorOutPut(ChatConstant.ERROR_NUMBER_MESSAGE_ID, ChatConstant.ERROR_CODE_NUMBER_MESSAGEID, null);

        }
        return deleteMessage(request.getMessageIds().get(0), request.isDeleteForAll(), config.getTypeCode());
    }

    /**
     * Get the participant list of specific thread
     * <p>
     *
     * @ param long threadId : id of the thread we want to get the participant list
     * @ param long count : number of the participant wanted to get
     * @ param long offset : offset of the participant list
     */

    public String getThreadParticipants(RequestThreadParticipant request) {

        long count = request.getCount();
        long offset = request.getOffset();
        long threadId = request.getThreadId();
        String typeCode = config.getTypeCode();

        return getThreadParticipantsMain((int) count, offset, threadId, typeCode, false);
    }

    /**
     * Get the participant list of specific thread
     *
     * @param threadId id of the thread we want to ge the participant list
     */
    @Deprecated
    public String getThreadParticipants(Integer count, Long offset, long threadId) {
        return getThreadParticipantsMain(count, offset, threadId, null, false);
    }

    private String getThreadParticipantsMain(Integer count, Long offset, long threadId, String typeCode, boolean admin) {
        String uniqueId = generateUniqueId();

        offset = offset != null ? offset : 0;
        count = count != null ? count : 50;

        if (state == ChatState.ChatReady) {

            ChatMessageContent chatMessageContent = new ChatMessageContent();

            chatMessageContent.setCount(count);
            chatMessageContent.setOffset(offset);
            chatMessageContent.setAdmin(admin);

            String content = gson.toJson(chatMessageContent);

            ChatMessage chatMessage = new ChatMessage();

            chatMessage.setContent(content);
            chatMessage.setType(ChatMessageType.THREAD_PARTICIPANTS);
            chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            chatMessage.setToken(config.getToken());
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setSubjectId(threadId);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", config.getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            jsonObject.remove("lastMessageId");
            jsonObject.remove("firstMessageId");
            jsonObject.remove("contentCount");
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, "SEND_THREAD_PARTICIPANT");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }


    public String createThread(RequestCreateThread requestCreateThread) {
        int threadType = requestCreateThread.getType();
        Invitee[] invitee = (Invitee[]) requestCreateThread.getInvitees().toArray();
        String threadTitle = requestCreateThread.getTitle();
        String description = requestCreateThread.getDescription();
        String image = requestCreateThread.getImage();
        String metadata = requestCreateThread.getMetadata();
        String typeCode = config.getTypeCode();

        return createThread(threadType, invitee, threadTitle, description, image, metadata, typeCode);
    }

    /**
     * Create the thread p to p/channel/group. The list below is showing all threads type
     * int NORMAL = 0;
     * int OWNER_GROUP = 1;
     * int PUBLIC_GROUP = 2;
     * int CHANNEL_GROUP = 4;
     * int TO_BE_USER_ID = 5;
     * <p>
     * int CHANNEL = 8;
     */
    public String createThread(int threadType, Invitee[] invitee, String threadTitle, String description, String image
            , String metadata, String typeCode) {

        String uniqueId = generateUniqueId();

        if (state == ChatState.ChatReady) {
            List<Invitee> invitees = new ArrayList<>(Arrays.asList(invitee));

            ChatThread chatThread = new ChatThread();
            chatThread.setType(threadType);
            chatThread.setInvitees(invitees);
            chatThread.setTitle(threadTitle);

            JsonObject chatThreadObject = (JsonObject) gson.toJsonTree(chatThread);

            if (Util.isNullOrEmpty(description)) {
                chatThreadObject.remove("description");
            } else {
                chatThreadObject.remove("description");
                chatThreadObject.addProperty("description", description);
            }

            if (Util.isNullOrEmpty(image)) {
                chatThreadObject.remove("image");
            } else {
                chatThreadObject.remove("image");
                chatThreadObject.addProperty("image", image);
            }

            if (Util.isNullOrEmpty(metadata)) {
                chatThreadObject.remove("metadata");

            } else {
                chatThreadObject.remove("metadata");
                chatThreadObject.addProperty("metadata", metadata);
            }
            String contentThreadChat = chatThreadObject.toString();

            ChatMessage chatMessage = getChatMessage(contentThreadChat, uniqueId, config.getTypeCode());

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", config.getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, "SEND_CREATE_THREAD");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * Create the thread with message is just for  p to p.( Thread Type is int NORMAL = 0)
     *
     * @return The first UniqueId is for create thread and the rest of them are for new message or forward messages
     * Its have three kind of Unique Ids. One of them is for message. One of them for Create Thread
     * and the others for Forward Message or Messages.
     * <p>
     * int type : Type of the Thread (You can have Thread Type from ThreadType.Class)
     * String ownerSsoId  [Optional]
     * List<Invitee> invitees  you can add your invite list here
     * String title  [Optional] title of the group thread
     * <p>
     * RequestThreadInnerMessage message{  object of the inner message
     * <p>
     * -------------  String text  text of the message
     * -------------  int type  type of the message  [Optional]
     * -------------  String metadata  [Optional]
     * -------------  String systemMetadata  [Optional]
     * -------------  List<Long> forwardedMessageIds  [Optional]
     * }
     */

    public ArrayList<String> createThreadWithMessage(RequestCreateThreadWithMessage threadRequest) {
        List<String> forwardUniqueIds;
        JsonObject innerMessageObj = null;
        JsonObject jsonObject;

        String threadUniqueId = generateUniqueId();

        ArrayList<String> uniqueIds = new ArrayList<>();
        uniqueIds.add(threadUniqueId);
        try {
            if (state == ChatState.ChatReady) {

                if (threadRequest.getMessage() != null) {
                    RequestThreadInnerMessage innerMessage = threadRequest.getMessage();
                    innerMessageObj = (JsonObject) gson.toJsonTree(innerMessage);

                    if (Util.isNullOrEmpty(threadRequest.getMessage().getType())) {
                        innerMessageObj.remove("type");
                    }

                    if (Util.isNullOrEmpty(threadRequest.getMessage().getText())) {
                        innerMessageObj.remove("message");
                    } else {
                        String newMsgUniqueId = generateUniqueId();

                        innerMessageObj.addProperty("uniqueId", newMsgUniqueId);
                        uniqueIds.add(newMsgUniqueId);
                    }

                    if (!Util.isNullOrEmptyNumber(threadRequest.getMessage().getForwardedMessageIds())) {

                        /* Its generated new unique id for each forward message*/
                        List<Long> messageIds = threadRequest.getMessage().getForwardedMessageIds();
                        forwardUniqueIds = new ArrayList<>();

                        for (long ids : messageIds) {
                            String frwMsgUniqueId = generateUniqueId();

                            forwardUniqueIds.add(frwMsgUniqueId);
                            uniqueIds.add(frwMsgUniqueId);
                        }
                        JsonElement element = gson.toJsonTree(forwardUniqueIds, new TypeToken<List<Long>>() {
                        }.getType());

                        JsonArray jsonArray = element.getAsJsonArray();
                        innerMessageObj.add("forwardedUniqueIds", jsonArray);
                    } else {
                        innerMessageObj.remove("forwardedUniqueIds");
                        innerMessageObj.remove("forwardedMessageIds");
                    }
                }

                JsonObject jsonObjectCreateThread = (JsonObject) gson.toJsonTree(threadRequest);

                jsonObjectCreateThread.remove("count");
                jsonObjectCreateThread.remove("offset");
                jsonObjectCreateThread.add("message", innerMessageObj);

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setContent(jsonObjectCreateThread.toString());
                chatMessage.setType(ChatMessageType.INVITATION);
                chatMessage.setUniqueId(threadUniqueId);
                chatMessage.setToken(config.getToken());
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));


                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                jsonObject.remove("repliedTo");
                jsonObject.remove("subjectId");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("contentCount");

                String typeCode = config.getTypeCode();

                if (!Util.isNullOrEmpty(typeCode)) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", typeCode);
                } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", config.getTypeCode());
                } else {
                    jsonObject.remove("typeCode");
                }

                sendAsyncMessage(jsonObject.toString(), "SEND_CREATE_THREAD_WITH_MESSAGE");
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, threadUniqueId);
            }

        } catch (Throwable e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueIds;
    }

    /**
     * It updates the information of the thread like
     * image;
     * name;
     * description;
     * metadata;
     */
    public String updateThreadInfo(long threadId, ThreadInfoVO threadInfoVO) {
        String uniqueId;
        uniqueId = generateUniqueId();
        try {
            if (state == ChatState.ChatReady) {
                JsonObject jObj = new JsonObject();

                jObj.addProperty("name", threadInfoVO.getTitle());
                jObj.addProperty("description", threadInfoVO.getDescription());
                jObj.addProperty("metadata", threadInfoVO.getMetadata());
                jObj.addProperty("image", threadInfoVO.getImage());

                String content = jObj.toString();

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setContent(content);

                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setToken(config.getToken());
                chatMessage.setSubjectId(threadId);
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setType(ChatMessageType.UPDATE_THREAD_INFO);

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");

                if (Util.isNullOrEmpty(config.getTypeCode())) {
                    if (Util.isNullOrEmpty(config.getTypeCode())) {
                        jsonObject.remove("typeCode");
                    } else {
                        jsonObject.addProperty("typeCode", config.getTypeCode());
                    }
                }
                //remove empty else

                sendAsyncMessage(jsonObject.toString(), "SEND_UPDATE_THREAD_INFO");

            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }

        return uniqueId;
    }

    /**
     * It updates the information of the thread like
     * image;
     * name;
     * description;
     * metadata;
     */


    public String updateThreadInfo(RequestThreadInfo request) {
        ThreadInfoVO threadInfoVO = new ThreadInfoVO.Builder().title(request.getName())
                .description(request.getDescription())
                .image(request.getImage())
                .metadat(request.getMetadata())
                .build();
        return updateThreadInfo(request.getThreadId(), threadInfoVO);
    }

    /**
     * Reply the message in the current thread and send az message and receive at the
     * <p>
     * messageContent content of the reply message
     * threadId    :   id of the thread
     * messageId  :    of the message that we want to reply
     * metaData   :   metadata of the message
     */

    public String replyMessage(RequestReplyMessage request) {
        long threadId = request.getThreadId();
        long messageId = request.getMessageId();
        String messageContent = request.getMessageContent();
        String systemMetaData = request.getSystemMetaData();
        int messageType = request.getMessageType();
        String typeCode = config.getTypeCode();

        return mainReplyMessage(messageContent, threadId, messageId, systemMetaData, messageType, null, typeCode);
    }

    private String mainReplyMessage(String messageContent, long threadId, long messageId, String systemMetaData,
                                    Integer messageType, String metaData, String typeCode) {
        String uniqueId;
        uniqueId = generateUniqueId();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setRepliedTo(messageId);
        chatMessage.setSubjectId(threadId);
        chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
        chatMessage.setToken(config.getToken());
        chatMessage.setContent(messageContent);
        chatMessage.setMetadata(metaData);
        chatMessage.setType(ChatMessageType.MESSAGE);

        JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

        if (Util.isNullOrEmpty(systemMetaData)) {
            jsonObject.remove("systemMetaData");
        } else {
            jsonObject.remove("systemMetaData");
            jsonObject.addProperty("systemMetaData", systemMetaData);
        }

        if (!Util.isNullOrEmpty(typeCode)) {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", typeCode);
        } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", config.getTypeCode());
        } else {
            jsonObject.remove("typeCode");
        }

        if (Util.isNullOrEmpty(messageType)) {
            jsonObject.remove("messageType");
        } else {
            jsonObject.remove("messageType");
            jsonObject.addProperty("messageType", messageType);
        }

        String asyncContent = jsonObject.toString();

        if (state == ChatState.ChatReady) {
            sendAsyncMessage(asyncContent, "SEND_REPLY_MESSAGE");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * Reply the message in the current thread and send az message and receive at the
     *
     * @param messageContent content of the reply message
     * @param threadId       id of the thread
     * @param messageId      of the message that we want to reply
     * @param systemMetaData metadata of the message
     */

    public String replyMessage(String messageContent, long threadId, long messageId, String systemMetaData
            , Integer messageType, String typeCode) {
        return mainReplyMessage(messageContent, threadId, messageId, systemMetaData, messageType, null, typeCode);
    }

    /**
     * In order to send seen message you have to call this method
     */
    public String seenMessage(long messageId, long ownerId) {
        String uniqueId;
        uniqueId = generateUniqueId();
        if (state == ChatState.ChatReady) {
            if (ownerId != getUserId()) {
                ChatMessage message = new ChatMessage();
                message.setType(ChatMessageType.SEEN);
                message.setContent(String.valueOf(messageId));
                message.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                message.setToken(config.getToken());
                message.setUniqueId(uniqueId);

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(message);

                if (Util.isNullOrEmpty(config.getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", config.getTypeCode());
                }
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");

                String asyncContent = jsonObject.toString();

                sendAsyncMessage(asyncContent, "SEND_SEEN_MESSAGE");

            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * In order to send seen message you have to call {@link #seenMessage(long, long)}
     */

    public String seenMessage(RequestSeenMessage request) {
        long messageId = request.getMessageId();
        long ownerId = request.getOwnerId();

        return seenMessage(messageId, ownerId);
    }

    /**
     * It Gets the information of the current user
     */

    public String getUserInfo() {
        String uniqueId = generateUniqueId();
        try {
            if (state == ChatState.AsyncReady) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.USER_INFO);
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setToken(config.getToken());
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                if (Util.isNullOrEmpty(config.getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", config.getTypeCode());
                }

                String asyncContent = jsonObject.toString();

                showInfoLog("SEND_USER_INFO", asyncContent);

                async.sendMessage(asyncContent, Message, null);

            }

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueId;
    }

    /**
     * It uploads image to the server just by pass image path
     *
     * @param requestUploadImage filePath   the path of image File
     *                           xC   the X coordinate of the upper-left corner of the specified rectangular region
     *                           yC   the Y coordinate of the upper-left corner of the specified rectangular region
     *                           wC   the width of the specified rectangular region
     *                           hC   the height of the specified rectangular region
     * @return uploadImage(filePath, xC, yC, hC, wC);
     */

    public String uploadImage(RequestUploadImage requestUploadImage) {
        String filePath = requestUploadImage.getFilePath();
        int xC = requestUploadImage.getxC();
        int yC = requestUploadImage.getyC();
        int hC = requestUploadImage.gethC();
        int wC = requestUploadImage.getwC();

        if (filePath.endsWith(".gif")) {
            return uploadFile(filePath);
        } else {
            return uploadImage(filePath, xC, yC, hC, wC);
        }
    }

    /**
     * @param filePath the path of image File
     * @param xC       the X coordinate of the upper-left corner of the specified rectangular region
     * @param yC       the Y coordinate of the upper-left corner of the specified rectangular region
     * @param hC       the height of the specified rectangular region
     * @param wC       the width of the specified rectangular region
     * @return uniqueId
     */
    @Deprecated
    public String uploadImage(String filePath, int xC, int yC, int hC, int wC) {
        String uniqueId = generateUniqueId();

        if (state == ChatState.ChatReady) {

            try {
                if (config.getFileServer() != null && filePath != null && !filePath.isEmpty()) {
                    File file = new File(filePath);

                    if (file.exists()) {

                        String mimeType = getContentType(file);

                        if (mimeType.equals("image/png") || mimeType.equals("image/jpeg")) {
                            FileApi fileApi;
                            RequestBody requestFile;

                            if (!Util.isNullOrEmpty(hC) && !Util.isNullOrEmpty(wC)) {
                                BufferedImage originalImage = ImageIO.read(file);

                                BufferedImage subImage = originalImage.getSubimage(xC, yC, wC, hC);

                                File outputFile = File.createTempFile("test", null);

                                ImageIO.write(subImage, mimeType.substring(mimeType.indexOf("/") + 1), outputFile);

                                fileApi = RetrofitHelperFileServer.getInstance(config.getFileServer()).create(FileApi.class);

                                requestFile = RequestBody.create(MediaType.parse("image/*"), outputFile);
                            } else {

                                fileApi = RetrofitHelperFileServer.getInstance(config.getFileServer()).create(FileApi.class);

                                requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                            }

                            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());

                            Call<FileImageUpload> fileImageUploadCall = fileApi.sendImageFile(body, config.getToken(), TOKEN_ISSUER, name);
                            String finalUniqueId = uniqueId;
                            String finalUniqueId1 = uniqueId;

                            RetrofitUtil.request(fileImageUploadCall, new ApiListener<FileImageUpload>() {

                                @Override
                                public void onSuccess(FileImageUpload fileImageUpload) {
                                    boolean hasError = fileImageUpload.isHasError();

                                    if (hasError) {
                                        String errorMessage = fileImageUpload.getMessage();
                                        int errorCode = fileImageUpload.getErrorCode();
                                        String jsonError = getErrorOutPut(errorMessage, errorCode, finalUniqueId1);

                                        showErrorLog(jsonError);
                                    } else {
                                        ChatResponse<ResultImageFile> chatResponse = new ChatResponse<>();
                                        ResultImageFile resultImageFile = new ResultImageFile();
                                        chatResponse.setUniqueId(finalUniqueId);

                                        resultImageFile.setId(fileImageUpload.getResult().getId());
                                        resultImageFile.setHashCode(fileImageUpload.getResult().getHashCode());
                                        resultImageFile.setName(fileImageUpload.getResult().getName());
                                        resultImageFile.setHeight(fileImageUpload.getResult().getHeight());
                                        resultImageFile.setWidth(fileImageUpload.getResult().getWidth());
                                        resultImageFile.setActualHeight(fileImageUpload.getResult().getActualHeight());
                                        resultImageFile.setActualWidth(fileImageUpload.getResult().getActualWidth());
                                        chatResponse.setResult(resultImageFile);
                                        String imageJson = gson.toJson(chatResponse);
                                        listener.onUploadImageFile(imageJson, chatResponse);
                                        showInfoLog("RECEIVE_UPLOAD_IMAGE");
                                        listener.OnLogEvent(imageJson);
                                    }
                                }

                                @Override
                                public void onError(Throwable throwable) {
                                    showErrorLog(throwable.getMessage());
                                }

                                @Override
                                public void onServerError(Response<FileImageUpload> response) {
                                    if (response.body() != null) {
                                        showErrorLog(response.body().getMessage());
                                    }
                                }
                            });
                        } else {
                            String jsonError = getErrorOutPut(ChatConstant.ERROR_NOT_IMAGE, ChatConstant.ERROR_CODE_NOT_IMAGE, null);

                            showErrorLog(jsonError);
                            uniqueId = null;
                        }
                    }

                } else {
                    showErrorLog("FileServer url Is null");

                    uniqueId = null;
                }
            } catch (Exception e) {
                showErrorLog(e.getCause().getMessage());
                getErrorOutPut(ChatConstant.ERROR_UPLOAD_FILE, ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId);
                uniqueId = null;
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * It uploads file to file server
     */
    @Deprecated
    public String uploadFile(String path) {
        String uniqueId = generateUniqueId();
        if (state == ChatState.ChatReady) {
            try {
                if (config.getFileServer() != null && path != null && !path.isEmpty()) {

                    File file = new File(path);
                    String mimeType = getContentType(file);

                    if (file.exists()) {

                        long fileSize = file.length();
                        FileApi fileApi = RetrofitHelperFileServer.getInstance(config.getFileServer()).create(FileApi.class);

                        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                        RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);

                        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                        Call<FileUpload> fileUploadCall = fileApi.sendFile(body, config.getToken(), TOKEN_ISSUER, name);

                        String finalUniqueId = uniqueId;
                        RetrofitUtil.request(fileUploadCall, new ApiListener<FileUpload>() {

                            @Override
                            public void onSuccess(FileUpload fileUpload) {
                                boolean hasError = fileUpload.isHasError();
                                if (hasError) {
                                    String errorMessage = fileUpload.getMessage();
                                    int errorCode = fileUpload.getErrorCode();
                                    String jsonError = getErrorOutPut(errorMessage, errorCode, finalUniqueId);
                                    showErrorLog(jsonError);
                                } else {
                                    ResultFile result = fileUpload.getResult();
                                    ChatResponse<ResultFile> chatResponse = new ChatResponse<>();
                                    result.setSize(fileSize);
                                    chatResponse.setUniqueId(finalUniqueId);
                                    chatResponse.setResult(result);
                                    String json = gson.toJson(chatResponse);
                                    listener.onUploadFile(json, chatResponse);
                                    showInfoLog("RECEIVE_UPLOAD_FILE" + json);
                                    listener.OnLogEvent(json);
                                }
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                showErrorLog(throwable.getMessage());
                            }

                            @Override
                            public void onServerError(Response<FileUpload> response) {
                                if (response.body() != null) {
                                    showErrorLog(response.body().getMessage());
                                }
                            }
                        });
                    } else {
                        String jsonError = getErrorOutPut(ChatConstant.ERROR_NOT_IMAGE, ChatConstant.ERROR_CODE_NOT_IMAGE, null);
                        showErrorLog(jsonError);
                        uniqueId = null;
                    }

                } else {
                    showErrorLog("File is not Exist");
                    return null;
                }

            } catch (Exception e) {
                showErrorLog(e.getCause().getMessage());
                return null;
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }


    public String uploadFile(RequestUploadFile requestUploadFile) {
        return uploadFile(requestUploadFile.getFilePath());
    }

    /**
     * This method first check the type of the file and then choose the right
     * server and send that
     *
     * @param description    It's the description that you want to send with file in the thread
     * @param filePath       Path of the file that you want to send to thread
     * @param threadId       ID of the thread that you want to send file
     * @param systemMetaData [optional]
     * @param xC             The X coordinate of the upper-left corner of the specified rectangular region [optional - for image file]
     * @param yC             The Y coordinate of the upper-left corner of the specified rectangular region[optional - for image file]
     * @param hC             The height of the specified rectangular region[optional -  for image file]
     * @param wC             The width of the specified rectangular region[optional - for image file]
     * @param handler        It is for send file message with progress
     * @return uniqueId
     */
    public String sendFileMessage(String description, long threadId, String filePath, String systemMetaData, Integer messageType, int xC, int yC, int hC, int wC, ProgressHandler.sendFileMessage handler) {

        String uniqueId = generateUniqueId();

        LFileUpload lFileUpload = new LFileUpload();
        lFileUpload.setDescription(description);
        lFileUpload.setFilePath(filePath);
        lFileUpload.setHandler(handler);
        lFileUpload.setMessageType(messageType);
        lFileUpload.setThreadId(threadId);
        lFileUpload.setUniqueId(uniqueId);
        lFileUpload.setSystemMetaData(systemMetaData);
        lFileUpload.setxC(xC);
        lFileUpload.setyC(yC);
        lFileUpload.sethC(hC);
        lFileUpload.setwC(wC);

        try {
            if (filePath != null) {
                File file = new File(filePath);
                String mimeType = getContentType(file);
                lFileUpload.setMimeType(mimeType);

                if (FileUtils.isImage(mimeType)) {
                    uploadImageFileMessage(lFileUpload);
                } else {
                    uploadFileMessage(lFileUpload);
                }
                return uniqueId;

            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_INVALID_FILE_URI
                        , ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);
                ErrorOutPut error = new ErrorOutPut(true, ChatConstant.ERROR_INVALID_FILE_URI, ChatConstant.ERROR_CODE_INVALID_FILE_URI, uniqueId);
                listener.OnLogEvent(jsonError);
                if (handler != null) {
                    handler.onError(jsonError, error);
                }
            }
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());

            return null;
        }
        return uniqueId;
    }

    /**
     * @param requestFileMessage description    It's the description that you want to send with file in the thread
     *                           filePath       Path of the file that you want to send to thread
     *                           threadId       ID of the thread that you want to send file
     *                           systemMetaData [optional]
     *                           xC             The X coordinate of the upper-left corner of the specified rectangular region [optional - for image file]
     *                           yC             The Y coordinate of the upper-left corner of the specified rectangular region[optional - for image file]
     *                           hC             The height of the specified rectangular region[optional -  for image file]
     *                           wC             The width of the specified rectangular region[optional - for image file]
     * @param handler
     * @return
     */

    public String sendFileMessage(RequestFileMessage requestFileMessage, ProgressHandler.sendFileMessage handler) {
        long threadId = requestFileMessage.getThreadId();
        String filePath = requestFileMessage.getFilePath();
        String description = requestFileMessage.getDescription();
        int messageType = requestFileMessage.getMessageType();
        String systemMetadata = requestFileMessage.getSystemMetadata();
        int xC = requestFileMessage.getxC();
        int yC = requestFileMessage.getyC();
        int hC = requestFileMessage.gethC();
        int wC = requestFileMessage.getwC();

        return sendFileMessage(description, threadId, filePath, systemMetadata, messageType, xC, yC, hC, wC, handler);
    }

    private void uploadFileMessage(LFileUpload lFileUpload) {

        String filePath = lFileUpload.getFilePath();

        try {
            if (Util.isNullOrEmpty(filePath)) {
                filePath = "";
            }
            File file = new File(filePath);
            long file_size;

            if (file.exists() || file.isFile()) {
                file_size = file.length();

                lFileUpload.setFileSize(file_size);
                lFileUpload.setFile(file);

                mainUploadFileMessage(lFileUpload);


            } else {
                showErrorLog("File Is Not Exist");
            }

        } catch (Throwable e) {
            showErrorLog(e.getCause().getMessage());
        }
    }

    private void mainUploadFileMessage(LFileUpload lFileUpload) {

        String description = lFileUpload.getDescription();
        Integer messageType = lFileUpload.getMessageType();
        long threadId = lFileUpload.getThreadId();
        String uniqueId = lFileUpload.getUniqueId();
        String systemMetadata = lFileUpload.getSystemMetaData();
        long messageId = lFileUpload.getMessageId();
        String mimeType = lFileUpload.getMimeType();
        File file = lFileUpload.getFile();
        long file_size = lFileUpload.getFileSize();
        String typeCode = config.getTypeCode();

        String methodName = lFileUpload.getMethodName();

        if (state == ChatState.ChatReady) {
            if (config.getFileServer() != null) {
                FileApi fileApi = RetrofitHelperFileServer.getInstance(config.getFileServer()).create(FileApi.class);

                RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), file.getName());
                RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);

                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                Call<FileUpload> fileUploadCall = fileApi.sendFile(body, config.getToken(), TOKEN_ISSUER, name);
                File finalFile = file;

                RetrofitUtil.request(fileUploadCall, new ApiListener<FileUpload>() {

                    @Override
                    public void onSuccess(FileUpload fileUpload) {
                        boolean error = fileUpload.isHasError();

                        if (error) {
                            String errorMessage = fileUpload.getMessage();

                            showErrorLog(errorMessage);

                        } else {
                            ResultFile result = fileUpload.getResult();

                            if (result != null) {
                                long fileId = result.getId();
                                String hashCode = result.getHashCode();

                                ChatResponse<ResultFile> chatResponse = new ChatResponse<>();
                                chatResponse.setResult(result);
                                chatResponse.setUniqueId(uniqueId);
                                result.setSize(file_size);
                                String json = gson.toJson(chatResponse);
                                listener.onUploadFile(json, chatResponse);
                                showInfoLog("RECEIVE_UPLOAD_FILE");
                                listener.OnLogEvent(json);
                                String jsonMeta = createFileMetadata(finalFile.getName(),
                                        hashCode,
                                        fileId,
                                        mimeType,
                                        file_size,
                                        "");
                                listener.OnLogEvent(jsonMeta);
                                if (!Util.isNullOrEmpty(methodName) && methodName.equals(ChatConstant.METHOD_REPLY_MSG)) {
                                    mainReplyMessage(description, threadId, messageId, systemMetadata, messageType, jsonMeta, typeCode);

                                    showInfoLog("SEND_REPLY_FILE_MESSAGE");

                                } else {
                                    sendTextMessageWithFile(description, threadId, jsonMeta, systemMetadata, uniqueId, typeCode, messageType);
                                }
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        showErrorLog(throwable.getMessage());
                    }

                    @Override
                    public void onServerError(Response<FileUpload> response) {
                        showErrorLog(response.body().getMessage());
                    }
                });
            } else {
                showErrorLog("FileServer url Is null");
            }

        } else {
            String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            listener.OnLogEvent(jsonError);
        }
    }

    private void sendTextMessageWithFile(String description, long threadId, String metaData, String systemMetadata, String uniqueId, String typeCode, Integer messageType) {

        ChatMessage chatMessage = new ChatMessage();

        chatMessage.setContent(description);
        chatMessage.setType(ChatMessageType.MESSAGE);
        chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
        chatMessage.setToken(config.getToken());
        chatMessage.setMetadata(metaData);

        chatMessage.setUniqueId(uniqueId);
        chatMessage.setSubjectId(threadId);

        JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

        if (Util.isNullOrEmpty(config.getTypeCode())) {
            jsonObject.remove("typeCode");
        } else {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", config.getTypeCode());
        }

        jsonObject.remove("repliedTo");

        String asyncContent = jsonObject.toString();


        if (state == ChatState.ChatReady) {
            sendAsyncMessage(asyncContent, "SEND_TXT_MSG_WITH_FILE");
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
    }

    /**
     * description  :  description of the message
     * threadId     :  id of the thread It's wanted to send in
     * fileUri      :  uri of the file
     * mimeType     :  mime type of the file
     * systemMetaData : metadata of the message
     * messageType  :  type of message
     * messageId    :  id of a message
     * methodName   :  METHOD_REPLY_MSG or other
     * handler      :  description of the interface methods are :
     * bytesSent    :   - Bytes sent since the last time this callback was called.
     * totalBytesSent :  - Total number of bytes sent so far.
     * totalBytesToSend : - Total bytes to send.
     */
    private void uploadImageFileMessage(LFileUpload lFileUpload) {

        String description = lFileUpload.getDescription();
        String filePath = lFileUpload.getFilePath();
        Integer messageType = lFileUpload.getMessageType();
        String uniqueId = lFileUpload.getUniqueId();
        String systemMetaData = lFileUpload.getSystemMetaData();
        String center = lFileUpload.getCenter();


        systemMetaData = systemMetaData != null ? systemMetaData : "";
        description = description != null ? description : "";
        messageType = messageType != null ? messageType : 0;

        if (Util.isNullOrEmpty(filePath)) {
            filePath = "";
        }
        File file = new File(filePath);

        if (file.exists()) {
            long fileSize = file.length();

            lFileUpload.setFile(file);
            lFileUpload.setFileSize(fileSize);
            lFileUpload.setSystemMetaData(systemMetaData);
            lFileUpload.setDescription(description);
            lFileUpload.setMessageType(messageType);
            lFileUpload.setCenter(center);

            mainUploadImageFileMsg(lFileUpload);

        } else {
            showErrorLog("File Is Not Exist");
        }

    }

    private void mainUploadImageFileMsg(LFileUpload fileUpload) {

        String description = fileUpload.getDescription();

        ProgressHandler.sendFileMessage handler = fileUpload.getHandler();

        Integer messageType = fileUpload.getMessageType();
        long threadId = fileUpload.getThreadId();
        String uniqueId = fileUpload.getUniqueId();
        String systemMetaData = fileUpload.getSystemMetaData();
        long messageId = fileUpload.getMessageId();
        String mimeType = fileUpload.getMimeType();
        String methodName = fileUpload.getMethodName();
        long fileSize = fileUpload.getFileSize();
        String center = fileUpload.getCenter();
        String typeCode = config.getTypeCode();

        File file = fileUpload.getFile();
        try {
            if (state == ChatState.ChatReady) {

                if (config.getFileServer() != null) {
                    FileApi fileApi = RetrofitHelperFileServer.getInstance(config.getFileServer()).create(FileApi.class);

                    RequestBody requestFile;

                    if (!Util.isNullOrEmpty(fileUpload.gethC()) && !Util.isNullOrEmpty(fileUpload.getwC())) {
                        BufferedImage originalImage = ImageIO.read(file);

                        BufferedImage subImage = originalImage.getSubimage(fileUpload.getxC(), fileUpload.getyC(), fileUpload.getwC(), fileUpload.gethC());

                        File outputFile = File.createTempFile("test", null);

                        ImageIO.write(subImage, mimeType.substring(mimeType.indexOf("/") + 1), outputFile);


                        requestFile = RequestBody.create(MediaType.parse("image/*"), outputFile);
                    } else {


                        requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                    }

                    MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                    RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());

                    Call<FileImageUpload> fileImageUploadCall = fileApi.sendImageFile(body, config.getToken(), TOKEN_ISSUER, name);

                    RetrofitUtil.request(fileImageUploadCall, new ApiListener<FileImageUpload>() {

                        @Override
                        public void onSuccess(FileImageUpload fileImageUpload) {
                            boolean hasError = fileImageUpload.isHasError();
                            if (hasError) {
                                String errorMessage = fileImageUpload.getMessage();
                                int errorCode = fileImageUpload.getErrorCode();
                                String jsonError = getErrorOutPut(errorMessage, errorCode, uniqueId);
                                listener.OnLogEvent(jsonError);
                                ErrorOutPut error = new ErrorOutPut(true, errorMessage, errorCode, uniqueId);

                                if (handler != null) {
                                    handler.onError(jsonError, error);
                                }
                            } else {

                                ResultImageFile result = fileImageUpload.getResult();
                                long imageId = result.getId();
                                String hashCode = result.getHashCode();

                                ChatResponse<ResultImageFile> chatResponse = new ChatResponse<>();
                                ResultImageFile resultImageFile = new ResultImageFile();
                                chatResponse.setUniqueId(uniqueId);
                                resultImageFile.setId(result.getId());
                                resultImageFile.setHashCode(result.getHashCode());
                                resultImageFile.setName(result.getName());
                                resultImageFile.setHeight(result.getHeight());
                                resultImageFile.setWidth(result.getWidth());
                                resultImageFile.setActualHeight(result.getActualHeight());
                                resultImageFile.setActualWidth(result.getActualWidth());
                                chatResponse.setResult(resultImageFile);
                                String imageJson = gson.toJson(chatResponse);
                                listener.onUploadImageFile(imageJson, chatResponse);
                                showInfoLog("RECEIVE_UPLOAD_IMAGE");
                                listener.OnLogEvent(imageJson);
                                String metaJson;
                                if (!Util.isNullOrEmpty(methodName) && methodName.equals(ChatConstant.METHOD_LOCATION_MSG)) {
                                    metaJson = createImageMetadata(file, hashCode, imageId, result.getActualHeight()
                                            , result.getActualWidth(), mimeType, fileSize, null, true, center);

                                } else {
                                    metaJson = createImageMetadata(file, hashCode, imageId, result.getActualHeight()
                                            , result.getActualWidth(), mimeType, fileSize, null, false, null);
                                }

                                // send to handler
                                if (handler != null) {
                                    handler.onFinishImage(imageJson, chatResponse);
                                }

                                if (!Util.isNullOrEmpty(methodName) && methodName.equals(ChatConstant.METHOD_REPLY_MSG)) {
                                    mainReplyMessage(description, threadId, messageId, systemMetaData, messageType, metaJson, typeCode);
                                    showInfoLog("SEND_REPLY_FILE_MESSAGE");
                                    listener.OnLogEvent(metaJson);
                                } else {
                                    sendTextMessageWithFile(description, threadId, metaJson, systemMetaData, uniqueId, typeCode, messageType);
                                }
                                listener.OnLogEvent(metaJson);
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            showErrorLog(throwable.getMessage());
                        }

                        @Override
                        public void onServerError(Response<FileImageUpload> response) {
                            if (response.body() != null) {
                                showErrorLog(response.body().getMessage());
                            }
                        }
                    });

                } else {
                    showErrorLog("FileServer url Is null");
                }

            } else {
                String jsonError = getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);

                listener.OnLogEvent(jsonError);
            }
        } catch (IOException e) {
            showErrorLog(e);
            getErrorOutPut(ChatConstant.ERROR_UPLOAD_FILE, ChatConstant.ERROR_CODE_UPLOAD_FILE, uniqueId);

        }
    }

    /**
     * Reply the message in the current thread and send az message and receive at the
     * <p>
     * messageContent content of the reply message
     * threadId   :   id of the thread
     * messageId  :   of the message that we want to reply
     * metaData   :    metadata of the message
     */

    public String replyFileMessage(RequestReplyFileMessage request, ProgressHandler.sendFileMessage handler) {
        String uniqueId = generateUniqueId();
        long threadId = request.getThreadId();
        String messageContent = request.getMessageContent();
        String systemMetaData = request.getSystemMetaData();
        String filePath = request.getFilePath();
        long messageId = request.getMessageId();
        int messageType = request.getMessageType();
        String methodName = ChatConstant.METHOD_REPLY_MSG;
        String typeCode;

        if (!Util.isNullOrEmpty(config.getTypeCode())) {
            typeCode = config.getTypeCode();
        } else {
            typeCode = config.getTypeCode();
        }

        LFileUpload lFileUpload = new LFileUpload();
        lFileUpload.setDescription(messageContent);
        lFileUpload.setFilePath(filePath);
        lFileUpload.setHandler(handler);
        lFileUpload.setMessageType(messageType);
        lFileUpload.setMessageId(messageId);
        lFileUpload.setMethodName(methodName);
        lFileUpload.setThreadId(threadId);
        lFileUpload.setUniqueId(uniqueId);
        lFileUpload.setSystemMetaData(systemMetaData);
        lFileUpload.setHandler(handler);
        lFileUpload.setMessageType(messageType);
        lFileUpload.setxC(request.getxC());
        lFileUpload.setyC(request.getyC());
        lFileUpload.setwC(request.getwC());
        lFileUpload.sethC(request.gethC());
        lFileUpload.setTypeCode(typeCode);

        try {
            if (filePath != null) {
                File file = new File(filePath);
                String mimeType = getContentType(file);

                lFileUpload.setMimeType(mimeType);

                if (FileUtils.isImage(mimeType)) {
                    uploadImageFileMessage(lFileUpload);
                } else {
                    uploadFileMessage(lFileUpload);
                }

                return uniqueId;

            } else {
                getErrorOutPut(ChatConstant.ERROR_INVALID_URI, ChatConstant.ERROR_CODE_INVALID_URI, uniqueId);
            }
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());

            return null;
        }
        return uniqueId;
    }

    /**
     * Message can be editing when you pass the message id and the edited
     * content in order to edit your Message.
     */
    @Deprecated
    public String editMessage(int messageId, String messageContent, String systemMetaData) {
        String uniqueId = generateUniqueId();
        try {
            if (state == ChatState.ChatReady) {

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.EDIT_MESSAGE);
                chatMessage.setToken(config.getToken());
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setSubjectId((long) messageId);
                chatMessage.setContent(messageContent);
                chatMessage.setSystemMetadata(systemMetaData);
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                if (Util.isNullOrEmpty(config.getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", config.getTypeCode());
                }

                jsonObject.remove("metadata");

                String asyncContent = jsonObject.toString();

                sendAsyncMessage(asyncContent, "SEND_EDIT_MESSAGE");

            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueId;
    }

    /**
     * Message can be editing when you pass the message id and the edited
     * content in order to edit your Message.
     */

    public String editMessage(RequestEditMessage request) {
        String uniqueId = generateUniqueId();
        try {
            JsonObject jsonObject;
            if (state == ChatState.ChatReady) {

                String messageContent = request.getMessageContent();
                long messageId = request.getMessageId();
                String metaData = request.getMetaData();

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.EDIT_MESSAGE);
                chatMessage.setToken(config.getToken());
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setSubjectId(messageId);
                chatMessage.setContent(messageContent);
                chatMessage.setSystemMetadata(metaData);
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));

                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");

                if (!Util.isNullOrEmpty(config.getTypeCode())) {
                    jsonObject.addProperty("typeCode", config.getTypeCode());
                } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", config.getTypeCode());
                } else {
                    jsonObject.remove("typeCode");
                }

                sendAsyncMessage(jsonObject.toString(), "SEND_EDIT_MESSAGE");
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueId;
    }

    /**
     * @param contactIds List of CONTACT IDs
     * @param threadId   ID of the thread that you are {*NOTICE*}admin of that,and you want to
     *                   add someone as a participant.
     */
    @Deprecated
    public String addParticipants(long threadId, List<Long> contactIds) {
        String uniqueId = generateUniqueId();
        try {

            if (state == ChatState.ChatReady) {
                AddParticipant addParticipant = new AddParticipant();
                addParticipant.setSubjectId(threadId);
                addParticipant.setUniqueId(uniqueId);
                JsonArray contacts = new JsonArray();
                for (Long p : contactIds) {
                    contacts.add(p);
                }
                addParticipant.setContent(contacts.toString());
                addParticipant.setSubjectId(threadId);
                addParticipant.setToken(config.getToken());
                addParticipant.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                addParticipant.setUniqueId(uniqueId);
                addParticipant.setType(ChatMessageType.ADD_PARTICIPANT);

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(addParticipant);

                if (Util.isNullOrEmpty(config.getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", config.getTypeCode());
                }

                String asyncContent = jsonObject.toString();

                sendAsyncMessage(asyncContent, "SEND_ADD_PARTICIPANTS");

            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }


        } catch (Throwable t) {
            showErrorLog(t.getCause().getMessage());
        }

        return uniqueId;
    }

    /**
     * contactIds  List of CONTACT IDs
     * threadId   ID of the thread that you are {*NOTICE*}admin of that,and you are going to
     * add someone as a participant.
     */

    public String addParticipants(RequestAddParticipants request) {

        String uniqueId = generateUniqueId();

        if (state == ChatState.ChatReady) {
            JsonArray contacts = new JsonArray();

            for (Long p : request.getContactIds()) {
                contacts.add(p);
            }
            ChatMessage chatMessage = new ChatMessage();

            chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            chatMessage.setToken(config.getToken());
            chatMessage.setContent(contacts.toString());
            chatMessage.setSubjectId(request.getThreadId());
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setType(ChatMessageType.ADD_PARTICIPANT);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
            jsonObject.remove("contentCount");
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");

            String typeCode = config.getTypeCode();

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", config.getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            sendAsyncMessage(jsonObject.toString(), "SEND_ADD_PARTICIPANTS");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * @param participantIds List of PARTICIPANT IDs that gets from {@link #getThreadParticipants}
     * @param threadId       ID of the thread that we want to remove their participant
     */
    public String removeParticipants(long threadId, List<Long> participantIds, String typeCode) {
        String uniqueId = generateUniqueId();

        if (state == ChatState.ChatReady) {
            RemoveParticipant removeParticipant = new RemoveParticipant();

            removeParticipant.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            removeParticipant.setType(ChatMessageType.REMOVE_PARTICIPANT);
            removeParticipant.setSubjectId(threadId);
            removeParticipant.setToken(config.getToken());
            removeParticipant.setUniqueId(uniqueId);

            JsonArray contacts = new JsonArray();

            for (Long p : participantIds) {
                contacts.add(p);
            }
            removeParticipant.setContent(contacts.toString());

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(removeParticipant);

            jsonObject.remove("contentCount");
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", config.getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, "SEND_REMOVE_PARTICIPANT");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * participantIds List of PARTICIPANT IDs from Thread's Participants object
     * threadId       ID of the thread that we want to remove their participant
     */

    public String removeParticipants(RequestRemoveParticipants request) {

        List<Long> participantIds = request.getParticipantIds();
        long threadId = request.getThreadId();
        String typeCode = config.getTypeCode();

        return removeParticipants(threadId, participantIds, typeCode);
    }

    /**
     * It leaves the thread that you are in there
     */
    public String leaveThread(long threadId, String typeCode) {
        String uniqueId = generateUniqueId();

        if (state == ChatState.ChatReady) {
            RemoveParticipant removeParticipant = new RemoveParticipant();

            removeParticipant.setSubjectId(threadId);
            removeParticipant.setToken(config.getToken());
            removeParticipant.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            removeParticipant.setUniqueId(uniqueId);
            removeParticipant.setType(ChatMessageType.LEAVE_THREAD);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(removeParticipant);

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", config.getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, "SEND_LEAVE_THREAD");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * leaves the thread
     * <p>
     *
     * @ param threadId id of the thread
     */

    public String leaveThread(RequestLeaveThread request) {

        return leaveThread(request.getThreadId(), config.getTypeCode());
    }

    /**
     * @param requestSetAuditor You can add auditor role to someone in a thread using user id.
     */

    public String addAuditor(RequestSetAuditor requestSetAuditor) {
        SetRuleVO setRuleVO = new SetRuleVO();
//        BeanUtils.copyProperties(requestSetAuditor, setRuleVO);
        setRuleVO.setTypeCode(config.getTypeCode());

        return setRole(setRuleVO);
    }

    /**
     * @param requestSetAdmin You can add admin role to someone in a thread using user id.
     */

    public String addAdmin(RequestSetAdmin requestSetAdmin) {
        SetRuleVO setRuleVO = new SetRuleVO();
//        BeanUtils.copyProperties(requestSetAdmin, setRuleVO);
        setRuleVO.setTypeCode(config.getTypeCode());

        return setRole(setRuleVO);

    }

    /**
     * @param setRuleVO You can add some roles to someone in a thread using user id.
     */
    private String setRole(SetRuleVO setRuleVO) {
        long threadId = setRuleVO.getThreadId();
        ArrayList<RequestRole> roles = setRuleVO.getRoles();
        String uniqueId = generateUniqueId();


        if (state == ChatState.ChatReady) {
            ArrayList<UserRoleVO> userRoleVOS = new ArrayList<>();

            for (RequestRole requestRole : roles) {
                UserRoleVO userRoleVO = new UserRoleVO();
                userRoleVO.setUserId(requestRole.getId());
                userRoleVO.setRoles(requestRole.getRoleTypes());
                userRoleVOS.add(userRoleVO);
            }

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(gson.toJson(userRoleVOS));
            chatMessage.setSubjectId(threadId);
            chatMessage.setToken(config.getToken());
            chatMessage.setType(ChatMessageType.SET_ROLE_TO_USER);
            chatMessage.setTokenIssuer(String.valueOf(TOKEN_ISSUER));
            chatMessage.setUniqueId(uniqueId);

            if (!Util.isNullOrEmpty(config.getTypeCode())) {
                chatMessage.setTypeCode(config.getTypeCode());
            } else {
                chatMessage.setTypeCode(config.getTypeCode());
            }

            String asyncContent = gson.toJson(chatMessage);

            sendAsyncMessage(asyncContent, "SET_RULE_TO_USER");
        }
        return uniqueId;
    }

    /**
     * @param requestSetAuditor You can add auditor role to someone in a thread using user id.
     */

    public String removeAuditor(RequestSetAuditor requestSetAuditor) {
        SetRuleVO setRuleVO = new SetRuleVO();

        BeanUtils.copyProperties(requestSetAuditor, setRuleVO);
        setRuleVO.setTypeCode(config.getTypeCode());

        return removeRole(setRuleVO);
    }

    /**
     * @param requestSetAdmin You can add admin role to someone in a thread using user id.
     */

    public String removeAdmin(RequestSetAdmin requestSetAdmin) {
        SetRuleVO setRuleVO = new SetRuleVO();
        BeanUtils.copyProperties(requestSetAdmin, setRuleVO);
        setRuleVO.setTypeCode(config.getTypeCode());
        return removeRole(setRuleVO);
    }


    /**
     * @param setRuleVO You can remove some roles from someone in a thread using user id.
     */
    private String removeRole(SetRuleVO setRuleVO) {
        long threadId = setRuleVO.getThreadId();
        ArrayList<RequestRole> roles = setRuleVO.getRoles();
        String uniqueId = generateUniqueId();

        if (state == ChatState.ChatReady) {
            ArrayList<UserRoleVO> userRoleVOS = new ArrayList<>();

            for (RequestRole requestRole : roles) {
                UserRoleVO userRoleVO = new UserRoleVO();
                userRoleVO.setUserId(requestRole.getId());
                userRoleVO.setRoles(requestRole.getRoleTypes());
                userRoleVOS.add(userRoleVO);
            }

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(gson.toJson(userRoleVOS));
            chatMessage.setSubjectId(threadId);
            chatMessage.setToken(config.getToken());
            chatMessage.setType(ChatMessageType.REMOVE_ROLE_FROM_USER);
            chatMessage.setTokenIssuer(String.valueOf(TOKEN_ISSUER));
            chatMessage.setUniqueId(uniqueId);
            if (!Util.isNullOrEmpty(config.getTypeCode())) {
                chatMessage.setTypeCode(config.getTypeCode());
            } else {
                chatMessage.setTypeCode(config.getTypeCode());
            }
            String asyncContent = gson.toJson(chatMessage);
            sendAsyncMessage(asyncContent, "REMOVE_RULE_FROM_USER");
        }
        return uniqueId;
    }

    /**
     * It blocks the thread
     *
     * @param contactId id of the contact
     * @param threadId  id of the thread
     * @param userId    id of the user
     */
    public String block(Long contactId, Long userId, Long threadId, String typeCode) {
        String uniqueId = generateUniqueId();

        if (state == ChatState.ChatReady) {

            JsonObject contentObject = new JsonObject();
            if (!Util.isNullOrEmpty(contactId)) {
                contentObject.addProperty("contactId", contactId);
            }
            if (!Util.isNullOrEmpty(userId)) {
                contentObject.addProperty("userId", userId);
            }
            if (!Util.isNullOrEmpty(threadId)) {
                contentObject.addProperty("threadId", threadId);
            }

            String json = contentObject.toString();
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(json);
            chatMessage.setToken(config.getToken());
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            chatMessage.setType(ChatMessageType.BLOCK);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", config.getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, "SEND_BLOCK");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * It blocks the thread
     * <p>
     *
     * @ param contactId id of the contact
     * @ param threadId  id of the thread
     * @ param userId    id of the user
     */

    public String block(RequestBlock request) {
        Long contactId = request.getContactId();
        long threadId = request.getThreadId();
        long userId = request.getUserId();
        String typeCode = config.getTypeCode();

        return block(contactId, userId, threadId, typeCode);
    }

    /**
     * It unblocks thread with three_way
     *
     * @param blockId   the id that came from getBlockList
     * @param userId    ID of the userId
     * @param threadId  ID of the thread
     * @param contactId ID of the contact
     */
    public String unblock(Long blockId, Long userId, Long threadId, Long contactId, String typeCode) {
        String uniqueId = generateUniqueId();

        if (state == ChatState.ChatReady) {
            ChatMessage chatMessage = new ChatMessage();

            JsonObject contentObject = new JsonObject();
            if (!Util.isNullOrEmpty(contactId)) {
                contentObject.addProperty("contactId", contactId);
            }
            if (!Util.isNullOrEmpty(userId)) {
                contentObject.addProperty("userId", userId);
            }
            if (!Util.isNullOrEmpty(threadId)) {
                contentObject.addProperty("threadId", threadId);
            }

            String jsonContent = contentObject.toString();


            chatMessage.setContent(jsonContent);
            chatMessage.setToken(config.getToken());
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            chatMessage.setType(ChatMessageType.UNBLOCK);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
            jsonObject.remove("contentCount");
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");

            if (Util.isNullOrEmpty(blockId)) {
                jsonObject.remove("subjectId");
            } else {
                jsonObject.remove("subjectId");
                jsonObject.addProperty("subjectId", blockId);
            }

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", config.getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, "SEND_UN_BLOCK");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }

        return uniqueId;
    }

    /**
     * It unblocks thread with three_way
     * <p>
     *
     * @ param blockId it can be found in the response of getBlockList
     * @ param userId ID of the user
     * @ param threadId ID of the thread
     * @ param contactId ID of the contact
     */

    public String unblock(RequestUnBlock request) {
        long contactId = request.getContactId();
        long threadId = request.getThreadId();
        long userId = request.getUserId();
        long blockId = request.getBlockId();
        String typeCode = config.getTypeCode();

        return unblock(blockId, userId, threadId, contactId, typeCode);
    }

    /**
     * It gets the list of the contacts that is on block list
     */
    public String getBlockList(Long count, Long offset, String typeCode) {
        String uniqueId = generateUniqueId();

        if (state == ChatState.ChatReady) {
            ChatMessageContent chatMessageContent = new ChatMessageContent();
            if (offset != null) {
                chatMessageContent.setOffset(offset);
            }
            if (count != null) {
                chatMessageContent.setCount(count);
            } else {
                chatMessageContent.setCount(50);
            }

            String json = gson.toJson(chatMessageContent);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(json);
            chatMessage.setType(ChatMessageType.GET_BLOCKED);
            chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            chatMessage.setToken(config.getToken());
            chatMessage.setUniqueId(uniqueId);
            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", config.getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, "SEND_BLOCK_List");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * It gets the list of the contacts that is on block list
     */

    public String getBlockList(RequestBlockList request) {
        return getBlockList(request.getCount(), request.getOffset(), config.getTypeCode());
    }


    public String getMessageDeliveredList(RequestDeliveredMessageList requestParams) {
        return deliveredMessageList(requestParams);
    }

    /**
     * Get the list of the participants that saw the specific message
     *
     * @param requestParams
     * @return seenMessageList(requestParams)
     */

    public String getMessageSeenList(RequestSeenMessageList requestParams) {
        return seenMessageList(requestParams);
    }

    /**
     * It Mutes the thread so notification is set to off for that thread
     */
    public String muteThread(long threadId, String typeCode) {
        String uniqueId = generateUniqueId();
        JsonObject jsonObject;

        try {
            if (state == ChatState.ChatReady) {

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.MUTE_THREAD);
                chatMessage.setToken(config.getToken());
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setSubjectId(threadId);


                chatMessage.setUniqueId(uniqueId);

                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");

                if (!Util.isNullOrEmpty(typeCode)) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", typeCode);
                } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", config.getTypeCode());
                } else {
                    jsonObject.remove("typeCode");
                }

                sendAsyncMessage(jsonObject.toString(), "SEND_MUTE_THREAD");

            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }

        return uniqueId;
    }

    /**
     * Mute the thread so notification is off for that thread
     */

    public String muteThread(RequestMuteThread request) {
        return muteThread(request.getThreadId(), config.getTypeCode());
    }

    /**
     * It Un mutes the thread so notification is on for that thread
     */

    public String unMuteThread(RequestMuteThread request) {
        String uniqueId = generateUniqueId();
        JsonObject jsonObject;
        try {
            if (state == ChatState.ChatReady) {
                long threadId = request.getThreadId();

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.UN_MUTE_THREAD);
                chatMessage.setToken(config.getToken());
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setSubjectId(threadId);
                chatMessage.setUniqueId(uniqueId);

                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");

                String typeCode = config.getTypeCode();

                if (!Util.isNullOrEmpty(typeCode)) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", typeCode);
                } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", config.getTypeCode());
                } else {
                    jsonObject.remove("typeCode");
                }


                sendAsyncMessage(jsonObject.toString(), "SEND_UN_MUTE_THREAD");
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueId;
    }

    /**
     * Unmute the thread so notification is on for that thread
     */
    @Deprecated
    public String unMuteThread(long threadId) {
        String uniqueId = generateUniqueId();

        try {
            if (state == ChatState.ChatReady) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.UN_MUTE_THREAD);
                chatMessage.setToken(config.getToken());
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setSubjectId(threadId);
                chatMessage.setUniqueId(uniqueId);

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                if (Util.isNullOrEmpty(config.getTypeCode())) {
                    jsonObject.remove("typeCode");
                } else {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", config.getTypeCode());
                }

                String asyncContent = jsonObject.toString();

                sendAsyncMessage(asyncContent, "SEND_UN_MUTE_THREAD");
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueId;
    }

    /**
     * It is possible to pin at most 5 thread
     *
     * @param requestPinThread
     * @return uniqueId
     */

    public String pinThread(RequestPinThread requestPinThread) {
        String uniqueId = generateUniqueId();
        JsonObject jsonObject;

        try {
            if (state == ChatState.ChatReady) {

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.PIN_THREAD);
                chatMessage.setToken(config.getToken());
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setSubjectId(requestPinThread.getThreadId());


                chatMessage.setUniqueId(uniqueId);

                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");

                String typeCode = config.getTypeCode();

                if (!Util.isNullOrEmpty(typeCode)) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", typeCode);
                } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", config.getTypeCode());
                } else {
                    jsonObject.remove("typeCode");
                }

                sendAsyncMessage(jsonObject.toString(), "SEND_PIN_THREAD");

            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }

        return uniqueId;
    }


    /**
     * Unpin the thread already pined with thread id
     *
     * @param requestPinThread
     * @return uniqueId
     */

    public String unPinThread(RequestPinThread requestPinThread) {
        String uniqueId = generateUniqueId();
        JsonObject jsonObject;
        try {
            if (state == ChatState.ChatReady) {
                long threadId = requestPinThread.getThreadId();

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.UNPIN_THREAD);
                chatMessage.setToken(config.getToken());
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setSubjectId(threadId);
                chatMessage.setUniqueId(uniqueId);

                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");

                String typeCode = config.getTypeCode();

                if (!Util.isNullOrEmpty(typeCode)) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", typeCode);
                } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", config.getTypeCode());
                } else {
                    jsonObject.remove("typeCode");
                }


                sendAsyncMessage(jsonObject.toString(), "SEND_UN_PIN_THREAD");
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueId;
    }


    public String getAdminList(RequestGetAdmin requestGetAdmin) {
        int count = (int) requestGetAdmin.getCount();
        long offset = requestGetAdmin.getOffset();
        long threadId = requestGetAdmin.getThreadId();
        String typeCode = config.getTypeCode();

        return getThreadParticipantsMain(count, offset, threadId, typeCode, true);
    }

    /**
     * If someone that is not in your contacts send message to you
     * their spam is false
     */
    @Deprecated
    public String spam(long threadId) {
        String uniqueId = generateUniqueId();

        if (state == ChatState.ChatReady) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessageType.SPAM_PV_THREAD);
            chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            chatMessage.setToken(config.getToken());
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setSubjectId(threadId);

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

            if (Util.isNullOrEmpty(config.getTypeCode())) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", config.getTypeCode());
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, "SEND_REPORT_SPAM");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

    /**
     * If someone that is not in your contact list tries to send message to you
     * their spam value is true,so you can call this method in order to set that to false
     * <p>
     *
     * @ param long threadId ID of the thread
     */

    public String spam(RequestSpam request) {
        String uniqueId = generateUniqueId();

        JsonObject jsonObject;
        try {

            if (state == ChatState.ChatReady) {
                long threadId = request.getThreadId();
                String typeCode = config.getTypeCode();

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.SPAM_PV_THREAD);
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setToken(config.getToken());
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setSubjectId(threadId);

                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
                jsonObject.remove("contentCount");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("metadata");
                jsonObject.remove("repliedTo");

                if (Util.isNullOrEmpty(typeCode)) {
                    if (Util.isNullOrEmpty(config.getTypeCode())) {
                        jsonObject.remove("typeCode");
                    } else {
                        jsonObject.addProperty("typeCode", config.getTypeCode());
                    }
                } else {
                    jsonObject.addProperty("typeCode", config.getTypeCode());
                }

                sendAsyncMessage(jsonObject.toString(), "SEND_REPORT_SPAM");
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueId;
    }


    public String interactMessage(RequestInteract request) {
        String uniqueId = generateUniqueId();

        try {
            if (state == ChatState.ChatReady) {
                ChatMessage chatMessage = new ChatMessage();

                chatMessage.setType(ChatMessageType.INTERACT_MESSAGE);
                chatMessage.setToken(config.getToken());
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setContent(request.getContent());
                chatMessage.setSystemMetadata(request.getSystemMetadata());
                chatMessage.setMetadata(request.getMetadata());
                chatMessage.setRepliedTo(request.getRepliedTo());
                chatMessage.setSubjectId(request.getMessageId());


                if (!Util.isNullOrEmpty(config.getTypeCode())) {
                    chatMessage.setTypeCode(config.getTypeCode());
                } else {
                    chatMessage.setTypeCode(config.getTypeCode());
                }

                sendAsyncMessage(gson.toJson(chatMessage), "SEND_INTERACT_MESSAGE");

            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }

        return uniqueId;
    }


    public ArrayList<String> createThreadWithFileMessage(RequestCreateThreadWithFile request) {
        ArrayList<String> uniqueIds = new ArrayList<>();

        String threadUniqId = generateUniqueId();
        uniqueIds.add(threadUniqId);

        System.out.println(threadUniqId);

        String newMsgUniqueId = generateMessageUniqueId(request, uniqueIds);

        System.out.println(newMsgUniqueId);

        List<String> forwardUniqueIds = generateForwardingMessageId(request, uniqueIds);

        if (state == ChatState.ChatReady) {
            if (request.getFile() != null && request.getFile() instanceof RequestUploadImage) {

                uploadImage((RequestUploadImage) request.getFile(), threadUniqId, metaData -> {
                    RequestThreadInnerMessage requestThreadInnerMessage;

                    if (request.getMessage() == null) {
                        requestThreadInnerMessage = new RequestThreadInnerMessage
                                .Builder()
                                .metadata(metaData)
                                .build();
                    } else {
                        requestThreadInnerMessage = request.getMessage();
                        requestThreadInnerMessage.setMetadata(metaData);
                    }


                    request.setMessage(requestThreadInnerMessage);
                    request.setFile(null);

                    createThreadWithMessage(request, threadUniqId, newMsgUniqueId, forwardUniqueIds);

                });

            } else if (request.getFile() != null) {

                uploadFile(request.getFile().getFilePath(), threadUniqId, metaData -> {
                    RequestThreadInnerMessage requestThreadInnerMessage;


                    if (request.getMessage() == null) {
                        requestThreadInnerMessage = new RequestThreadInnerMessage
                                .Builder()
                                .metadata(metaData)
                                .build();
                    } else {
                        requestThreadInnerMessage = request.getMessage();
                        requestThreadInnerMessage.setMetadata(metaData);
                    }


                    request.setMessage(requestThreadInnerMessage);
                    request.setFile(null);

                    createThreadWithMessage(request, threadUniqId, newMsgUniqueId, forwardUniqueIds);

                });
            }

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, threadUniqId);
        }

        return uniqueIds;
    }

    private List<String> generateForwardingMessageId(RequestCreateThreadWithMessage request, ArrayList<String> uniqueIds) {
        List<String> forwardUniqueIds = null;

        if (request.getMessage() != null && !Util.isNullOrEmptyNumber(request.getMessage().getForwardedMessageIds())) {
            List<Long> messageIds = request.getMessage().getForwardedMessageIds();
            forwardUniqueIds = new ArrayList<>();

            for (long ids : messageIds) {
                String frwMsgUniqueId = generateUniqueId();

                System.out.println(frwMsgUniqueId);

                forwardUniqueIds.add(frwMsgUniqueId);
                uniqueIds.add(frwMsgUniqueId);
            }
        }
        return forwardUniqueIds;
    }

    private String generateMessageUniqueId(RequestCreateThreadWithMessage request, ArrayList<String> uniqueIds) {
        String newMsgUniqueId = null;
        if (request.getMessage() != null && !Util.isNullOrEmpty(request.getMessage().getText())) {
            newMsgUniqueId = generateUniqueId();
            uniqueIds.add(newMsgUniqueId);
        }
        return newMsgUniqueId;
    }

    private void createThreadWithMessage(RequestCreateThreadWithMessage threadRequest,
                                         String threadUniqueId,
                                         String messageUniqueId,
                                         List<String> forwardUniqueIds) {
        JsonObject innerMessageObj = null;
        JsonObject jsonObject;

        try {
            if (state == ChatState.ChatReady) {

                if (threadRequest.getMessage() != null) {
                    RequestThreadInnerMessage innerMessage = threadRequest.getMessage();
                    innerMessageObj = (JsonObject) gson.toJsonTree(innerMessage);

                    if (Util.isNullOrEmpty(threadRequest.getMessage().getType())) {
                        innerMessageObj.remove("type");
                    }

                    if (Util.isNullOrEmpty(threadRequest.getMessage().getText())) {
                        innerMessageObj.remove("message");
                    } else {
                        innerMessageObj.addProperty("uniqueId", messageUniqueId);
                    }

                    if (!Util.isNullOrEmptyNumber(threadRequest.getMessage().getForwardedMessageIds())) {

                        /* Its generated new unique id for each forward message*/
                        JsonElement element = gson.toJsonTree(forwardUniqueIds, new TypeToken<List<Long>>() {
                        }.getType());

                        JsonArray jsonArray = element.getAsJsonArray();
                        innerMessageObj.add("forwardedUniqueIds", jsonArray);
                    } else {
                        innerMessageObj.remove("forwardedUniqueIds");
                        innerMessageObj.remove("forwardedMessageIds");
                    }
                }

                JsonObject jsonObjectCreateThread = (JsonObject) gson.toJsonTree(threadRequest);

                jsonObjectCreateThread.remove("count");
                jsonObjectCreateThread.remove("offset");
                jsonObjectCreateThread.add("message", innerMessageObj);

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setContent(jsonObjectCreateThread.toString());
                chatMessage.setType(ChatMessageType.INVITATION);
                chatMessage.setUniqueId(threadUniqueId);
                chatMessage.setToken(config.getToken());
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));


                jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                jsonObject.remove("repliedTo");
                jsonObject.remove("subjectId");
                jsonObject.remove("systemMetadata");
                jsonObject.remove("contentCount");

                String typeCode = config.getTypeCode();

                if (!Util.isNullOrEmpty(typeCode)) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", typeCode);
                } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", config.getTypeCode());
                } else {
                    jsonObject.remove("typeCode");
                }

                sendAsyncMessage(jsonObject.toString(), "SEND_CREATE_THREAD_WITH_MESSAGE");
            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, threadUniqueId);
            }

        } catch (Throwable e) {
            showErrorLog(e.getCause().getMessage());
        }
    }


    private void uploadFile(String path, String finalUniqueId, UploadFileListener uploadFileListener) {
        try {
            if (config.getFileServer() != null && !Util.isNullOrEmpty(path)) {

                File file = new File(path);
                String mimeType = getContentType(file);

                if (file.exists()) {

                    long fileSize = file.length();
                    FileApi fileApi = RetrofitHelperFileServer.getInstance(config.getFileServer()).create(FileApi.class);

                    RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                    RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);

                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                    Call<FileUpload> fileUploadCall = fileApi.sendFile(body, config.getToken(), TOKEN_ISSUER, name);

                    RetrofitUtil.request(fileUploadCall, new ApiListener<FileUpload>() {

                        @Override
                        public void onSuccess(FileUpload fileUpload) {
                            boolean hasError = fileUpload.isHasError();
                            if (hasError) {
                                String errorMessage = fileUpload.getMessage();
                                int errorCode = fileUpload.getErrorCode();
                                String jsonError = getErrorOutPut(errorMessage, errorCode, finalUniqueId);
                                showErrorLog(jsonError);
                            } else {
                                ResultFile resultFile = fileUpload.getResult();

                                showInfoLog("RECEIVE_UPLOAD_FILE", gson.toJson(resultFile));

                                MetaDataFile metaDataFile = new MetaDataFile();
                                FileMetaDataContent metaDataContent = new FileMetaDataContent();
                                metaDataContent.setHashCode(resultFile.getHashCode());
                                metaDataContent.setId(resultFile.getId());
                                metaDataFile.setFile(metaDataContent);

                                uploadFileListener.fileUploaded(gson.toJson(metaDataFile));
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            showErrorLog(throwable.getMessage());
                        }

                        @Override
                        public void onServerError(Response<FileUpload> response) {
                            if (response.body() != null) {
                                showErrorLog(response.body().getMessage());
                            }
                        }
                    });
                } else {
                    String jsonError = getErrorOutPut(ChatConstant.ERROR_NOT_IMAGE, ChatConstant.ERROR_CODE_NOT_IMAGE, null);
                    showErrorLog(jsonError);
                }
            } else {
                showErrorLog("File is not Exist");
            }
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
        }
    }


    private void uploadImage(RequestUploadImage requestUploadImage, String finalUniqueId, UploadFileListener uploadFileListener) {

        String filePath = requestUploadImage.getFilePath();
        int xC = requestUploadImage.getxC();
        int yC = requestUploadImage.getyC();
        int hC = requestUploadImage.gethC();
        int wC = requestUploadImage.getwC();

        if (filePath.endsWith(".gif")) {
            uploadFile(filePath, finalUniqueId, uploadFileListener);
        } else {
            uploadImage(filePath, xC, yC, hC, wC, finalUniqueId, uploadFileListener);
        }

    }

    public void uploadImage(String filePath,
                            int xC,
                            int yC,
                            int hC,
                            int wC,
                            String finalUniqueId,
                            UploadFileListener uploadFileListener) {

        try {
            if (config.getFileServer() != null && filePath != null && !filePath.isEmpty()) {
                File file = new File(filePath);

                if (file.exists()) {

                    String mimeType = getContentType(file);

                    if (mimeType.equals("image/png") || mimeType.equals("image/jpeg")) {
                        FileApi fileApi;
                        RequestBody requestFile;

                        if (!Util.isNullOrEmpty(hC) && !Util.isNullOrEmpty(wC)) {
                            BufferedImage originalImage = ImageIO.read(file);

                            BufferedImage subImage = originalImage.getSubimage(xC, yC, wC, hC);

                            File outputFile = File.createTempFile("test", null);

                            ImageIO.write(subImage, mimeType.substring(mimeType.indexOf("/") + 1), outputFile);

                            fileApi = RetrofitHelperFileServer.getInstance(config.getFileServer()).create(FileApi.class);

                            requestFile = RequestBody.create(MediaType.parse("image/*"), outputFile);
                        } else {
                            fileApi = RetrofitHelperFileServer.getInstance(config.getFileServer()).create(FileApi.class);

                            requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                        }

                        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());

                        Call<FileImageUpload> fileImageUploadCall = fileApi.sendImageFile(body, config.getToken(), TOKEN_ISSUER, name);

                        RetrofitUtil.request(fileImageUploadCall, new ApiListener<FileImageUpload>() {

                            @Override
                            public void onSuccess(FileImageUpload fileImageUpload) {
                                boolean hasError = fileImageUpload.isHasError();

                                if (hasError) {
                                    String errorMessage = fileImageUpload.getMessage();
                                    int errorCode = fileImageUpload.getErrorCode();
                                    String jsonError = getErrorOutPut(errorMessage, errorCode, finalUniqueId);

                                    showErrorLog(jsonError);
                                } else {
                                    ResultImageFile resultImageFile = new ResultImageFile();

                                    resultImageFile.setId(fileImageUpload.getResult().getId());
                                    resultImageFile.setHashCode(fileImageUpload.getResult().getHashCode());
                                    resultImageFile.setName(fileImageUpload.getResult().getName());
                                    resultImageFile.setHeight(fileImageUpload.getResult().getHeight());
                                    resultImageFile.setWidth(fileImageUpload.getResult().getWidth());
                                    resultImageFile.setActualHeight(fileImageUpload.getResult().getActualHeight());
                                    resultImageFile.setActualWidth(fileImageUpload.getResult().getActualWidth());

                                    showInfoLog("RECEIVE_UPLOAD_IMAGE", gson.toJson(fileImageUpload));

                                    uploadFileListener.fileUploaded(gson.toJson(resultImageFile));
                                }
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                showErrorLog(throwable.getMessage());
                            }

                            @Override
                            public void onServerError(Response<FileImageUpload> response) {
                                if (response.body() != null) {
                                    showErrorLog(response.body().getMessage());
                                }
                            }
                        });
                    } else {
                        String jsonError = getErrorOutPut(ChatConstant.ERROR_NOT_IMAGE, ChatConstant.ERROR_CODE_NOT_IMAGE, null);
                        showErrorLog(jsonError);
                    }
                }
            } else {
                showErrorLog("FileServer url Is null");
            }
        } catch (Exception e) {
            showErrorLog(e.getCause().getMessage());
            getErrorOutPut(ChatConstant.ERROR_UPLOAD_FILE, ChatConstant.ERROR_CODE_UPLOAD_FILE, finalUniqueId);
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

    /**
     * Ping for staying chat alive
     */
    private void ping() {
        if (state == ChatState.ChatReady) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessageType.PING);
            chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
            chatMessage.setToken(config.getToken());

            String asyncContent = gson.toJson(chatMessage);

            sendAsyncMessage(asyncContent, "CHAT PING");
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
                case ChatMessageType.INVITATION:
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

    /**
     * order    If order is empty [default = desc] and also you have two option [ asc | desc ]
     * order should be set with lower case
     */
    private void getHistoryMain(History history, long threadId, String uniqueId, String typeCode) {
        long count = history.getCount() > 0 ? history.getCount() : 50;
        long offsets = history.getOffset() > 0 ? history.getOffset() : 0;
        long fromTime = history.getFromTime();
        long fromTimeNanos = history.getFromTimeNanos();
        long toTime = history.getToTime();
        long toTimeNanos = history.getToTimeNanos();


        history.setCount(count);
        history.setOffset(offsets);
        String query = history.getQuery();

        JsonObject jObj = (JsonObject) gson.toJsonTree(history);
        if (history.getLastMessageId() == 0) {
            jObj.remove("lastMessageId");
        }

        if (history.getFirstMessageId() == 0) {
            jObj.remove("firstMessageId");
        }

        if (history.getId() <= 0) {
            jObj.remove("id");
        }

        if (Util.isNullOrEmpty(query)) {
            jObj.remove("query");
        }

        if (Util.isNullOrEmpty(fromTime)) {
            jObj.remove("fromTime");
        }

        if (Util.isNullOrEmpty(fromTimeNanos)) {
            jObj.remove("fromTimeNanos");
        }

        if (Util.isNullOrEmpty(toTime)) {
            jObj.remove("toTime");
        }

        if (Util.isNullOrEmpty(toTimeNanos)) {
            jObj.remove("toTimeNanos");
        }

        if (Util.isNullOrEmpty(history.getUniqueIds())) {
            jObj.remove("uniqueIds");
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(jObj.toString());
        chatMessage.setType(ChatMessageType.GET_HISTORY);
        chatMessage.setToken(config.getToken());
        chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setSubjectId(threadId);

        JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

        if (!Util.isNullOrEmpty(typeCode)) {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", typeCode);
        } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", config.getTypeCode());
        } else {
            jsonObject.remove("typeCode");
        }


        String asyncContent = jsonObject.toString();

        sendAsyncMessage(asyncContent, "SEND GET THREAD HISTORY");
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


    private void signalMessage(RequestSignalMsg requestSignalMsg) {

        String uniqueId = generateUniqueId();
        int signalType = requestSignalMsg.getSignalType();
        long threadId = requestSignalMsg.getThreadId();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", signalType);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(jsonObject.toString());
        chatMessage.setType(ChatMessageType.SIGNAL_MESSAGE);
        chatMessage.setToken(config.getToken());
        chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setSubjectId(threadId);

        JsonObject jsonObjectCht = (JsonObject) gson.toJsonTree(chatMessage);

        if (Util.isNullOrEmpty(config.getTypeCode())) {
            jsonObjectCht.remove("typeCode");
        } else {
            jsonObjectCht.remove("typeCode");
            jsonObjectCht.addProperty("typeCode", config.getTypeCode());
        }
        String asyncContent = jsonObjectCht.toString();
        sendAsyncMessage(asyncContent, "SEND SIGNAL_TYPE " + signalType);
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

    private String getContactMain(Integer count, Long offset, String typeCode) {
        String uniqueId = generateUniqueId();

        count = count != null && count > 0 ? count : 50;
        offset = offset != null && offset >= 0 ? offset : 0;

        if (state == ChatState.ChatReady) {

            ChatMessageContent chatMessageContent = new ChatMessageContent();

            chatMessageContent.setOffset(offset);

            JsonObject jObj = (JsonObject) gson.toJsonTree(chatMessageContent);
            jObj.remove("lastMessageId");
            jObj.remove("firstMessageId");
            jObj.remove("new");

            jObj.remove("count");
            jObj.addProperty("size", count);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(jObj.toString());
            chatMessage.setType(ChatMessageType.GET_CONTACTS);
            chatMessage.setToken(config.getToken());
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));

            JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);
            jsonObject.remove("contentCount");
            jsonObject.remove("systemMetadata");
            jsonObject.remove("metadata");
            jsonObject.remove("repliedTo");
            jsonObject.remove("subjectId");

            if (!Util.isNullOrEmpty(typeCode)) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typeCode);
            } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", config.getTypeCode());
            } else {
                jsonObject.remove("typeCode");
            }

            String asyncContent = jsonObject.toString();

            sendAsyncMessage(asyncContent, "GET_CONTACT_SEND");

        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
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
    private String deliveredMessageList(RequestDeliveredMessageList requestParams) {
        String uniqueId = generateUniqueId();

        try {
            if (state == ChatState.ChatReady) {
                if (Util.isNullOrEmpty(requestParams.getCount())) {
                    requestParams.setCount(50);
                }

                JsonObject object = (JsonObject) gson.toJsonTree(requestParams);
                object.remove("typeCode");

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setToken(config.getToken());
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setType(ChatMessageType.DELIVERED_MESSAGE_LIST);

                chatMessage.setContent(object.toString());

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                String typeCode = config.getTypeCode();

                if (!Util.isNullOrEmpty(typeCode)) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", typeCode);
                } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", config.getTypeCode());
                } else {
                    jsonObject.remove("typeCode");
                }

                String asyncContent = jsonObject.toString();

                sendAsyncMessage(asyncContent, "SEND_DELIVERED_MESSAGE_LIST");

            } else {
                getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
            }

        } catch (Throwable e) {
            showErrorLog(e.getCause().getMessage());
        }
        return uniqueId;
    }

    /**
     * The replacement method is getMessageSeenList.
     */
    private String seenMessageList(RequestSeenMessageList requestParams) {
        String uniqueId = generateUniqueId();

        if (state == ChatState.ChatReady) {
            try {

                if (Util.isNullOrEmpty(requestParams.getCount())) {
                    requestParams.setCount(50);
                }

                JsonObject object = (JsonObject) gson.toJsonTree(requestParams);
                object.remove("typeCode");

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessageType.SEEN_MESSAGE_LIST);
                chatMessage.setTokenIssuer(Integer.toString(TOKEN_ISSUER));
                chatMessage.setToken(config.getToken());
                chatMessage.setUniqueId(uniqueId);
                chatMessage.setContent(object.toString());

                JsonObject jsonObject = (JsonObject) gson.toJsonTree(chatMessage);

                String typeCode = config.getTypeCode();

                if (!Util.isNullOrEmpty(typeCode)) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", typeCode);
                } else if (!Util.isNullOrEmpty(config.getTypeCode())) {
                    jsonObject.remove("typeCode");
                    jsonObject.addProperty("typeCode", config.getTypeCode());
                } else {
                    jsonObject.remove("typeCode");
                }

                String asyncContent = jsonObject.toString();

                sendAsyncMessage(asyncContent, "SEND_SEEN_MESSAGE_LIST");

            } catch (Throwable e) {
                showErrorLog(e.getCause().getMessage());
            }
        } else {
            getErrorOutPut(ChatConstant.ERROR_CHAT_READY, ChatConstant.ERROR_CODE_CHAT_READY, uniqueId);
        }
        return uniqueId;
    }

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
        chatMessage.setType(ChatMessageType.INVITATION);
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
        sendAsyncMessage2(requestThread);
        return requestThread.getUniqueId();
    }

    public String getHistory2(GetHistoryRequest request) {
        sendAsyncMessage2(request);
        return request.getUniqueId();
    }

    public String sendTextMessage2(SendMessageRequest request) {
        sendAsyncMessage2(request);
        return request.getUniqueId();
    }

    public String muteThread2(GeneralRequest request) {
        sendAsyncMessage2(request);
        return request.getUniqueId();
    }

    public String unMuteThread2(GeneralRequest request) {
        sendAsyncMessage2(request);
        return request.getUniqueId();
    }

    public String pinThread2(GeneralRequest request) {
        sendAsyncMessage2(request);
        return request.getUniqueId();
    }

    public String unPinThread2(GeneralRequest request) {
        sendAsyncMessage2(request);
        return request.getUniqueId();
    }

    public String getThreadParticipants2(ThreadParticipantRequest request) {
        sendAsyncMessage2(request);
        return request.getUniqueId();
    }

    public String getUserInfo2(GeneralRequest request) {
        sendAsyncMessage2(request);
        return request.getUniqueId();
    }

    public String editMessage2(EditMessageRequest request) {
        sendAsyncMessage2(request);
        return request.getUniqueId();
    }

    public String addAdmin2(SetAdminRequest request) {
        sendAsyncMessage2(request);
        return request.getUniqueId();
    }

    public String block2(BlockRequest request) {
        sendAsyncMessage2(request);
        return request.getUniqueId();
    }

    public String unBlock2(UnBlockRequest request) {
        sendAsyncMessage2(request);
        return request.getUniqueId();
    }

    public String deleteMessage2(DeleteMessageRequest request) {
        sendAsyncMessage2(request);
        return request.getUniqueId();
    }

    public String addParticipants2(AddParticipantsRequest request) {
        sendAsyncMessage2(request);
        return request.getUniqueId();
    }

    public String removeParticipants2(RemoveParticipantsRequest request) {
        sendAsyncMessage2(request);
        return request.getUniqueId();
    }

    public String closeThread(GeneralRequest request) {
        sendAsyncMessage2(request);
        return request.getUniqueId();
    }

    public String leaveThread2(LeaveThreadRequest request) {
        sendAsyncMessage2(request);
        return request.getUniqueId();
    }

    public String clearHistory2(ClearHistoryRequest request) {
        sendAsyncMessage2(request);
        return request.getUniqueId();
    }

    private void sendAsyncMessage2(BaseRequest request) {
        if (state == ChatState.ChatReady) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setToken(config.getToken());
            chatMessage.setUniqueId(request.getUniqueId());
            chatMessage.setType(request.getChatMessageType());// ping , getThread , getHistory , ...
            chatMessage.setContent(request.getChatMessageContent());
            chatMessage.setSubjectId(request.getSubjectId());
            chatMessage.setTypeCode(config.getTypeCode());   // we should send this everywhere but not correct complete
            chatMessage.setMessageType(1); // video , text , picture , ...    //we must do something about this for not send in everywhere
            async.sendMessage(gson.toJson(chatMessage), Message, null);
        }
    }
}
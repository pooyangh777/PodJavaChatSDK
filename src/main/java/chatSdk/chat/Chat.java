package chatSdk.chat;

import asyncSdk.Async;
import asyncSdk.AsyncListener;
import asyncSdk.model.AsyncMessage;
import chatSdk.chat.chatInterface.ChatInterface;
import chatSdk.dataTransferObject.GeneralRequest;
import chatSdk.dataTransferObject.chat.*;
import asyncSdk.model.AsyncState;
import chatSdk.dataTransferObject.message.inPut.*;
import chatSdk.dataTransferObject.contacts.outPut.*;
import chatSdk.dataTransferObject.message.outPut.*;
import chatSdk.dataTransferObject.system.outPut.*;
import chatSdk.dataTransferObject.thread.outPut.*;
import chatSdk.dataTransferObject.user.inPut.*;
import chatSdk.dataTransferObject.user.outPut.*;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static asyncSdk.model.AsyncMessageType.Message;
import static chatSdk.dataTransferObject.chat.ChatMessageType.USER_INFO;

/**
 * Created By Khojasteh on 7/29/2019
 */
public class Chat implements AsyncListener, ChatInterface {
    private static final Logger logger = LogManager.getLogger(Chat.class);
    private static Async async;
    @Getter
    private static Chat instance;
    private final ChatListener listener;
    private UserInfo user;
    private long lastSentMessageTime;
    private ChatState state;
    public ChatConfig config;
    private int reconnectCount = 0;
    private Timer reconnectTimer;

    private final OnReceiveMessageFactory responseHandlers = new OnReceiveMessageFactory();

    private Chat(ChatConfig chatConfig, ChatListener listener) {
        this.config = chatConfig;
        this.listener = listener;
    }

    public synchronized static Chat init(ChatConfig chatConfig, ChatListener listener) {
        if (instance == null) {
            async = new Async(chatConfig.getAsyncConfig());
            instance = new Chat(chatConfig, listener);
            instance.responseHandlers.listener = listener;
        }
        return instance;
    }

    @Override
    public void onReceivedMessage(AsyncMessage asyncMessage) {
        responseHandlers.onReceivedMessage(asyncMessage);
    }

    @Override
    public void onError(Exception exception) {
    }

    @Override
    public void onStateChanged(AsyncState state, Async async) {
        switch (state) {
            case AsyncReady:
                if (user == null) {
                    internalGetUserInfo();
                } else {
                    this.state = ChatState.ChatReady;
                    stopAsyncReconnect();
                }
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
                checkAsyncIsConnected();
                break;
        }
        listener.onChatState(this.state);
    }

    void setState(ChatState state) {
        this.state = state;
    }

    public void connect() throws Exception {
        try {
            async.setListener(this);
            async.connect();
        } catch (Throwable throwable) {
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

    private void checkAsyncIsConnected() {
        lastSentMessageTime = new Date().getTime();
        reconnectTimer = new Timer();
        reconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                reconnectAsync();
            }
        }, 0, config.getReconnectInterval());
    }

    private void reconnectAsync() {
        if (async.getState() == AsyncState.Closed && reconnectCount < config.getMaxReconnectCount()) {
            try {
                logger.info("Reconnecting " + reconnectCount  + " of " + config.getMaxReconnectCount());
                reconnectCount++;
                async.connect();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    void stopAsyncReconnect() {
        reconnectCount = config.getMaxReconnectCount();
        if (reconnectTimer != null) {
            reconnectTimer.cancel();
        }
    }

    public UserInfo getUser() {
        return user;
    }

    void setUser(UserInfo user) {
        this.user = user;
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

    public String batchDeleteMessage(BatchDeleteMessageRequest request) {
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

    public String unPinMessage(UnpinMessageRequest request) {
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

//    public String getTagParticipants(GetTagParticipantsRequest request) {
//        sendAsyncMessage(request);
//        return request.getUniqueId();
//    }

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

    public String spam(GeneralRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String mention(MentionRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String deleteMessage(DeleteMessageRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String signalMessage(SignalMessageRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    public String currentUserRoles(GeneralRequest request) {
        sendAsyncMessage(request);
        return request.getUniqueId();
    }

    void internalGetUserInfo() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setToken(config.getToken());
        chatMessage.setUniqueId(UUID.randomUUID().toString());
        chatMessage.setType(USER_INFO);
        chatMessage.setTypeCode(config.getTypeCode());
        async.sendMessage(GsonFactory.gson.toJson(chatMessage), Message, null);
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
            String json = GsonFactory.gson.toJson(chatMessage);
            async.sendMessage(json, Message, null);
            logger.info("CHAT_SDK Send With type " + request.getChatMessageType() + ": \n" + json  + "\n");
        }
    }
}
package chatSdk.chat;

import asyncSdk.Async;
import asyncSdk.AsyncListener;
import asyncSdk.model.AsyncMessage;
import asyncSdk.model.Message;
import chatSdk.chat.chatInterface.ChatInterface;
import chatSdk.dataTransferObject.GeneralRequest;
import chatSdk.dataTransferObject.chat.*;
import asyncSdk.model.AsyncState;
import chatSdk.dataTransferObject.contacts.inPut.Contact;
import chatSdk.dataTransferObject.contacts.inPut.Contacts;
import chatSdk.dataTransferObject.message.inPut.*;
import chatSdk.dataTransferObject.contacts.outPut.*;
import chatSdk.dataTransferObject.message.outPut.*;
import chatSdk.dataTransferObject.system.outPut.*;
import chatSdk.dataTransferObject.thread.outPut.*;
import chatSdk.dataTransferObject.user.inPut.*;
import chatSdk.dataTransferObject.user.outPut.*;
import chatSdk.networking.api.ContactApi;
import chatSdk.networking.retrofithelper.ChatResponse;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static asyncSdk.model.AsyncMessageType.Message;
import static chatSdk.chat.GsonFactory.gson;

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
                logger.info("Reconnecting " + reconnectCount + " of " + config.getMaxReconnectCount());
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

    public String addContact(AddContactRequest request) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.pod.ir/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ContactApi apiService = retrofit.create(ContactApi.class);

        Call<Contacts> call = apiService.addContact(config.getToken(), 1, request.getFirstName(), request.getLastName(), request.getEmail(), request.getCellphoneNumber(), request.getUniqueId());

        call.enqueue(new Callback<Contacts>() {
            @Override
            public void onResponse(Call<Contacts> call, Response<Contacts> response) {

            }

            @Override
            public void onFailure(Call<Contacts> call, Throwable throwable) {

            }


        });

        return request.getUniqueId();
    }

    public void addContacts(List<AddContactRequest> request) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.pod.ir/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Map<String, List<AddContactRequest>> contactLists = new HashMap<>();
        contactLists.put("contactList", request);
        ContactApi contactApi = retrofit.create(ContactApi.class);
        Call<JsonObject> call = contactApi.addContacts(config.getToken(), "1", contactLists);
//        Call<JsonObject> call = contactApi.addContacts(config.getToken(), "1", request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    // extract relevant information from the JSON object here
                } else {
                    // handle error
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {

            }
        });


//        OkHttpClient httpClient = new OkHttpClient.Builder()
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public okhttp3.Response intercept(Chain chain) throws IOException {
//                        Request original = chain.request();
//                        Request request = original.newBuilder()
//                                .header("_token_", config.getToken())
//                                .header("_token_issuer_", "1")
//                                .method(original.method(), original.body())
//                                .build();
//
//                        return chain.proceed(request);
//                    }
//                })
//                .build();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://api.pod.ir/")
//                .client(httpClient)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();
//
//        ContactApi contactApi = retrofit.create(ContactApi.class);
//        Call<ChatResponse<ContactResponse>> call = contactApi.addContacts(request);
//        call.enqueue(new Callback<ChatResponse<ContactResponse>>() {
//            @Override
//            public void onResponse(Call<ChatResponse<ContactResponse>> call, retrofit2.Response<ChatResponse<ContactResponse>> response) {
//                if (response.isSuccessful()) {
//                    ChatResponse<ContactResponse> chatResponse = response.body();
//
//                } else {
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ChatResponse<ContactResponse>> call, Throwable t) {
//
//            }
//        });

    }

//        OkHttpClient httpClient = new OkHttpClient.Builder()
//                .addInterceptor(chain -> {
//                    Request original = chain.request();
//                    Request requestWithHeaders = original.newBuilder()
//                            .header("_token_", config.getToken())
//                            .header("_token_issuer_", "1")
//                            .method(original.method(), original.body())
//                            .build();
//                    return chain.proceed(requestWithHeaders);
//                })
//                .build();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://api.pod.ir/")
//                .client(httpClient)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        ContactApi contactApi = retrofit.create(ContactApi.class);
//        Call<ChatResponse<ContactResponse>> call = contactApi.addContacts(request);
//
//        call.enqueue(new Callback<ChatResponse<ContactResponse>>() {
//            @Override
//            public void onResponse(Call<ChatResponse<ContactResponse>> call, retrofit2.Response<ChatResponse<ContactResponse>> response) {
//                if (response.isSuccessful()) {
//                    ChatResponse<ContactResponse> chatResponse = response.body();
//
//                } else {
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ChatResponse<ContactResponse>> call, Throwable t) {
//            }
//        });


    @Getter
    @Setter
    public static class ContactResponse {
        List<Contact> contacts;
    }
//        OkHttpClient okHttpClient = new OkHttpClient.
//                Builder().
//                addInterceptor(new Interceptor() {
//                    @Override
//                    public okhttp3.Response intercept(Chain chain) throws IOException {
//                        Request request = chain.request();
//                        HttpUrl url = request.url().newBuilder().build();
//                        System.out.println("Request URL: " + url.toString());
//                        return chain.proceed(request);
//                    }
//                })
//                .build();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://api.pod.ir/")
//                .client(new OkHttpClient())
//                .build();
//
//        ContactApi apiService = retrofit.create(ContactApi.class);
//        Map queryParameters = new HashMap<>();
//        queryParameters.put("firstName[0]", "Ali");
//        queryParameters.put("lastName[0]", "ali");
//        queryParameters.put("cellphoneNumber[0]", "555-1234");
//        queryParameters.put("uniqueId[0]", "12343444fdgd");
//        queryParameters.put("email[0]", "ali@example.com");
//        queryParameters.put("firstName[1]", "Sam");
//        queryParameters.put("lastName[1]", "sam");
//        queryParameters.put("cellphoneNumber[1]", "555-5678");
//        queryParameters.put("email[1]", "reza@example.com");
//        queryParameters.put("uniqueId[1]", "kdhsafdfdgd");
//        AddContactRequest requestBody = new AddContactRequest();
//        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
//        RequestBody requestBody = RequestBody.create(mediaType, "");
//
//        Call call = apiService.addContacts(
//                queryParameters,
//                "2a5f7037feb844c08f8106b1cccd3bc7.XzIwMjM2",
//                "1",
//                "application/x-www-form-urlencoded",
//                requestBody
//        );
//
//        Response response = call.execute();
//    }

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://api.pod.ir/")
//                .client(okHttpClient)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        ContactApi apiService = retrofit.create(ContactApi.class);
//        Map<String, String> fields = new HashMap<>();
//        for (int i = 0; i < requests.size(); i++) {
//            AddContactRequest request = requests.get(i);
//            String index = String.valueOf(i);
//            fields.put("contacts[" + index + "][firstName]", request.getFirstName());
//            fields.put("contacts[" + index + "][lastName]", request.getLastName());
//            fields.put("contacts[" + index + "][cellphoneNumber]", request.getCellphoneNumber());
//            fields.put("contacts[" + index + "][email]", request.getEmail());
//            fields.put("contacts[" + index + "][uniqueId]", request.getUniqueId());
//        }
//        // Convert the fields map to x-www-form-urlencoded format
//        String requestBody = getRequestBody(fields);
//        RequestBody request = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), requestBody);
//        Call<Response<Contacts>> call = apiService.addContacts(config.getToken(), 1, request);
//        try {
//            Response<Response<Contacts>> response = call.execute();
//            if (response != null && response.isSuccessful()) {
//                // Handle successful response
//            } else {
//                // Handle unsuccessful response
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            // Handle exception
//        }
//        return requests.get(0).getUniqueId();
//    }
//
//    private String getRequestBody(Map<String, String> fields) {
//        StringBuilder builder = new StringBuilder();
//        for (Map.Entry<String, String> entry : fields.entrySet()) {
//            if (builder.length() > 0) {
//                builder.append("&");
//            }
//            String key = entry.getKey();
//            String value = entry.getValue();
//            builder.append(key);
//            builder.append("=");
//            try {
//                builder.append(URLEncoder.encode(value, String.valueOf(StandardCharsets.UTF_8)));
//            } catch (UnsupportedEncodingException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return builder.toString();
//    }
//        Map<String, String> fields = new HashMap<>();
//        for (int i = 0; i < requests.size(); i++) {
//            AddContactRequest request = requests.get(i);
//            String index = String.valueOf(i);
//            fields.put("contacts[" + index + "][firstName]", request.getFirstName());
//            fields.put("contacts[" + index + "][lastName]", request.getLastName());
//            fields.put("contacts[" + index + "][cellphoneNumber]", request.getCellphoneNumber());
//            fields.put("contacts[" + index + "][email]", request.getEmail());
//            fields.put("contacts[" + index + "][uniqueId]", request.getUniqueId());
//        }
//        Call<Response<Contacts>> call = apiService.addContacts(config.getToken(), 1, fields);
//        try {
//            Response<Response<Contacts>> response = call.execute();
//            if (response != null && response.isSuccessful()) {
//                // Handle successful response
//            } else {
//                // Handle unsuccessful response
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            // Handle exception
//        }
//       return requests.get(0).getUniqueId();
//    }
//        ArrayList<String> firstNames = new ArrayList<>();
//        ArrayList<String> lastNames = new ArrayList<>();
//        ArrayList<String> emails = new ArrayList<>();
//        ArrayList<String> uniqueIds = new ArrayList<>();
//        ArrayList<String> cellphoneNumbers = new ArrayList<>();
//        for (AddContactRequest contact : requests) {
//            firstNames.add(contact.getFirstName());
//            lastNames.add(contact.getLastName());
//            emails.add(contact.getEmail());
//            uniqueIds.add(contact.getUniqueId());
//            cellphoneNumbers.add(contact.getCellphoneNumber());
//        }

//        Map queryMap = new HashMap<>();
//        queryMap.put("firstName", "Ali");
//        queryMap.put("lastName", "ali");
//        queryMap.put("cellphoneNumber", "555-1234");
//        queryMap.put("uniqueId", "12343444fdgd");
//        queryMap.put("email", "ali@example.com");
//        queryMap.put("firstName", "Sam");
//        queryMap.put("lastName", "sam");
//        queryMap.put("cellphoneNumber", "555-5678");
//        queryMap.put("email", "reza@example.com");
//        queryMap.put("uniqueId", "kdhsafdfdgd");
//        Call<Response<Contacts>> call = apiService.addContacts(config.getToken(), 1, queryMap);
//
//        Call<Response<Contacts>> call = apiService.addContacts(config.getToken(), 1, "default",firstNames, lastNames, emails, uniqueIds, cellphoneNumbers);
//        call.enqueue(new Callback<Response<Contacts>>() {
//            @Override
//            public void onResponse(Call<Response<Contacts>> call, Response<Response<Contacts>> response) {
//                if (response != null && response.isSuccessful()) {
//                    if (response.body() != null) {
//                        List<Contact> responseContacts = response.body().body().getResult();
//                    }
//                    // Do something with the list of contacts returned by the API
//                } else {
//                    String errorMessage = null;
//                    if (response != null && response.errorBody() != null) {
//                        try {
//                            errorMessage = response.errorBody().string();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    // Handle the error
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Response<Contacts>> call, Throwable t) {
//                // Handle the failure
//            }
//        });
//
//        return requests.get(0).getUniqueId();
//    }

    void internalGetUserInfo() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setToken(config.getToken());
        chatMessage.setUniqueId(UUID.randomUUID().toString());
        chatMessage.setType(ChatMessageType.USER_INFO);
        chatMessage.setTypeCode(config.getTypeCode());
        sendToAsync(chatMessage);
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
            sendToAsync(chatMessage);
        }
    }

    private void sendToAsync(ChatMessage chatMessage) {
        String json = gson.toJson(chatMessage);
        asyncSdk.model.Message message = new Message();
        message.setContent(json);
        message.setPeerName(config.getAsyncConfig().getServerName());
        message.setTtl(config.getTtl());
        message.setUniqueId(chatMessage.getUniqueId());
        async.sendMessage(message, Message);
        logger.info("CHAT_SDK Send With type " + chatMessage.getType() + ": \n" + json + "\n");
    }
}
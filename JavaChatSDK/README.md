# # FanapPodChatSDK
<img src="https://gitlab.com/hamed8080/fanappodchatsdk/-/raw/gl-pages/.docs/favicon.svg"  width="64" height="64">
<br />
<br />

Fanap's POD Chat Service - JAVA SDK
## Features

- [x] Simplify Socket connection to Async server
- [x] Caching system
- [x] Static file response
- [x] Downlaod / Upload File or Data or Image resumebble
- [x] Manage threads and messages
- [x] Manage multiple accounts at the same time

## How to use?

```swift
@Builder
@Getter
@Setter
AsyncConfig {
    private boolean isSocketProvider;
    private String token;
    private String serverName;
    private String ssoHost;
    private String queueServer;
    private String queuePort;
    private String queueInput;
    private String queueOutput;
    private String queueUserName;
    private String queuePassword;
    private int queueReconnectTime;
    private String socketAddress;
    private boolean isLoggable;
    private String appId;


@Builder
@Getter
@Setter
ChatConfig {
    AsyncConfig asyncConfig;
    String severName;
    String token;
    String ssoHost;
    String platformHost;
    String fileServer;
    Long chatId;
    String typeCode = "default";
    long ttl;
    boolean isLoggable = false;
}

  public synchronized static Chat init(ChatConfig chatConfig, ChatListener listener) {
        if (instance == null) {
            async = Async.getInstance(chatConfig.getAsyncConfig());
            instance = new Chat(chatConfig, listener);
            gson = new Gson();
        }
        return instance;
    }
```




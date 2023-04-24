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
- [x] Manage conversations and messages
- [x] Manage multiple accounts at the same time

## How to use?

```swift
@Builder
@Getter
@Setter
AsyncConfig {
    boolean isSocketProvider;
    String token;
    String serverName;
    String ssoHost;
    String queueServer;
    String queuePort;
    String queueInput;
    String queueOutput;
    String queueUserName;
    String queuePassword;
    int queueReconnectTime;
    String socketAddress;
    boolean isLoggable;
    String appId;


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




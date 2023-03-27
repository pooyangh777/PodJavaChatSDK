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
public class ChatConfig {
    private AsyncConfig asyncConfig;
    private String severName;
    private String token;
    private String ssoHost;
    private String platformHost;
    private String fileServer;
    private Long chatId;
    private String typeCode = "default";
    private long ttl;
    private boolean isLoggable = false;
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




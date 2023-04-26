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
 AsyncConfig asyncConfig = AsyncConfig
                .builder()
                .isSocketProvider(isSocket)
                .socketAddress(socketAddress)
                .serverName(serverName)
                .queuePassword(queuePassword)
                .queueUserName(queueUserName)
                .queueInput(queueInput)
                .queueOutput(queueOutput)
                .queueServer(queueServer)
                .queuePort(queuePort)
                .isLoggable(true)
                .appId("PodChat")
                .build();
        ChatConfig chatConfig = ChatConfig.builder()
                .asyncConfig(asyncConfig)
                .severName(serverName)
                .token("ac08a5eb74d34daf88cbe732ddbdcf8c.XzIwMjM0")
                .chatId(chatId)
                .fileServer(fileServer)
                .ssoHost(ssoHost)
                .platformHost(platformHost)
                .isLoggable(true)
                .build();
 ```
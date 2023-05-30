package chatSdk.dataTransferObject.chat;


import asyncSdk.AsyncConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
    @Builder.Default
    private String typeCode = "default";
    private long ttl;
    @Builder.Default
    private boolean isLoggable = false;
    @Builder.Default
    private Long maxReconnectCount = 5L;
    @Builder.Default
    private Long reconnectInterval = 5000L;
}

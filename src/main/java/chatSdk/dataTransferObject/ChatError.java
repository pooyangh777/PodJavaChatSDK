package chatSdk.dataTransferObject;

import chatSdk.chat.GsonFactory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatError {
    private String message;
    private Integer code;
    private Boolean hasError;
    private String content;
    private BanError banError;

    public void setMessage(String message) {
        this.message = message;
        try {
            banError = GsonFactory.gson.fromJson(message, BanError.class);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

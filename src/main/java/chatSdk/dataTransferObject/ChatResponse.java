package chatSdk.dataTransferObject;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ChatResponse<T> {
    private T result;
    private ChatError error;
    private Long contentCount;
    private Long time;
    private String uniqueId;
    private Long subjectId;
}
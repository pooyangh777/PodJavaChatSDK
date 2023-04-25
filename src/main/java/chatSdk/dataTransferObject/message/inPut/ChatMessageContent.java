package chatSdk.dataTransferObject.message.inPut;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * [ asc | desc ] change sort of order message. default is desc
 */
@Setter
@Getter
public class ChatMessageContent {
    private long count;
    private String name;
    @SerializedName("new")
    private boolean New;
    private Integer firstMessageId;
    private Integer lastMessageId;
    private long offset;
    private String order;
    private List<Integer> threadIds;
    private boolean admin;
}

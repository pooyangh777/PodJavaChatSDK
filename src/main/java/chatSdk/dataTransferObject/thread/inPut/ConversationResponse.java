package chatSdk.dataTransferObject.thread.inPut;


import chatSdk.dataTransferObject.message.inPut.MessageVO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class ConversationResponse{
    private Long id;
    private Long joinDate;
    private Participant inviter;
    private String title;
    private List<Participant> participants;
    private Long time;
    private String lastMessage;
    private String lastParticipantName;
    private Boolean group;
    private Long partner;
    private String lastParticipantImage;
    private Long unreadCount;
    private Long lastSeenMessageId;
    private Long lastSeenMessageTime;
    private Integer lastSeenMessageNanos;
    private MessageVO lastMessageVO;
    private Long partnerLastSeenMessageId;
    private Long partnerLastSeenMessageTime;
    private Integer partnerLastSeenMessageNanos;
    private Long partnerLastDeliveredMessageId;
    private Long partnerLastDeliveredMessageTime;
    private Integer partnerLastDeliveredMessageNanos;
    private Integer type;
    private String image;
    private String description;
    private String metadata;
    private Boolean mute;
    private Long participantCount;
    private Boolean canEditInfo;
    private Boolean canSpam;
    private Boolean admin;
    private Boolean mentioned;
    private Boolean pin;
}

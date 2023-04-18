package chatSdk.dataTransferObject.thread.inPut;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * + RequestCreateThread    {object}
 * - ownerSsoId          {string}
 * + invitees            {object}
 * -id                {string}
 * -idType            {int} ** inviteeVOidTypes
 * - title               {string}
 * - type                {int} ** createThreadTypes
 */
@Setter
@Getter
public class ChatThread {
    private int type;
    private String ownerSsoId;
    private List<Invitee> invitees;
    private String title;
    private String description;
    private String image;
    private String metadata;
}

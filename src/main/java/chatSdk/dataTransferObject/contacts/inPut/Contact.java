package chatSdk.dataTransferObject.contacts.inPut;

import chatSdk.dataTransferObject.user.inPut.LinkedUser;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Contact {
    private long id;
    private String firstName;
    private long userId;
    private String lastName;
    private Boolean blocked;
    private long creationDate;
    private LinkedUser linkedUser;
    private String cellphoneNumber;
    private String email;
    private String uniqueId;
    private long notSeenDuration;
    private boolean hasUser;
}


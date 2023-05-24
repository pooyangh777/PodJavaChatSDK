package chatSdk.dataTransferObject.contacts.inPut;

import chatSdk.dataTransferObject.user.inPut.LinkedUser;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Contact {
    private Long id;
    private String firstName;
    private Long userId;
    private String lastName;
    private Boolean blocked;
    private Long creationDate;
    private LinkedUser linkedUser;
    private String cellphoneNumber;
    private String email;
    private String uniqueId;
    private Long notSeenDuration;
    private Boolean hasUser;
}


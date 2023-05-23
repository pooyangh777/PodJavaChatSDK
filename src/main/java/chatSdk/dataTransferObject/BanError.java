package chatSdk.dataTransferObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class BanError {
    public String errorMessage;
    public Integer duration;
    public String uniqueId;
}

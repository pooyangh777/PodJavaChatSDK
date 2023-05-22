package chatSdk.chat.chatInterface;

import chatSdk.dataTransferObject.GeneralRequest;
import chatSdk.dataTransferObject.system.outPut.SetAdminRequest;

public interface SystemInterface {
    String addAdmin(SetAdminRequest request);
    String currentUserRoles(GeneralRequest request);
}

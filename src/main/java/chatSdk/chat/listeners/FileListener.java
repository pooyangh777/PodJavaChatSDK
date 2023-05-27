package chatSdk.chat.listeners;

import chatSdk.dataTransferObject.ChatResponse;
import chatSdk.dataTransferObject.ChatResponse2;
import chatSdk.dataTransferObject.file.inPut.ResultFile;
import chatSdk.dataTransferObject.file.inPut.ResultImageFile;
import chatSdk.dataTransferObject.user.inPut.ResultUserInfo;
import chatSdk.dataTransferObject.user.inPut.UserInfo;

public interface FileListener {
    default void onUserInfo(ChatResponse<UserInfo> userInfo) {}

    default void onUploadImageFile(String content, ChatResponse2<ResultImageFile> response) {}
    default void onUploadFile(String content, ChatResponse2<ResultFile> response) {}
}

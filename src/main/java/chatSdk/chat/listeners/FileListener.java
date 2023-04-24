package chatSdk.chat.listeners;

import chatSdk.dataTransferObject.ChatResponse;
import chatSdk.dataTransferObject.file.inPut.ResultFile;
import chatSdk.dataTransferObject.file.inPut.ResultImageFile;
import chatSdk.dataTransferObject.user.inPut.ResultUserInfo;

public interface FileListener {
    default void onUserInfo(String content, ChatResponse<ResultUserInfo> response) {}
    default void onUploadImageFile(String content, ChatResponse<ResultImageFile> response) {}
    default void onUploadFile(String content, ChatResponse<ResultFile> response) {}
}

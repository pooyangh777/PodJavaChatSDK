package chatSdk.chat.listeners;

import chatSdk.dataTransferObject.ChatResponse2;
import chatSdk.dataTransferObject.file.inPut.ResultFile;
import chatSdk.dataTransferObject.file.inPut.ResultImageFile;
import chatSdk.dataTransferObject.user.inPut.ResultUserInfo;

public interface FileListener {
    default void onUserInfo(String content, ChatResponse2<ResultUserInfo> response) {}
    default void onUploadImageFile(String content, ChatResponse2<ResultImageFile> response) {}
    default void onUploadFile(String content, ChatResponse2<ResultFile> response) {}
}

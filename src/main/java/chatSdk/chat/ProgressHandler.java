package chatSdk.chat;

import chatSdk.dataTransferObject.ChatResponse;
import chatSdk.dataTransferObject.system.outPut.ErrorOutPut;
import chatSdk.dataTransferObject.file.inPut.ResultFile;
import chatSdk.dataTransferObject.file.inPut.ResultImageFile;
import chatSdk.dataTransferObject.file.inPut.FileUpload;

//A callback for while file is being uploaded.
public abstract class ProgressHandler {

    public interface onProgress {
        default void onProgressUpdate(int bytesSent) {
        }

        default void onProgressUpdate(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {
        }

        default void onFinish(String imageJson, ChatResponse<ResultImageFile> chatResponse) {
        }

        default void onError(String jsonError, ErrorOutPut error) {
        }
    }

    public interface onProgressFile {
        void onProgressUpdate(int bytesSent);

        default void onProgress(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {
        }

        default void onFinish(String imageJson, FileUpload fileImageUpload) {
        }

        default void onError(String jsonError, ErrorOutPut error) {
        }
    }

    public interface sendFileMessage {

        default void onProgressUpdate(String uniqueId, int bytesSent, int totalBytesSent, int totalBytesToSend) {
        }

        default void onFinishImage(String json, ChatResponse<ResultImageFile> chatResponse) {
        }

        default void onFinishFile(String json, ChatResponse<ResultFile> chatResponse) {
        }

        default void onError(String jsonError, ErrorOutPut error) {
        }
    }

    public interface cancelUpload {
        default void cancelUpload(String uniqueId) {

        }
    }

    public void onProgress(int bytesSent, int totalBytesSent, int totalBytesToSend) {
    }
}

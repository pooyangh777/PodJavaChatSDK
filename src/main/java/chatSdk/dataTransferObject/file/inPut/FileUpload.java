package chatSdk.dataTransferObject.file.inPut;

import chatSdk.dataTransferObject.file.inPut.ResultFile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUpload {
    private boolean hasError;
    private int errorCode;
    private String referenceNumber;
    private int count;
    private String ott;
    private String message;
    private ResultFile result;
}

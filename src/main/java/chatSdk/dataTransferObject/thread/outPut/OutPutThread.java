package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.BaseOutPut;
import chatSdk.dataTransferObject.thread.inPut.ResultThread;

public class OutPutThread extends BaseOutPut {
    private ResultThread result;

    public ResultThread getResult() {
        return result;
    }

    public void setResult(ResultThread result) {
        this.result = result;
    }
}

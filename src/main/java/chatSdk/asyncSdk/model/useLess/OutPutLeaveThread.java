package chatSdk.asyncSdk.model.useLess;

import chatSdk.dataTransferObject.BaseOutPut;
import chatSdk.dataTransferObject.thread.inPut.ResultLeaveThread;

public class OutPutLeaveThread extends BaseOutPut {

    private ResultLeaveThread result;

    public ResultLeaveThread getResult() {
        return result;
    }

    public void setResult(ResultLeaveThread result) {
        this.result = result;
    }
}

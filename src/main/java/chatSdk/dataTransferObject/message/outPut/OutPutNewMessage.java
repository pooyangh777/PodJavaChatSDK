package chatSdk.dataTransferObject.message.outPut;

import chatSdk.dataTransferObject.BaseOutPut;
import chatSdk.dataTransferObject.message.inPut.ResultNewMessage;

public class OutPutNewMessage extends BaseOutPut {
    private ResultNewMessage result;

    public ResultNewMessage getResult() {
        return result;
    }

    public void setResult(ResultNewMessage result) {
        this.result = result;
    }
}

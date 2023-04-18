package chatSdk.asyncSdk.model.useLess;

import chatSdk.dataTransferObject.BaseOutPut;
import chatSdk.dataTransferObject.thread.inPut.ResultAddParticipant;

public class OutPutAddParticipant extends BaseOutPut {

    private ResultAddParticipant result;

    public ResultAddParticipant getResult() {
        return result;
    }

    public void setResult(ResultAddParticipant result) {
        this.result = result;
    }
}

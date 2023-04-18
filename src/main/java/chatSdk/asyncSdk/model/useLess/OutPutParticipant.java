package chatSdk.asyncSdk.model.useLess;

import chatSdk.dataTransferObject.BaseOutPut;
import chatSdk.dataTransferObject.thread.inPut.ResultParticipant;

public class OutPutParticipant extends BaseOutPut {

    private ResultParticipant result;

    public ResultParticipant getResult() {
        return result;
    }

    public void setResult(ResultParticipant result) {
        this.result = result;
    }


}

package chatSdk.dataTransferObject.user.outPut;


import chatSdk.dataTransferObject.BaseOutPut;
import chatSdk.dataTransferObject.user.inPut.ResultHistory;

public class OutPutHistory extends BaseOutPut {

    private ResultHistory result;

    public ResultHistory getResult() {
        return result;
    }

    public void setResult(ResultHistory result) {
        this.result = result;
    }
}

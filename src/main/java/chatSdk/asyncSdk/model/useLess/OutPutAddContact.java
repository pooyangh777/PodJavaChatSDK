package chatSdk.asyncSdk.model.useLess;

import chatSdk.asyncSdk.model.BaseOutPut;
import chatSdk.asyncSdk.model.ResultAddContact;

public class OutPutAddContact extends BaseOutPut {

    private ResultAddContact result;

    public ResultAddContact getResult() {
        return result;
    }

    public void setResult(ResultAddContact result) {
        this.result = result;
    }
}

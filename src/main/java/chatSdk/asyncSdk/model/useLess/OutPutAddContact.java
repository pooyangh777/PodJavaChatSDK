package chatSdk.asyncSdk.model.useLess;

import chatSdk.dataTransferObject.BaseOutPut;
import chatSdk.dataTransferObject.contacts.inPut.ResultAddContact;

public class OutPutAddContact extends BaseOutPut {

    private ResultAddContact result;

    public ResultAddContact getResult() {
        return result;
    }

    public void setResult(ResultAddContact result) {
        this.result = result;
    }
}

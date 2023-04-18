package chatSdk.asyncSdk.model.useLess;

import chatSdk.dataTransferObject.BaseOutPut;
import chatSdk.dataTransferObject.contacts.inPut.ResultContact;

public class OutPutContact extends BaseOutPut {

    private ResultContact result;

    public ResultContact getResult() {
        return result;
    }

    public void setResult(ResultContact result) {
        this.result = result;
    }

}

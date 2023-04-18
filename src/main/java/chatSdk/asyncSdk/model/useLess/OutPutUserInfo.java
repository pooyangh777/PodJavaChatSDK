package chatSdk.asyncSdk.model.useLess;

import chatSdk.dataTransferObject.BaseOutPut;
import chatSdk.dataTransferObject.user.inPut.ResultUserInfo;

public class OutPutUserInfo extends BaseOutPut {

    private ResultUserInfo result;

    public ResultUserInfo getResult() {
        return result;
    }

    public void setResult(ResultUserInfo resultUserInfo) {
        this.result = resultUserInfo;
    }
}

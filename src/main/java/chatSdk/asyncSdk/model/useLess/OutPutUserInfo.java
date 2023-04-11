package chatSdk.asyncSdk.model.useLess;

import chatSdk.asyncSdk.model.BaseOutPut;
import chatSdk.asyncSdk.model.ResultUserInfo;

public class OutPutUserInfo extends BaseOutPut {

    private ResultUserInfo result;

    public ResultUserInfo getResult() {
        return result;
    }

    public void setResult(ResultUserInfo resultUserInfo) {
        this.result = resultUserInfo;
    }
}

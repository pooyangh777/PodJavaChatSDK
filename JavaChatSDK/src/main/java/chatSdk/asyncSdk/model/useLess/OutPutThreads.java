package chatSdk.asyncSdk.model.useLess;

import chatSdk.asyncSdk.model.BaseOutPut;

public class OutPutThreads<T> extends BaseOutPut {

    private T result;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}

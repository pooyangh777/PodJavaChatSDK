package chatSdk.dataTransferObject.map.outPut;

import chatSdk.dataTransferObject.BaseOutPut;
import chatSdk.dataTransferObject.map.inPut.ResultMap;

public class OutPutMapNeshan extends BaseOutPut {
    private long count;
    private ResultMap result;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public ResultMap getResult() {
        return result;
    }

    public void setResult(ResultMap result) {
        this.result = result;
    }
}

package posmy.interview.boot.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import posmy.interview.boot.constants.StatusResponse;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T> implements Serializable {

    public int code;
    public String description;
    public T data;

    public ResponseResult() {
        this.code = StatusResponse.OK.getCode();
        this.description = StatusResponse.OK.getDescription();
    }

    public ResponseResult(StatusResponse statusResponse) {
        this.code = statusResponse.getCode();
        this.description = statusResponse.getDescription();
    }

    public ResponseResult(T data) {
        this.code = StatusResponse.OK.getCode();
        this.description = StatusResponse.OK.getDescription();
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseResult{" +
                "code=" + code +
                ", description='" + description + '\'' +
                ", data=" + data +
                '}';
    }
}

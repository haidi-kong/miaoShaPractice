package com.travel.order.providers.Exception;

import com.travel.common.enums.ResultStatus;

/**
 * @author
 */
public class MqOrderException extends RuntimeException {

    private ResultStatus status;

    public MqOrderException(ResultStatus status){
        super();
        this.status = status;
    }

    public MqOrderException(Integer code ,String msg){
        super();
    }

    public ResultStatus getStatus() {
        return status;
    }

    public void setStatus(ResultStatus status) {
        this.status = status;
    }
}

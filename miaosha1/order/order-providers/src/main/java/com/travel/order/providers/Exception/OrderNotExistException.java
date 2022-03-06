package com.travel.order.providers.Exception;

import com.travel.common.enums.ResultStatus;


public class OrderNotExistException extends RuntimeException {

    private ResultStatus status;

    public OrderNotExistException(ResultStatus status){
        super();
        this.status = status;
    }

    public OrderNotExistException() {

    }

    public OrderNotExistException(String message) {
        super(message);
    }

    public ResultStatus getStatus() {
        return status;
    }

    public void setStatus(ResultStatus status) {
        this.status = status;
    }
}



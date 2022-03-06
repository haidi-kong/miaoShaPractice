package com.travel.order.providers.Exception;

import com.travel.common.enums.ResultStatus;


public class StockShortageException extends RuntimeException {

    private ResultStatus status;

    public StockShortageException(ResultStatus status){
        super();
        this.status = status;
    }

    public StockShortageException() {

    }

    public StockShortageException(String message) {
        super(message);
    }

    public ResultStatus getStatus() {
        return status;
    }

    public void setStatus(ResultStatus status) {
        this.status = status;
    }
}



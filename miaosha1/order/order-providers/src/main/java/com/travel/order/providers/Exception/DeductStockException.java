package com.travel.order.providers.Exception;

import com.travel.common.enums.ResultStatus;

/**
 * @author
 */
public class DeductStockException extends RuntimeException {

    private ResultStatus status;

    public DeductStockException(ResultStatus status){
        super();
        this.status = status;
    }

    public ResultStatus getStatus() {
        return status;
    }

    public void setStatus(ResultStatus status) {
        this.status = status;
    }
}

package com.travel.users.providers.exceptions;

import com.travel.common.enums.ResultStatus;


public class AccountException extends RuntimeException {

    private ResultStatus status;

    public AccountException(ResultStatus status){
        super();
        this.status = status;
    }

    public AccountException() {

    }

    public AccountException(String message) {
        super(message);
    }

    public ResultStatus getStatus() {
        return status;
    }

    public void setStatus(ResultStatus status) {
        this.status = status;
    }
}



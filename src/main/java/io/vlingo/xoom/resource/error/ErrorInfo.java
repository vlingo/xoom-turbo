// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.resource.error;

public class ErrorInfo {

    private String message;
    private String exception;

    public ErrorInfo() {
    }

    public ErrorInfo(Throwable throwable) {
        this.message = throwable.getLocalizedMessage();
        this.exception = throwable.getClass().getName();
    }

    public ErrorInfo(String message, String exception) {
        this.message = message;
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "ErrorInfo{" +
                "message='" + message + '\'' +
                ", exception='" + exception + '\'' +
                '}';
    }
}

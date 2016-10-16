package com.ydq.crash.exception;

/**
 * <p>Title: CustomException</p>
 * <p>Description:系统 自定义异常类，针对预期的异常，需要在程序中抛出此类的异常 </p>
 *
 * @author yandeqing
 */
public class CustomException extends RuntimeException implements ExceptionCode {

    //异常信息
    public String message;
    //异常错误码
    public int code = 0;

    public CustomException(int code) {
        super();
        this.code = code;
    }

    public CustomException(String message) {
        super(message);
        this.message = message;
    }

    public CustomException(int code, String message) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        switch (code) {
            case NONE:
                break;

            case NO_ACCOUNT:
                message="账号未设置，请设置账号";
                break;

            case NO_PWD:
                message="账号未设置，请设置密码";
                break;

            case PWD_ERR:
                message="账号密码验证失败，请设检查账号密码";
                break;
            case NO_RECEIVER:
                message="请设置接收日志的邮箱";
                break;
        }
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

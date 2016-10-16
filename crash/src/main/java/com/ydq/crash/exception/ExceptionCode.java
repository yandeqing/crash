package com.ydq.crash.exception;

public interface ExceptionCode {
    public final static int NONE = 0x0000;
    public final static int NO_ACCOUNT = NONE + 1;
    public final static int NO_PWD = NO_ACCOUNT + 1;
    public final static int NO_RECEIVER = NO_PWD + 1;
    public final static int PWD_ERR = NO_RECEIVER + 1;
}
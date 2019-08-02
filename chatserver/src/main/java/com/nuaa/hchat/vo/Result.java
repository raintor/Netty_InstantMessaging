package com.nuaa.hchat.vo;

/**
 * @author: raintor
 * @Date: 2019/6/22 16:31
 * @Description:
 */

/**
 * 用于返回给前端结果信息
 */
public class Result<T> {
    private boolean success;

    private String message;

    private T result;

    /**
     * faile only result status and msg
     * @param success
     * @param message
     */
    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * success return all
     * @param success
     * @param message
     * @param result
     */
    public Result(boolean success, String message, T result) {
        this.success = success;
        this.message = message;
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}

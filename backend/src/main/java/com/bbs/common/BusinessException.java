package com.bbs.common;

/**
 * 业务异常类
 * <p>用于业务逻辑校验失败时抛出，如"账号已存在"、"密码错误"等</p>
 * <p>与RuntimeException区分，GlobalExceptionHandler会对此类异常返回HTTP 400</p>
 */
public class BusinessException extends RuntimeException {

    /** 业务错误码 */
    private final int code;

    /**
     * 构造业务异常
     *
     * @param message 错误信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    /**
     * 构造业务异常（自定义错误码）
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

package com.demo.server.user.constant;

/**
 * 用户域常量。
 */
public final class UserConstants {

    private UserConstants() {
    }

    /** 昵称最大长度。 */
    public static final int NICKNAME_MAX_LEN = 32;
    /** 密码最小长度。 */
    public static final int PASSWORD_MIN_LEN = 6;
    /** 密码最大长度。 */
    public static final int PASSWORD_MAX_LEN = 64;
    /** 手机号长度。 */
    public static final int MOBILE_LEN = 11;
    /** 邮箱最大长度。 */
    public static final int EMAIL_MAX_LEN = 128;
    /** 头像 URL 最大长度。 */
    public static final int AVATAR_MAX_LEN = 512;

    /** 错误码：用户不存在。 */
    public static final String CODE_USER_NOT_FOUND = "USER_NOT_FOUND";
    /** 错误码：手机号已注册。 */
    public static final String CODE_MOBILE_DUPLICATED = "MOBILE_DUPLICATED";
    /** 错误码：用户被禁用。 */
    public static final String CODE_USER_DISABLED = "USER_DISABLED";
    /** 错误码：密码错误。 */
    public static final String CODE_PASSWORD_INCORRECT = "PASSWORD_INCORRECT";

    /** 中国大陆手机号正则。 */
    public static final String MOBILE_REGEX = "^1[3-9]\\d{9}$";
    /** 邮箱正则。 */
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
}

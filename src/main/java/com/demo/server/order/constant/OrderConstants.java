package com.demo.server.order.constant;

/**
 * 订单域常量。
 */
public final class OrderConstants {

    private OrderConstants() {
    }

    /** 订单号长度。 */
    public static final int ORDER_NO_LEN = 20;
    /** 备注最大长度。 */
    public static final int REMARK_MAX_LEN = 256;
    /** 单订单商品数量上限。 */
    public static final int MAX_ITEM_COUNT = 100;
    /** 订单金额最小值。 */
    public static final double AMOUNT_MIN = 0d;
    /** 订单金额最大值。 */
    public static final double AMOUNT_MAX = 99_999_999d;
    /** 自动取消时间（毫秒），未支付 30 分钟。 */
    public static final long AUTO_CANCEL_TIMEOUT_MS = 30L * 60L * 1000L;
    /** 自动确认收货时间（毫秒），发货后 7 天。 */
    public static final long AUTO_CONFIRM_TIMEOUT_MS = 7L * 24L * 60L * 60L * 1000L;

    /** 错误码：订单不存在。 */
    public static final String CODE_ORDER_NOT_FOUND = "ORDER_NOT_FOUND";
    /** 错误码：订单状态非法。 */
    public static final String CODE_ORDER_STATUS_INVALID = "ORDER_STATUS_INVALID";
    /** 错误码：订单不可取消。 */
    public static final String CODE_ORDER_NOT_CANCELLABLE = "ORDER_NOT_CANCELLABLE";
    /** 错误码：订单不可支付。 */
    public static final String CODE_ORDER_NOT_PAYABLE = "ORDER_NOT_PAYABLE";
    /** 错误码：订单不可发货。 */
    public static final String CODE_ORDER_NOT_SHIPPABLE = "ORDER_NOT_SHIPPABLE";
    /** 错误码：订单不属于该用户。 */
    public static final String CODE_ORDER_OWNER_MISMATCH = "ORDER_OWNER_MISMATCH";

    /** 订单号前缀。 */
    public static final String ORDER_NO_PREFIX = "OD";
}

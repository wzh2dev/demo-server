package com.demo.server.product.constant;

/**
 * 商品域常量。
 */
public final class ProductConstants {

    private ProductConstants() {
    }

    /** 商品名最大长度。 */
    public static final int NAME_MAX_LEN = 128;
    /** 子标题最大长度。 */
    public static final int SUBTITLE_MAX_LEN = 256;
    /** 描述最大长度。 */
    public static final int DESCRIPTION_MAX_LEN = 4000;
    /** 品牌最大长度。 */
    public static final int BRAND_MAX_LEN = 64;
    /** 图片 URL 最大长度。 */
    public static final int IMAGE_URL_MAX_LEN = 512;
    /** 单商品图片数量上限。 */
    public static final int MAX_IMAGE_COUNT = 20;
    /** 价格最小值。 */
    public static final double PRICE_MIN = 0d;
    /** 价格最大值。 */
    public static final double PRICE_MAX = 99_999_999d;

    /** 错误码：商品不存在。 */
    public static final String CODE_PRODUCT_NOT_FOUND = "PRODUCT_NOT_FOUND";
    /** 错误码：商品已下架。 */
    public static final String CODE_PRODUCT_OFF_SHELF = "PRODUCT_OFF_SHELF";
    /** 错误码：库存不足。 */
    public static final String CODE_STOCK_INSUFFICIENT = "STOCK_INSUFFICIENT";
    /** 错误码：价格非法。 */
    public static final String CODE_PRICE_INVALID = "PRICE_INVALID";

    /** 排序：价格升序。 */
    public static final String SORT_PRICE_ASC = "price_asc";
    /** 排序：价格降序。 */
    public static final String SORT_PRICE_DESC = "price_desc";
    /** 排序：创建时间降序。 */
    public static final String SORT_CREATED_DESC = "created_desc";
    /** 排序：销量降序。 */
    public static final String SORT_SOLD_DESC = "sold_desc";
}

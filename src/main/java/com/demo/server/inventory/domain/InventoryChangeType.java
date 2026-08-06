package com.demo.server.inventory.domain;

/**
 * 库存变更类型。
 */
public enum InventoryChangeType {
    INBOUND,
    OUTBOUND,
    LOCK,
    UNLOCK,
    ADJUST
}

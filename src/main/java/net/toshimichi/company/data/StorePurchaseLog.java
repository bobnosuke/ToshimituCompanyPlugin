package net.toshimichi.company.data;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
public class StorePurchaseLog {

    private final String player;
    private final double price;
    private final LocalDateTime purchasedAt = LocalDateTime.now(ZoneOffset.UTC);
}

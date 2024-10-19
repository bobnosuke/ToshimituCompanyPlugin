package net.toshimichi.company;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Map;

@RequiredArgsConstructor
@Getter
@ConfigSerializable
public class Config {

    private int timezone = 9;
    private int companyCreateCost = 10000;
    private int joinRequestTimeout = 600;
    private int confirmTimeout = 600;
    private Map<Integer, Double> levels = Map.of(
            1, 15000.0,
            2, 30000.0,
            3, 45000.0,
            4, 55000.0,
            5, 65000.0,
            6, 100000.0
    );
}

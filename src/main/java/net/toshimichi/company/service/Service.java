package net.toshimichi.company.service;

public interface Service {

    default void onEnable() {

    }

    default void onDisable() {

    }

    default void onTick(int tick) {

    }
}

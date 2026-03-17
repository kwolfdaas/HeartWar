package me.kharique.heartclasses.data;

import me.kharique.heartclasses.hearts.HeartType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private HeartType heartType;
    private int hearts;
    private final Set<HeartType> ownedTypes;

    public PlayerData(UUID uuid, HeartType heartType, int hearts) {
        this(uuid, heartType, hearts, Collections.singleton(heartType));
    }

    public PlayerData(UUID uuid, HeartType heartType, int hearts, Set<HeartType> ownedTypes) {
        this.uuid = uuid;
        this.heartType = heartType;
        this.hearts = hearts;
        this.ownedTypes = new HashSet<>(ownedTypes);
        this.ownedTypes.add(heartType);
    }

    public UUID getUuid() {
        return uuid;
    }

    public HeartType getHeartType() {
        return heartType;
    }

    public void setHeartType(HeartType heartType) {
        this.heartType = heartType;
        this.ownedTypes.add(heartType);
    }

    public int getHearts() {
        return hearts;
    }

    public void setHearts(int hearts) {
        this.hearts = hearts;
    }

    public void addHearts(int amount) {
        this.hearts += amount;
    }

    public void removeHearts(int amount) {
        this.hearts = Math.max(0, this.hearts - amount);
    }

    public Set<HeartType> getOwnedTypes() {
        return Collections.unmodifiableSet(ownedTypes);
    }

    public void addOwnedType(HeartType type) {
        this.ownedTypes.add(type);
    }

    public boolean hasHeart(HeartType type) {
        return this.heartType == type || ownedTypes.contains(type);
    }
}

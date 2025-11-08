package kfs.boulder;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Tile {
    PLAYER('P'),
    DIRT('.'),
    ROCK('#'),
    EMPTY('e'),
    GEM('*'),
    STONE('s'),
    DOOR_CLOSED('X'),
    DOOR_OPENED('Y');

    public final char sym;

    private Tile(char sym) {
        this.sym = sym;
    }

    public char getCode() {
        return sym;
    }

    private static final Map<Character, Tile> BY_CODE =
        Arrays.stream(values())
            .collect(Collectors.toMap(Tile::getCode, e -> e));

    public static Tile fromCode(char c) {
        return BY_CODE.get(c);
    }
}

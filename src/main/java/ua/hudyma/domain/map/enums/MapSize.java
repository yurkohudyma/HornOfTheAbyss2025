package ua.hudyma.domain.map.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MapSize {

    S (36),
    M (72),
    L (108),
    XL (144),
    H (180),
    XH (216),
    G (252);

    private final int mapSize;
}

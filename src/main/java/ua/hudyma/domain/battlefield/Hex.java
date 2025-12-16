package ua.hudyma.domain.battlefield;

import lombok.Data;

@Data
public class Hex {
    private Hex nw;
    private Hex ne;
    private Hex w;
    private Hex e;
    private Hex sw;
    private Hex se;
}

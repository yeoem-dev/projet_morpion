package yeo.aras.morpion;

public enum Owner {
    FIRST, SECOND, NONE;
    public Owner opposite() {
        return this == SECOND ? FIRST: this == FIRST ? SECOND : NONE;
    }
}

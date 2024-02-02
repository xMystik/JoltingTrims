package me.skript.joltingtrims.utilities.enums;

public enum ItemType {

    FILLER("FILLER"),
    GENERAL_MENU_OPENER("GENERAL_MENU_OPENER"),
    MATERIAL_MENU_OPENER("MATERIAL_MENU_OPENER"),
    PATTERN_MENU_OPENER("PATTERN_MENU_OPENER"),
    CLEAR_PATTERN("CLEAR_PATTERN"),
    FINALIZE_CHANGES("FINALIZE_CHANGES");

    private String type;

    ItemType(String type) {
        this.type = type;
    }

    public String getString() {
        return type;
    }

}

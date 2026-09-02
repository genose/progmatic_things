package com.example.crm.d02.format;

/**
 * Primitives de formatage à largeur fixe, équivalent Java des règles COBOL :
 *   PIC X(n)  → alphaLeft  : cadré à gauche, complété par des espaces, tronqué si trop long.
 *   PIC 9(n)  → numericRight : cadré à droite, complété par des zéros.
 *
 * Les méthodes writeAlpha / writeNumeric écrivent directement dans le tableau
 * de caractères construit par chaque formatteur d'enregistrement.
 */
public final class FixedField {

    private FixedField() {}

    /** PIC X(n) — retourne une String de longueur exacte {@code width}. */
    public static String alphaLeft(String value, int width) {
        if (value == null) value = "";
        if (value.length() >= width) return value.substring(0, width);
        return String.format("%-" + width + "s", value);
    }

    /** PIC 9(n) — retourne une String de longueur exacte {@code width}, zéros à gauche. */
    public static String numericRight(long value, int width) {
        return String.format("%0" + width + "d", value);
    }

    /**
     * Écrit un champ alphanumérique (PIC X) dans {@code rec} à partir de l'index
     * {@code offset} (0-based) sur exactement {@code width} caractères.
     */
    public static void writeAlpha(char[] rec, int offset, String value, int width) {
        String s = alphaLeft(value, width);
        for (int i = 0; i < width; i++) {
            rec[offset + i] = s.charAt(i);
        }
    }

    /**
     * Écrit un champ numérique (PIC 9) dans {@code rec} à partir de l'index
     * {@code offset} (0-based) sur exactement {@code width} caractères, zéros à gauche.
     */
    public static void writeNumeric(char[] rec, int offset, long value, int width) {
        String s = numericRight(value, width);
        for (int i = 0; i < width; i++) {
            rec[offset + i] = s.charAt(i);
        }
    }
}

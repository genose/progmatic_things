package com.example.crm.d02.format;

import org.junit.Test;
import static org.junit.Assert.*;

public class FixedFieldTest {

    // ── alphaLeft ────────────────────────────────────────────────────────────

    @Test
    public void alphaLeft_padsWithSpacesWhenValueIsShorter() {
        assertEquals("hello     ", FixedField.alphaLeft("hello", 10));
    }

    @Test
    public void alphaLeft_truncatesWhenValueIsLonger() {
        assertEquals("hello", FixedField.alphaLeft("hello world!", 5));
    }

    @Test
    public void alphaLeft_exactLengthReturnsSameValue() {
        assertEquals("ABCD", FixedField.alphaLeft("ABCD", 4));
    }

    @Test
    public void alphaLeft_nullTreatedAsEmpty() {
        assertEquals("     ", FixedField.alphaLeft(null, 5));
    }

    @Test
    public void alphaLeft_emptyStringGivesSpaces() {
        assertEquals("   ", FixedField.alphaLeft("", 3));
    }

    // ── numericRight ─────────────────────────────────────────────────────────

    @Test
    public void numericRight_padsWithZeros() {
        assertEquals("0000123", FixedField.numericRight(123L, 7));
    }

    @Test
    public void numericRight_zero() {
        assertEquals("00000000", FixedField.numericRight(0L, 8));
    }

    @Test
    public void numericRight_maxFills() {
        assertEquals("9999999", FixedField.numericRight(9_999_999L, 7));
    }

    @Test
    public void numericRight_singleDigitWidth() {
        assertEquals("7", FixedField.numericRight(7L, 1));
    }

    // ── writeAlpha (char[]) ───────────────────────────────────────────────────

    @Test
    public void writeAlpha_writesAtCorrectOffset() {
        char[] rec = new char[10];
        java.util.Arrays.fill(rec, ' ');
        FixedField.writeAlpha(rec, 3, "AB", 2);
        assertEquals("   AB     ", new String(rec));
    }

    @Test
    public void writeAlpha_doesNotOverwriteNeighbours() {
        char[] rec = new char[10];
        java.util.Arrays.fill(rec, 'X');
        FixedField.writeAlpha(rec, 2, "hi", 2);
        assertEquals('X', rec[1]);
        assertEquals('h', rec[2]);
        assertEquals('i', rec[3]);
        assertEquals('X', rec[4]);
    }

    // ── writeNumeric (char[]) ─────────────────────────────────────────────────

    @Test
    public void writeNumeric_writesZeroPaddedAtOffset() {
        char[] rec = new char[12];
        java.util.Arrays.fill(rec, ' ');
        FixedField.writeNumeric(rec, 2, 42L, 7);
        assertEquals("  0000042   ", new String(rec));
    }
}

package com.lxc.Competitioninformationsystem.utils;

public class StringUtils {
    public static int getIndex(String value, char c, int pos) {
        int idx = 0;
        for(int i = 0; i < value.length(); i++) {
            if(value.charAt(i) == c) {
                idx++;
                if(idx == pos) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int getReverseIndex(String value, char c, int pos) {
        int idx = 0;
        for(int i = value.length() - 1; i >= 0; i--) {
            if(value.charAt(i) == c) {
                idx++;
                if(idx == pos) {
                    return i;
                }
            }
        }
        return -1;
    }
}

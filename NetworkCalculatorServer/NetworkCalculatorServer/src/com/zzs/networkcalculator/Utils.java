package com.zzs.networkcalculator;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static boolean isLegalChar(char ch) {
        return isOperatorOrBrackets(ch) || isLetter(ch) || isNumeric(String.valueOf(ch));
    }

    public static List<String> removeCommonVariable(List<String> list, String common) {
        List<String> newList = new ArrayList<>();
        for (String str : list) {
            for (char ch : common.toCharArray()) {
                str = str.replaceFirst(String.valueOf(ch), "");
            }
            newList.add(str);
        }
        return newList;
    }

    public static String maxSubstring(List<String> list) {
        String max = list.get(0);
        int size = list.size();
        if (size > 1) {
            for (int i = 0; i < size - 1; i++) {
                max = maxSubstring(max, list.get(i + 1));
            }
        }
        return max;
    }

    public static String maxSubstring(String strOne, String strTwo) {
        if (strOne == null || strTwo == null) {
            return null;
        }
        if (strOne.equals("") || strTwo.equals("")) {
            return null;
        }
        String max = "";
        String min = "";
        if (strOne.length() < strTwo.length()) {
            max = strTwo;
            min = strOne;
        } else {
            max = strTwo;
            min = strOne;
        }
        String current = "";
        for (int i = 0; i < min.length(); i++) {
            for (int begin = 0, end = min.length() - i; end <= min.length(); begin++, end++) {
                current = min.substring(begin, end);
                if (max.contains(current)) {
                    return current;
                }
            }
        }
        return null;
    }

    public static List<Factor> removeCommonFactor(List<Factor> list, int inFactor) {
        List<Factor> newList = new ArrayList<>();
        for (Factor fac : list) {
            fac.setSubFactors(removeCommonSubFactor(fac.getSubFactors(), inFactor));
            newList.add(fac);
        }
        return newList;
    }


    private static List<Integer> removeCommonSubFactor(List<Integer> list, int inFactor) {
        List<Integer> newList = new ArrayList<>();
        for (int fac : list) {
            fac /= inFactor;
            newList.add(fac);
        }
        return newList;
    }

    public static int gcd(int m, int n) {
        int result = 0;
        while (n != 0) {
            result = m % n;
            m = n;
            n = result;
        }
        return m;
    }

    public static boolean isLetter(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    /**
     * @param ch
     *
     * @return
     */
    public static boolean isRawOperator(char ch) {
        return '+' == ch || '-' == ch || '*' == ch || '/' == ch || '^' == ch;
    }

    /**
     * @param str
     *
     * @return
     */
    public static boolean isRawOperator(String str) {
        return !str.isEmpty() && 1 == str.length() && isRawOperator(str.charAt(0));
    }

    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param str
     *
     * @return
     */
    public static boolean isOperatorOrBrackets(String str) {
        return "+".equals(str) || "-".equals(str) || "*".equals(str) || "/".equals(str) || "^"
                .equals(str) || "(".equals(
                str) || ")".equals(str);
    }

    /**
     * Is operator or brackets.
     *
     * @param ch
     *
     * @return
     */
    public static boolean isOperatorOrBrackets(char ch) {
        return '+' == ch || '-' == ch || '*' == ch || '/' == ch || '^' == ch || '(' == ch || ')'
                == ch;
    }

    /**
     * Get total operator in an expression.
     *
     * @param input input
     *
     * @return Total operator in an expression
     */
    public static int getTotalOperator(String input) {
        if (null == input || input.isEmpty()) {
            return 0;
        } else {
            int count = 0;
            for (int i = 0; i < input.length(); i++) {
                if (isRawOperator(input.charAt(i))) {
                    count++;
                }
            }
            return count;
        }
    }
    
    /**
     * Check the input.
     *
     * @param input Input expression.
     *
     * @return true: right input; false: wrong input.
     */
    public static String checkInput(String input) {
        String message = "";
        if (input.isEmpty()) {
            message = "Input can not be empty!";
        } else {
            input = input.replace(" ", "");
            int size = input.length();
            for (int i = 0; i < size; i++) {
                char ch = input.charAt(i);
                if (!Utils.isLegalChar(ch)) {
                    message = "Error! Illegal char at position " + i;
                    break;
                }
                if (size - 1 == i) {
                    break;
                }
                char ch2 = input.charAt(i + 1);
                // Check separated char.
                if (!Utils.isLegalChar(ch2)) {
                    message = "Error! Illegal char at position" + (i + 1);
                    break;
                }
                // Allow 'pi', 'exp', '((', '))'
                if ('p' == ch && 'i' == ch2) {
                    continue;
                } else if (i < size - 2 && 'e' == ch && 'x' == ch2 && 'p' == input.charAt(i + 2)) {
                    continue;
                } else if ('(' == ch && '(' == ch2) {
                    continue;
                } else if (')' == ch && ')' == ch2) {
                    continue;
                } else if ('^' == ch && '^' != ch2 && '*' != ch2 && '/' != ch2 && '+' != ch2) {
                    continue;
                }

                // Disallow continuous letters or operators.
                if ((Utils.isLetter(ch) && Utils.isLetter(ch2)) ||
                        (Utils.isRawOperator(ch) && Utils.isRawOperator(ch2))) {
                    message = "Error! Input char at " + i + " and " + (i + 1) +
                            " are illegal!";
                    break;
                }
            }
        }
        return message;

    }
}
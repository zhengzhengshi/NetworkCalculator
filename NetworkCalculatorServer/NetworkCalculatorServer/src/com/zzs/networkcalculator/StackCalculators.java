package com.zzs.networkcalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.zzs.networkcalculator.Utils;

/**
 * Use stack to calculate the expression.
 */
public class StackCalculators {
    /**
     * Flag for continuous operator.
     */
    private boolean mContinuousOperator = false;

    /**
     * Get the string list of the expression.
     *
     * @param expression expression
     *
     * @return String list of the expression
     */
    private List<String> getStringList(final String expression) {
        List<String> result = new ArrayList<>();
        String temp = "";
        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            // Ignore space.
            if (' ' == ch) {
                continue;
            }
            if (!Utils.isOperatorOrBrackets(ch)) {
                temp = temp + expression.charAt(i);
            } else {
                if (!"".equals(temp)) {
                    result.add(temp);
                    // Handle ^(power)
                    checkToAddRightBracketsForPower(result);
                }
                // Handle ^(power)
                checkToAddBracketsForPower(result, ch);
                checkToAddZeroForPower(result, ch);
                result.add(ch + "");
                temp = "";
            }
        }
        if (temp != "") {
            result.add(temp);
            checkToAddRightBracketsForPower(result);
        }
        return result;
    }

    /**
     * When inputting negative power, we should add zero before it.
     *
     * @param result      result list
     * @param currentChar current char
     */
    private void checkToAddZeroForPower(List<String> result, char currentChar) {
        if ((result.isEmpty() || result.get(result.size() - 1).equals("(")) && '-' == currentChar) {
            result.add("0");
        }
    }

    /**
     * When inputting negative power, we should add brackets for it.
     *
     * @param result      result list
     * @param currentChar current char
     */
    private void checkToAddBracketsForPower(List<String> result, char currentChar) {
        if (!result.isEmpty()) {
            String lastString = result.get(result.size() - 1);
            if (Utils.isRawOperator(lastString) && Utils.isRawOperator(currentChar)) {
                result.add("(");
                mContinuousOperator = true;
            }
        }
    }

    /**
     * When inputting negative power, we should add brackets for it.
     *
     * @param result result list
     */
    private void checkToAddRightBracketsForPower(List<String> result) {
        if (mContinuousOperator) {
            result.add(")");
            mContinuousOperator = false;
        }
    }

    /**
     * Get the post order list of the expression.
     *
     * @param inOrderList The input list.
     *
     * @return The post order list.
     */
    private List<String> getPostOrder(List<String> inOrderList) {

        List<String> result = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        for (int i = 0; i < inOrderList.size(); i++) {
            char ch = inOrderList.get(i).charAt(0);
            if (!Utils.isOperatorOrBrackets(ch)) {
                result.add(inOrderList.get(i));
            } else {
                switch (ch) {
                    case '(':
                        stack.push(inOrderList.get(i));
                        break;
                    case ')':
                        while (!stack.peek().equals("(")) {
                            result.add(stack.pop());
                        }
                        stack.pop();
                        break;
                    default:
                        while (!stack.isEmpty() && compare(stack.peek(), inOrderList.get(i))) {
                            result.add(stack.pop());
                        }
                        stack.push(inOrderList.get(i));
                        break;
                }
            }
        }
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }
        return result;
    }


    /**
     * Calculate the expression.
     *
     * @param input       Input expression.
     * @param reduceCount Total reduce count.
     *
     * @return The calculate result.
     */
    public String calculate(String input) {
        // First, get all chars in the expression.
        List<String> sequenceOrder = getStringList(input);
        // Second, get the post order of the expression.
        List<String> postOrder = getPostOrder(sequenceOrder);
        // Use stack to calculate the expression.
        Stack<Operand> stack = new Stack<>();
        for (int i = 0; i < postOrder.size(); i++) {
            String str = postOrder.get(i);
            //Number
            if (!Utils.isOperatorOrBrackets(str)) {
                if (Utils.isNumeric(str)) {
                    int num = Integer.parseInt(str);
                    stack.push(new Operand(num));
                } else {
                    stack.push(new Operand(str));
                }
            } else {
                //Char
                Operand back = stack.pop();
                Operand front = stack.pop();
                Operand res = null;
                switch (str) {
                    case "+":
                        res = front.add(back);
                        break;
                    case "-":
                        res = front.sub(back);
                        break;
                    case "*":
                        res = front.multiple(back);
                        break;
                    case "/":
                        res = front.divide(back);
                        break;
                    case "^":
                        res = front.power(back);
                        break;
                }
                stack.push(res);
            }
        }
        return stack.pop().toString();
    }

    /**
     * Compare operator priority.
     *
     * @param peek Peek of the stack
     * @param cur  Current char
     *
     * @return true: Peek priority bigger than current priority; false: Peek priority smaller than
     * current priority;
     */
    private static boolean compare(String peek, String cur) {
        if ("^".equals(peek) && ("^".equals(cur) || "/".equals(cur) || "*".equals(cur) || "+"
                .equals(cur) || "-".equals(cur))) {
            return true;
        } else if ("*".equals(peek) && ("/".equals(cur) || "*".equals(cur) || "+".equals(cur) || "-"
                .equals(cur))) {
            return true;
        } else if ("/".equals(peek) && ("/".equals(cur) || "*".equals(cur) || "+".equals(cur) ||
                "-".equals(cur))) {
            return true;
        } else if ("+".equals(peek) && ("+".equals(cur) || "-".equals(cur))) {
            return true;
        } else if ("-".equals(peek) && ("+".equals(cur) || "-".equals(cur))) {
            return true;
        }
        return false;
    }
}

package com.zzs.networkcalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class for a factor.
 */
public class Factor {
    /** Factors. */
    private List<Integer> mSubFactors = new ArrayList<>();
    /** Variables in the factor. */
    private List<String> mSubVariables = new ArrayList<>();

    /**
     * Constructor.
     */
    public Factor() {

    }

    /**
     * Constructor with only one number.
     */
    public Factor(int num) {
        mSubFactors.add(num);
        mSubVariables.add("");
    }

    /**
     * Constructor with only one string.
     */
    public Factor(String variable) {
        if (Constants.INPUT_PI.equals(variable)) {
            mSubFactors.add(1);
            mSubVariables.add(Constants.PI);
        } else if (Constants.INPUT_EXP.equals(variable)) {
            mSubFactors.add(1);
            mSubVariables.add(Constants.EXP);
        }
    }

    /**
     * Add two factor.
     *
     * @param factor factor.
     *
     * @return Add result.
     */
    public Factor add(Factor factor) {
        for (int i = 0; i < factor.getSubVariables().size(); i++) {
            String argVariable = factor.getSubVariables().get(i);
            argVariable = sortString(argVariable);
            int argFactor = factor.getSubFactors().get(i);
            if (mSubVariables.contains(argVariable)) {
                int index = mSubVariables.indexOf(argVariable);
                int myFactor = mSubFactors.get(index);
                mSubFactors.set(index, myFactor + argFactor);
            } else {
                mSubFactors.add(argFactor);
                mSubVariables.add(argVariable);
            }
        }
        return this;
    }

    /**
     * Add negative symbol to the factor.
     */
    public void setNegatives() {
        for (int i = 0; i < getSubVariables().size(); i++) {
            int argFactor = getSubFactors().get(i);
            argFactor = -argFactor;
            getSubFactors().set(i, argFactor);
        }
    }

    /**
     * Multiple two factor.
     *
     * @param factorList
     * @param variableList
     * @param factorList2
     * @param variableList2
     *
     * @return Multiple result.
     */
    public static Factor multipleNonFraction(final List<Integer> factorList, final List<String>
            variableList, final List<Integer> factorList2, final List<String> variableList2) {
        if (null == factorList || null == variableList || null == factorList2 || null ==
                variableList2 || factorList.size() != variableList.size() || factorList2.size() !=
                variableList2.size()) {
            return null;
        }
        List<Factor> tempList = new ArrayList<>();

        for (int i = 0; i < variableList.size(); i++) {
            Factor temp = new Factor();
            // Deep copy.
            List<Integer> tempFactors = new ArrayList<>();
            List<String> tempVariables = new ArrayList<>();
            tempFactors.addAll(factorList2);
            tempVariables.addAll(variableList2);
            temp.setSubFactors(tempFactors);
            temp.setSubVariables(tempVariables);

            int argFactor = factorList.get(i);
            String argVariable = variableList.get(i);

            // Expand to multiple.
            for (int j = 0; j < temp.getSubVariables().size(); j++) {
                int myFactor = temp.getSubFactors().get(j);
                String myVariable = temp.getSubVariables().get(j);
                int newFactor = myFactor * argFactor;
                String newVariable = myVariable + argVariable;
                newVariable = sortString(newVariable);
                temp.getSubFactors().set(j, newFactor);
                temp.getSubVariables().set(j, newVariable);
            }
            tempList.add(temp);
        }
        // Add all parts.
        Factor firstOperand = tempList.get(0);
        for (int i = 1; i < tempList.size(); i++) {
            firstOperand = firstOperand.add(tempList.get(i));
        }
        return firstOperand;
    }

    /**
     * Is positive single number or variable.
     *
     * @return true: positive single; false: not positive single.
     */
    public boolean isSinglePositive() {
        return isSingle() && mSubFactors.get(0) > 0;
    }

    /**
     * Is single number or variable.
     *
     * @return true: single; false: not single.
     */
    public boolean isSingle() {
        return mSubFactors.size() == 1;
    }

    /**
     * Is one.
     *
     * @return true: 1; false: not 1.
     */
    public boolean isOne() {
        return 1 == mSubFactors.size() && mSubVariables.get(0).equals("") &&
                Math.abs(mSubFactors.get(0)) == 1;
    }

    /**
     * Is zero.
     *
     * @return true: 0; false: not 0.
     */
    public boolean isZero() {
        return 1 == mSubFactors.size() && mSubFactors.get(0) == 0;
    }

    /**
     * Is -1.
     *
     * @return true: -1; false: not -1;
     */
    public boolean isOneNegative() {
        return isOne() && mSubFactors.get(0) == -1;
    }

    public List<Integer> getSubFactors() {
        return mSubFactors;
    }

    public void setSubFactors(List<Integer> subFactors) {
        mSubFactors = subFactors;
    }

    public List<String> getSubVariables() {
        return mSubVariables;
    }

    public void setSubVariables(List<String> subVariables) {
        mSubVariables = subVariables;
    }

    private static String changeToPowerResult(String variable) {
        Map<String, Integer> variableMap = new HashMap<>();
        for (int i = 0; i < variable.length(); i++) {
            String subVariable = variable.substring(i, i + 1);
            if (variableMap.containsKey(subVariable)) {
                int pow = variableMap.get(subVariable);
                variableMap.put(subVariable, pow + 1);
            } else {
                variableMap.put(subVariable, 1);
            }
        }
        String powerResult = "";
        Set<String> keys = variableMap.keySet();
        for (String key : keys) {
            if (variableMap.get(key) > 1) {
                powerResult += key + "^" + variableMap.get(key);
            } else {
                powerResult += key;
            }
        }
        return powerResult;
    }

    @Override
    public String toString() {
        for (int i = 0; i < mSubVariables.size(); i++) {
            String powerVariable = changeToPowerResult(mSubVariables.get(i));
            mSubVariables.set(i, powerVariable);
        }
        String ret = "";
        for (int i = 0; i < mSubFactors.size(); i++) {
            if (mSubFactors.get(i) == 0) {
                // Ignore factor with 0.
                continue;
            }
            String operator = "";
            if (i != 0) {
                // Add '+'.
                if (mSubFactors.get(i) > 0) {
                    operator = "+";
                }
            }
            if (1 == Math.abs(mSubFactors.get(i)) && !mSubVariables.get(i).equals("")) {
                // Ignore 1.
                String negative = mSubFactors.get(i) < 0 ? "-" : "";
                ret += operator + negative + mSubVariables.get(i);
            } else {
                ret += operator + mSubFactors.get(i) + mSubVariables.get(i);
            }
        }
        if ("".equals(ret)) {
            ret = "0";
        }
        if (!isSingle()) {
            ret = "(" + ret + ")";
        }
        if (ret.startsWith("+")) {
            ret = ret.substring(1);
        }
        return ret;
    }

    /**
     * Sort.
     *
     * @param variable
     *
     * @return Sort result.
     */
    private static String sortString(String variable) {
        char[] sortArgVariableList = variable.toCharArray();
        Arrays.sort(sortArgVariableList);
        variable = new String(sortArgVariableList);
        return variable;
    }

    public boolean equals(Factor subFactor) {
        int variableSize = mSubVariables.size();
        int variableSize2 = subFactor.getSubVariables().size();
        int factorSize = mSubFactors.size();
        int factorSize2 = subFactor.getSubFactors().size();
        // Check if list sizes are the same.
        if (variableSize != variableSize2 || factorSize != factorSize2) {
            return false;
        }
        // Check if variables and factors are the same.
        for (int i = 0; i < variableSize; i++) {
            String variable = subFactor.getSubVariables().get(i);
            if (mSubVariables.contains(variable)) {
                int index = mSubVariables.indexOf(variable);
                int factorNum = mSubFactors.get(i);
                int factorNum2 = subFactor.getSubFactors().get(index);
                if (factorNum != factorNum2) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }
}

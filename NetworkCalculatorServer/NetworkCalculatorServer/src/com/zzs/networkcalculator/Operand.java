package com.zzs.networkcalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zzs.networkcalculator.Utils;

/**
 * Class for one operand.
 */
public class Operand {
    /** Whether reduce. */
    public static boolean sReduce = true;
    /** TAG. */
    private static final String TAG = Operand.class.getSimpleName();
    /** Factor list for numerator. */
    private List<Factor> mFactors = new ArrayList<>();
    /** Variable list for numerator. */
    private List<String> mVariables = new ArrayList<>();
    /** Factor list for denominator. */
    private List<Factor> mFactors2 = new ArrayList<>();
    /** Variable list for denominator. */
    private List<String> mVariables2 = new ArrayList<>();

    /**
     * Constructor.
     */
    public Operand() {
    }

    /**
     * Raw number constructor.
     *
     * @param num Number.
     */
    public Operand(int num) {
        mFactors.add(new Factor(num));
        mVariables.add("");
        mFactors2.add(new Factor(1));
        mVariables2.add("");
    }

    /**
     * Raw variable constructor.
     *
     * @param str variable.
     */
    public Operand(String str) {
        switch (str) {
            case "pi":
                mFactors.add(new Factor(str));
                mVariables.add("");
                mFactors2.add(new Factor(1));
                mVariables2.add("");
                break;
            case "exp":
                mFactors.add(new Factor(str));
                mVariables.add("");
                mFactors2.add(new Factor(1));
                mVariables2.add("");
                break;
            default:
                mFactors.add(new Factor(1));
                mVariables.add(str);
                mFactors2.add(new Factor(1));
                mVariables2.add("");
                break;
        }

    }

    /**
     * Multiple two factor and variable list.
     *
     * @param factorList
     * @param variableList
     * @param factorList2
     * @param variableList2
     *
     * @return Multiple result.
     */
    private static Operand multipleNonFraction(final List<Factor> factorList, final List<String>
            variableList, final List<Factor> factorList2, final List<String> variableList2) {
        if (null == factorList || null == variableList || null == factorList2 || null ==
                variableList2 || factorList.size() != variableList.size() || factorList2.size() !=
                variableList2.size()) {
            return null;
        }
        List<Operand> tempList = new ArrayList<>();

        for (int i = 0; i < variableList.size(); i++) {
            Operand temp = new Operand();
            // Deep copy.
            List<Factor> tempFactors = new ArrayList<>();
            List<String> tempVariables = new ArrayList<>();
            tempFactors.addAll(factorList2);
            tempVariables.addAll(variableList2);
            temp.setFactors(tempFactors);
            temp.setVariables(tempVariables);

            Factor argFactor = factorList.get(i);
            String argVariable = variableList.get(i);

            // Expand to multiple.
            for (int j = 0; j < temp.getVariables().size(); j++) {
                Factor myFactor = temp.getFactors().get(j);
                String myVariable = temp.getVariables().get(j);
                Factor newFactor = Factor.multipleNonFraction(myFactor.getSubFactors(), myFactor
                        .getSubVariables(), argFactor.getSubFactors(), argFactor.getSubVariables());
                String newVariable = myVariable + argVariable;
                newVariable = sortString(newVariable);
                temp.getFactors().set(j, newFactor);
                temp.getVariables().set(j, newVariable);
            }
            tempList.add(temp);
        }
        // Add all parts.
        Operand firstOperand = tempList.get(0);
        for (int i = 1; i < tempList.size(); i++) {
            firstOperand = firstOperand.addNonFraction(tempList.get(i));
        }

        return firstOperand;
    }

    /**
     * Multiple.
     *
     * @param operand multiplier
     *
     * @return Multiple result.
     */
    public Operand multiple(Operand operand) {
        // reduction

        // Multiple two numerators
        Operand molecule = multipleNonFraction(mFactors, mVariables,
                operand.getFactors(), operand.getVariables());
        mVariables = molecule.getVariables();
        mFactors = molecule.getFactors();
        // Multiple two denominator
        Operand denominator = multipleNonFraction(mFactors2, mVariables2,
                operand.getFactors2(), operand.getVariables2());
        mVariables2 = denominator.getVariables();
        mFactors2 = denominator.getFactors();

        // Reduce
        reduce(this);

        return this;
    }

    /**
     * Divide.
     *
     * @param operand Divisor
     *
     * @return Divide result
     * @throws IllegalArgumentException Throws then divisor is zero.
     */
    public Operand divide(Operand operand) throws IllegalArgumentException {
        if (isZero(operand)) {
        	System.out.print("divide, operand is 0!");
            throw new IllegalArgumentException("divide, operand is 0!");
        }
        multiple(operand.reciprocal());

        return this;
    }

    /**
     * Reduce one operand.
     *
     * @param operand Operand been reduced.
     */
    private void reduce(Operand operand) {
        if (sReduce) {
            // Reduction variables
            reduceVariable(operand);
            // Reduction factors
            reductionFactor(operand);
        }
    }

    /**
     * Reduce the variables.
     *
     * @param operand Operand
     */
    private void reduceVariable(Operand operand) {
        String common1 = Utils.maxSubstring(operand.getVariables());
        String common2 = Utils.maxSubstring(operand.getVariables2());
        String common3 = Utils.maxSubstring(common1, common2);
        if (common3 != null) {
            operand.setVariables(Utils.removeCommonVariable(operand.getVariables(), common3));
            operand.setVariables2(Utils.removeCommonVariable(operand.getVariables2(), common3));
        }
    }

    /**
     * Reduce the factor.
     *
     * @param oper
     */
    private void reductionFactor(Operand oper) {
        //Find all the pi and exp.
        String common1 = Utils.maxSubstring(getCommonInVariable(oper.getFactors()));
        String common2 = Utils.maxSubstring(getCommonInVariable(oper.getFactors2()));
        String common3 = Utils.maxSubstring(common1, common2);
        if (common3 != null) {
            for (Factor factor : oper.getFactors()) {
                factor.setSubVariables(Utils.removeCommonVariable(factor.getSubVariables(),
                        common3));
            }
            for (Factor factor : oper.getFactors2()) {
                factor.setSubVariables(Utils.removeCommonVariable(factor.getSubVariables(),
                        common3));
            }
        }
        // Get reduction factor.
        int factor1 = gcdFactor(oper.getFactors());
        int factor2 = gcdFactor(oper.getFactors2());
        int factor3 = Utils.gcd(factor1, factor2);
        if (Math.abs(factor3) != 1) {
            oper.setFactors(Utils.removeCommonFactor(oper.getFactors(), factor3));
            oper.setFactors2(Utils.removeCommonFactor(oper.getFactors2(), factor3));
        }
    }

    /**
     * Get the common factors.
     *
     * @param factorList
     *
     * @return
     */
    private List<String> getCommonInVariable(List<Factor> factorList) {
        List<String> tempCommonInVariableList = new ArrayList<>();
        for (Factor factor : factorList) {
            String commonInVariable = Utils.maxSubstring(factor.getSubVariables());
            tempCommonInVariableList.add(commonInVariable);
        }
        return tempCommonInVariableList;
    }

    /**
     * Get the greatest common divisor.
     *
     * @param list factor list.
     *
     * @return The greatest common divisor
     */
    private int gcdFactor(List<Factor> list) {
        int max = gcdSubFactor(list.get(0).getSubFactors());
        int size = list.size();
        if (size > 1) {
            for (int i = 0; i < size - 1; i++) {
                max = Utils.gcd(max, gcdSubFactor(list.get(i + 1).getSubFactors()));
            }
        }
        return max;
    }

    /**
     * Get the greatest common divisor in sub factor.
     *
     * @param list Sub factor.
     *
     * @return Get the greatest common divisor
     */
    private int gcdSubFactor(List<Integer> list) {

        int max = list.get(0);
        int size = list.size();
        if (size > 1) {
            for (int i = 0; i < size - 1; i++) {
                max = Utils.gcd(max, list.get(i + 1));
            }
        }
        return max;
    }

    /**
     * If the current operand is zero.
     *
     * @param operand operand
     *
     * @return true: is zero; false: not zero.
     */
    private static boolean isZero(Operand operand) {
        return 1 == operand.getFactors().size() && 0 == operand.getFactors().get(0).getSubFactors
                ().get(0);
    }

    /**
     * Power.
     *
     * @param operand
     *
     * @return Power result.
     * @throws IllegalArgumentException
     */
    public Operand power(Operand operand) throws IllegalArgumentException {
        // Operand must be a number.
        if (!isDigit(operand)) {
            throw new IllegalArgumentException("power, operand must be a number.");
        }
        int pow = operand.getFactors().get(0).getSubFactors().get(0);

        boolean negativePower = pow < 0;
        if (pow < 0) {
            pow = -1 * pow;
        } else if (0 == pow) {
            copyFrom(new Operand(1));
            return this;
        }
        Operand tmp = (Operand) (this.clone());

        for (int i = 0; i < pow - 1; i++) {
            multiple(tmp);
        }

        if (negativePower) {
            reciprocal();
        }

        // Reduce.
        reduce(this);
        return this;
    }

    /**
     * Get the reciprocal of current operand.
     */
    private Operand reciprocal() {
        List<Factor> factorList = mFactors;
        mFactors = mFactors2;
        mFactors2 = factorList;

        List<String> variableList = mVariables;
        mVariables = mVariables2;
        mVariables2 = variableList;
        return this;
    }

    /**
     * If the current operand is digit.
     *
     * @param operand operand
     *
     * @return true: is digit; false: not digit.
     */
    private static boolean isDigit(Operand operand) {
        return 1 == operand.getFactors().size() && "".equals(operand.getVariables().get(0)) &&
                1 == operand.getFactors2().size() && "".equals(operand.getVariables2().get(0));
    }

    /**
     * Add.
     *
     * @param operand operand.
     *
     * @return Add result.
     */
    public Operand add(Operand operand) {
        if (equals(mFactors2, mVariables2, operand.getFactors2(), operand.getVariables2())) {
            // Same denominator, add two numerators.
            addNonFraction(operand);
        } else {
            // Different denominator.
            Operand multipleRet1 = multipleNonFraction(mFactors2, mVariables2, operand.getFactors
                    (), operand.getVariables());
            Operand multipleRet2 = multipleNonFraction(mFactors, mVariables, operand.getFactors2
                    (), operand.getVariables2());
            Operand added = multipleRet1.addNonFraction(multipleRet2);
            mFactors = added.getFactors();
            mVariables = added.getVariables();
            // Multiple two denominator.
            Operand multipliedOperand = multipleNonFraction(mFactors2, mVariables2, operand
                    .getFactors2(), operand.getVariables2());

            mFactors2 = multipliedOperand.getFactors();
            mVariables2 = multipliedOperand.getVariables();
        }

        // Reduce.
        reduce(this);

        return this;
    }

    /**
     * Add without denominator.
     *
     * @param operand operand.
     *
     * @return Add result.
     */
    private Operand addNonFraction(Operand operand) {
        for (int i = 0; i < operand.getVariables().size(); i++) {
            String argVariable = operand.getVariables().get(i);
            argVariable = sortString(argVariable);
            Factor argFactor = operand.getFactors().get(i);

            if (mVariables.contains(argVariable)) {
                int index = mVariables.indexOf(argVariable);
                Factor myFactor = mFactors.get(index);
                myFactor = myFactor.add(argFactor);
                mFactors.set(index, myFactor);
            } else {
                mFactors.add(argFactor);
                mVariables.add(argVariable);
            }
        }
        return this;
    }

    /**
     * Sub
     *
     * @param operand operand.
     *
     * @return sub result.
     */
    public Operand sub(Operand operand) {
        for (int i = 0; i < operand.getVariables().size(); i++) {
            Factor argFactor = operand.getFactors().get(i);
            argFactor.setNegatives();
            operand.getFactors().set(i, argFactor);
        }
        this.add(operand);

        // Reduce.
        reduce(this);

        return this;
    }

    /**
     * Check whether two operand is equal.
     *
     * @param factorList
     * @param variableList
     * @param factorList2
     * @param variableList2
     *
     * @return true: equal; false not equal.
     */
    private static boolean equals(List<Factor> factorList, List<String> variableList,
                                  List<Factor> factorList2, List<String> variableList2) {
        int variableSize = variableList.size();
        int variableSize2 = variableList2.size();
        int factorSize = factorList.size();
        int factorSize2 = factorList2.size();
        // Check if list sizes are the same.
        if (variableSize != variableSize2 || factorSize != factorSize2) {
            // Log.e(TAG, "equals, size not the same");
            return false;
        }
        // Check if variables and factors are the same.
        for (int i = 0; i < variableSize; i++) {
            String variable = variableList.get(i);
            if (variableList2.contains(variable)) {
                int index = variableList2.indexOf(variable);
                Factor factor = factorList.get(i);
                Factor factor2 = factorList2.get(index);
                if (!factor.equals(factor2)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String numerator = toStringNonFraction(mFactors, mVariables);
        String denominator = toStringNonFraction(mFactors2, mVariables2);
        if ("1".equals(denominator)) {
            return numerator;
        } else {
            String numeratorString = mFactors.size() > 1 ? "(" + numerator + ")" : numerator;
            String denominatorString = mFactors2.size() > 1 ? "(" + denominator + ")" :
                    denominator;
            return numeratorString + "/" + denominatorString;
        }
    }

    /**
     * To string.
     *
     * @param factorList
     * @param variableList
     *
     * @return The string result.
     */
    public String toStringNonFraction(List<Factor> factorList, List<String> variableList) {
        // Deep copy.
        List<Factor> tempFactors = new ArrayList<>();
        List<String> tempVariables = new ArrayList<>();
        tempFactors.addAll(factorList);
        tempVariables.addAll(variableList);

        for (int i = 0; i < tempVariables.size(); i++) {
            String powerVariable = changeToPowerResult(tempVariables.get(i));
            tempVariables.set(i, powerVariable);
        }
        String ret = "";
        int factorSize = tempFactors.size();
        for (int i = 0; i < factorSize; i++) {
            Factor f = tempFactors.get(i);
            if (f.isZero()) {
                // Ignore factor with 0.
                continue;
            }
            String operator = (0 != i) && (f.isSinglePositive() || !f.isSingle()) ? "+" : "";
            if (f.isOne() && !tempVariables.get(i).equals("")) {
                // Ignore 1 if variable is not empty.
                String negative = f.isOneNegative() ? "-" : "";
                ret += operator + negative + tempVariables.get(i);
            } else {
                ret += operator + tempFactors.get(i) + tempVariables.get(i);
            }
        }
        if ("".equals(ret)) {
            ret = "0";
        }
        if (ret.startsWith("+")) {
            ret = ret.substring(1);
        }
        return ret;
    }

    /**
     * Change to power result.
     * For example aaa will return a^3.
     *
     * @param variable variable.
     *
     * @return variable with power.
     */
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
    protected Operand clone() {
        Operand operand = new Operand();
        for (int i = 0; i < this.mVariables.size(); i++) {
            operand.getVariables().add(this.getVariables().get(i));
            operand.getFactors().add(this.getFactors().get(i));
        }
        for (int i = 0; i < this.mVariables2.size(); i++) {
            operand.getVariables2().add(this.getVariables2().get(i));
            operand.getFactors2().add(this.getFactors2().get(i));
        }
        return operand;
    }

    private Operand copyFrom(Operand operand) {
        mFactors = operand.getFactors();
        mFactors2 = operand.getFactors2();
        mVariables = operand.getVariables();
        mVariables2 = operand.getVariables2();
        return this;
    }

    /**
     * Sort the string.
     *
     * @param variable variable.
     *
     * @return The sorted string.
     */
    private static String sortString(String variable) {
        char[] sortArgVariableList = variable.toCharArray();
        Arrays.sort(sortArgVariableList);
        variable = new String(sortArgVariableList);
        return variable;
    }

    /**
     * If current operand has denominator.
     *
     * @return true: has denominator; false: don't has denominator.
     */
    public boolean hasDenominator() {
        return !(1 == mFactors2.size() && 1 == mFactors2.get(0).getSubFactors().get(0) && ""
                .equals(mVariables2.get(0)));
    }

    public List<Factor> getFactors2() {
        return mFactors2;
    }

    public void setFactors2(List<Factor> factors2) {
        mFactors2 = factors2;
    }

    public List<String> getVariables2() {
        return mVariables2;
    }

    public void setVariables2(List<String> variables2) {
        mVariables2 = variables2;
    }

    public List<Factor> getFactors() {
        return mFactors;
    }

    public void setFactors(List<Factor> factors) {
        mFactors = factors;
    }

    public List<String> getVariables() {
        return mVariables;
    }

    public void setVariables(List<String> variables) {
        mVariables = variables;
    }
}

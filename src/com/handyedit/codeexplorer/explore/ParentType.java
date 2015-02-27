package com.handyedit.codeexplorer.explore;

/**
 * Method parent types.
 *
 * @author Alexei Orishchenko
 */
public enum ParentType {
    CLASS(0), PACKAGE(1), MODULE(2), PROJECT(3);

    private int _value;

    private ParentType(int value) {
        _value = value;
    }

    public int getValue() {
        return _value;
    }

    public static ParentType get(int value) {
        for (ParentType v: values()) {
            if (value == v.getValue()) {
                return v;
            }
        }
        return null;
    }
}

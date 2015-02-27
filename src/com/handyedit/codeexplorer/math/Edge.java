package com.handyedit.codeexplorer.math;

import org.jetbrains.annotations.NotNull;

/**
 * Graph edge with label.
 *
 * @author Alexei Orishchenko
 */
public class Edge<T> {

    private @NotNull T _from;
    private @NotNull T _to;

    private @NotNull String _label;

    public Edge() {
    }

    public Edge(@NotNull T from, @NotNull T to, String label) {
        _from = from;
        _to = to;
        setLabel(label);
    }

    public Edge(@NotNull T from, @NotNull T to) {
        this(from, to, "");
    }

    @NotNull
    public T getFrom() {
        return _from;
    }

    public void setFrom(@NotNull T from) {
        _from = from;
    }

    @NotNull
    public T getTo() {
        return _to;
    }

    public void setTo(@NotNull T to) {
        _to = to;
    }

    @NotNull
    public String getLabel() {
        return _label;
    }

    public void setLabel(String label) {
        _label = label != null ? label : "";
    }

    @Override
    public int hashCode() {
        return _from.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Edge) {
            Edge dep = (Edge) obj;
            return _from.equals(dep.getFrom()) && _to.equals(dep.getTo());
        }
        return false;
    }

    @Override
    public String toString() {
        return "Edge(" + _from + ", " + _to + ")";
    }

    public void reverse() {
        T temp = _from;
        _from = _to;
        _to = temp;
    }
}

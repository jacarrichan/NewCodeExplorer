package com.handyedit.codeexplorer.explore.filter;

import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Filters list.
 * Method is filtered if there is a filter from the list that filters the method.
 *
 * @author Alexei Orishchenko
 */
public class CompositeFilter extends MethodFilter {

    private List<MethodFilter> _filters = new ArrayList<MethodFilter>();

    public CompositeFilter() {
    }

    public CompositeFilter(List<MethodFilter> filters) {
        _filters = filters;
    }

    public void add(MethodFilter filter) {
        _filters.add(filter);
    }

    public void remove(MethodFilter filter) {
        _filters.remove(filter);
    }

    public List<MethodFilter> getFilters() {
        return _filters;
    }

    public int hashCode() {
        return _filters.size();
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof CompositeFilter) {
            return ((CompositeFilter) obj)._filters.equals(_filters);
        }
        return false;
    }

    public boolean match(PsiMethod node) {
        for (MethodFilter filter: _filters) {
            if (!filter.match(node)) {
                return false;
            }
        }
        return true;
    }

    public CompositeFilter copy() {
        return new CompositeFilter(_filters);
    }
}

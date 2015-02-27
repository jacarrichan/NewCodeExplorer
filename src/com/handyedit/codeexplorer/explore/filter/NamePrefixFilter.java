package com.handyedit.codeexplorer.explore.filter;

import com.handyedit.codeexplorer.res.CodeExplorerBundle;
import com.intellij.psi.PsiMethod;

/**
 * Filters methods by name prefix.
 *
 * @author Alexei Orishchenko
 */
public class NamePrefixFilter extends MethodFilter {

    private String _prefix;

    public NamePrefixFilter() {
    }

    public NamePrefixFilter(String prefix) {
        _prefix = prefix;
    }

    public boolean match(PsiMethod method) {
        return !method.getName().startsWith(_prefix);
    }

    public int hashCode() {
        return _prefix.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof NamePrefixFilter) {
            NamePrefixFilter other = (NamePrefixFilter) obj;
            return _prefix.equals(other._prefix);
        }
        return false;
    }

    public String toString() {
        return CodeExplorerBundle.message("dialog.filter.method-prefix", _prefix);
    }

    public String getPrefix() {
        return _prefix;
    }

    public void setPrefix(String prefix) {
        _prefix = prefix;
    }
}

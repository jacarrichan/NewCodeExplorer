package com.handyedit.codeexplorer.explore.filter;

import com.handyedit.codeexplorer.explore.MethodParent;
import com.handyedit.codeexplorer.explore.ParentFactory;
import com.handyedit.codeexplorer.explore.ParentType;
import com.handyedit.codeexplorer.res.CodeExplorerBundle;
import com.intellij.psi.PsiMethod;

/**
 * Filters methods that belong to specified unit (class, package or module).
 *
 * @author Alexei Orishchenko
 */
public class ScopeFilter extends MethodFilter {
    private String _name;
    private ParentType _type;

    public ScopeFilter() {
    }

    public ScopeFilter(String name, ParentType type) {
        _name = name;
        _type = type;
    }

    public boolean match(PsiMethod method) {
        MethodParent parent = ParentFactory.getInstance().getParent(method, _type);
        return parent == null || !_name.equals(parent.getQualifiedName());
    }

    public int hashCode() {
        return _name.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ScopeFilter) {
            ScopeFilter other = (ScopeFilter) obj;
            return other._name.equals(_name) && other._type.equals(_type);
        }
        return false;
    }

    public String toString() {
        String key = null;
        if (_type == ParentType.CLASS) {
            key = "dialog.filter.class";
        }
        if (_type == ParentType.PACKAGE) {
            key = "dialog.filter.package";
        }
        if (_type == ParentType.MODULE) {
            key = "dialog.filter.module";
        }

        if (key != null) {
            return CodeExplorerBundle.message(key, _name);
        }
        return "Unknown";
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public ParentType getType() {
        return _type;
    }

    public void setType(ParentType type) {
        _type = type;
    }
}

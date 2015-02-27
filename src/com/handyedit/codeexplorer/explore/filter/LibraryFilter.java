package com.handyedit.codeexplorer.explore.filter;

import com.handyedit.codeexplorer.res.CodeExplorerBundle;
import com.handyedit.codeexplorer.util.PsiUtils;
import com.intellij.psi.PsiMethod;

/**
 * Filters library methods (from class outside project sources).
 *
 * @author Alexei Orishchenko
 */
public class LibraryFilter extends MethodFilter {

    public static LibraryFilter INSTANCE = new LibraryFilter();

    public LibraryFilter() {
    }

    public boolean match(PsiMethod node) {
        return !PsiUtils.isLibraryMethod(node);
    }

    public int hashCode() {
        return 0;
    }

    public boolean equals(Object obj) {
        return obj != null && obj instanceof LibraryFilter;
    }

    public String toString() {
        return CodeExplorerBundle.message("dialog.filter.library");
    }
}

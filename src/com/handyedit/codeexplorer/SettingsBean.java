package com.handyedit.codeexplorer;

import com.handyedit.codeexplorer.explore.filter.LibraryFilter;
import com.handyedit.codeexplorer.explore.filter.MethodFilter;
import com.handyedit.codeexplorer.explore.filter.NamePrefixFilter;
import com.handyedit.codeexplorer.explore.filter.ScopeFilter;
import com.intellij.util.xmlb.annotations.AbstractCollection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexei Orishchenko
 */
public class SettingsBean {

    private List<MethodFilter> _filters = new ArrayList<MethodFilter>();
    private boolean _isShowMethodBody;

    public SettingsBean() {
    }

    public SettingsBean(List<MethodFilter> filters, boolean showMethodBody) {
        _filters = filters;
        _isShowMethodBody = showMethodBody;
    }

    @AbstractCollection(elementTypes = { LibraryFilter.class, ScopeFilter.class, NamePrefixFilter.class })
    public List<MethodFilter> getFilters() {
        return _filters;
    }

    public void setFilters(List<MethodFilter> filters) {
        _filters = filters;
    }

    public boolean isShowMethodBody() {
        return _isShowMethodBody;
    }

    public void setShowMethodBody(boolean showMethodBody) {
        _isShowMethodBody = showMethodBody;
    }
}

package com.handyedit.codeexplorer.ui.dialog;

import com.handyedit.codeexplorer.explore.filter.CompositeFilter;
import com.handyedit.codeexplorer.explore.filter.LibraryFilter;
import com.handyedit.codeexplorer.explore.filter.MethodFilter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Filters model.
 *
 * @author Alexei Orishchenko
 */
class FiltersListModel extends AbstractListModel {

    private List<MethodFilter> _items = new ArrayList<MethodFilter>();

    FiltersListModel(List<MethodFilter> items) {
        _items.addAll(items);
    }

    public int getSize() {
        return _items.size();
    }

    public Object getElementAt(int index) {
        return _items.get(index);
    }

    public boolean add(MethodFilter filter) {
        return add(filter, _items.size());
    }

    public boolean add(MethodFilter filter, int index) {
        if (_items.contains(filter)) {
            return false;
        }
        _items.add(index, filter);
        fireIntervalAdded(this, index, index);
        return true;
    }

    public void remove(int first, int last) {
        for (int i = last; i >= first; i--) {
            _items.remove(i);
        }
        fireIntervalRemoved(this, first, last);
    }

    public CompositeFilter getFilter() {
        return new CompositeFilter(_items);
    }

    public boolean containsLibraryFilter() {
        for (MethodFilter filter: _items) {
            if (filter instanceof LibraryFilter) {
                return true;
            }
        }
        return false;
    }
}

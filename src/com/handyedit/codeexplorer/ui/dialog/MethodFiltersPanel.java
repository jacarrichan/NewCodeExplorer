package com.handyedit.codeexplorer.ui.dialog;

import com.handyedit.codeexplorer.explore.MethodParent;
import com.handyedit.codeexplorer.explore.filter.*;
import com.handyedit.codeexplorer.res.CodeExplorerBundle;
import com.handyedit.codeexplorer.util.LayoutUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Fitlers panel for settings dialog.
 *
 * @author Alexei Orishchenko
 */
public class MethodFiltersPanel extends JPanel {

    private Project _project;

    private JList _list = new JList();
    private JButton _filterLibButton;
    private JButton _removeButton;
    private static final int BUTTON_PADDING = 5;

    public MethodFiltersPanel(Project project) {
        _project = project;

        setBorder(BorderFactory.createTitledBorder(CodeExplorerBundle.message("dialog.filter.exclude")));

        setLayout(new BorderLayout());
        add(new JScrollPane(_list), BorderLayout.CENTER);
        add(createButtons(), BorderLayout.EAST);

        _list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    _removeButton.setEnabled(!_list.getSelectionModel().isSelectionEmpty());
                }
            }
        });
        _list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    }

    private JPanel createButtons() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        JButton addScopeButton = new JButton(CodeExplorerBundle.message("dialog.filter.add-scope"));
        JButton addNameButton = new JButton(CodeExplorerBundle.message("dialog.filter.add-name"));
        _filterLibButton = new JButton(CodeExplorerBundle.message("dialog.filter.add-lib"));
        _removeButton = new JButton(CodeExplorerBundle.message("dialog.filter.remove"));

        panel.add(addScopeButton, createConstraint(0));
        panel.add(addNameButton, createConstraint(1));
        panel.add(_filterLibButton, createConstraint(2));
        panel.add(_removeButton, createConstraint(3, true));

        addScopeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addScopeFilter();
            }
        });

        addNameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addNameFilter();
            }
        });

        _filterLibButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addLibraryFilter();
            }
        });

        _removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeFilter();
            }
        });

        return panel;
    }

    private GridBagConstraints createConstraint(int col) {
        return createConstraint(col, false);
    }

    private GridBagConstraints createConstraint(int col, boolean last) {
        if (last) {
            return LayoutUtils.create(0, col, true, true, true, false, new Insets(0, BUTTON_PADDING, BUTTON_PADDING, BUTTON_PADDING));
        }
        return LayoutUtils.create(0, col, true, new Insets(0, BUTTON_PADDING, BUTTON_PADDING, BUTTON_PADDING));
    }

    private void addScopeFilter() {
        SelectScopeDialog scopeDialog = new SelectScopeDialog(_project);
        scopeDialog.show();
        MethodParent selected = scopeDialog.getScope(_project);
        if (selected != null) {
            addFilter(new ScopeFilter(selected.getQualifiedName(), selected.getType()));
        }
    }

    private void addLibraryFilter() {
        FiltersListModel model = (FiltersListModel) _list.getModel();
        model.add(LibraryFilter.INSTANCE, 0);
        _list.setSelectedIndex(0);
        _filterLibButton.setEnabled(false);
    }

    private void addNameFilter() {
        String name = Messages.showInputDialog(_project,
                CodeExplorerBundle.message("dialog.filter.prefix.text"),
                CodeExplorerBundle.message("dialog.filter.prefix.title"), null);
        if (name != null && !"".equals(name)) {
            addFilter(new NamePrefixFilter(name));
        }
    }

    private void addFilter(MethodFilter filter) {
        FiltersListModel model = (FiltersListModel) _list.getModel();
        if (model.add(filter)) {
            _list.setSelectedIndex(model.getSize() - 1);
        }
    }

    private void removeFilter() {
        ListSelectionModel sm = _list.getSelectionModel();
        int newSelection = sm.getMinSelectionIndex();
        if (!sm.isSelectionEmpty()) {
            FiltersListModel model = (FiltersListModel) _list.getModel();
            model.remove(sm.getMinSelectionIndex(), sm.getMaxSelectionIndex());
            int size = model.getSize();
            if (size > 0) {
                if (newSelection >= size) {
                    newSelection = size - 1;
                }
                _list.setSelectedIndex(newSelection);
            }
        }
        updateFilterButton((FiltersListModel) _list.getModel());
    }

    public CompositeFilter getFilter() {
        FiltersListModel model = (FiltersListModel) _list.getModel();
        return model.getFilter();
    }

    public void setFilter(CompositeFilter filter) {
        FiltersListModel model = new FiltersListModel(filter.getFilters());
        _list.setModel(model);
        updateFilterButton(model);
    }

    private void updateFilterButton(FiltersListModel model) {
        _filterLibButton.setEnabled(!model.containsLibraryFilter());
    }
}

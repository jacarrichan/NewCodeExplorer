package com.handyedit.codeexplorer.ui.dialog;

import com.handyedit.codeexplorer.explore.MethodParent;
import com.handyedit.codeexplorer.explore.ParentFactory;
import com.handyedit.codeexplorer.res.CodeExplorerBundle;
import com.handyedit.codeexplorer.util.LayoutUtils;
import com.handyedit.codeexplorer.util.PsiUtils;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.util.ClassUtil;
import com.intellij.refactoring.ui.ClassNameReferenceEditor;
import com.intellij.refactoring.ui.PackageNameReferenceEditorCombo;
import com.intellij.ui.JBColor;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Dialog to select method parent (class, package or module).
 *
 * @author Alexei Orishchenko
 */
public class SelectScopeDialog extends DialogWrapper {

    private static final int PAD = 10;

    private JPanel _panel;

    private Collection<JComponent> _choosers = new ArrayList<JComponent>();
    private JComponent _packageChooser;

    private Set<String> _modules;
    private Project _project;
    private Map<JComponent, ScopeGetter> _scopeGetters = new HashMap<JComponent, ScopeGetter>();

    public SelectScopeDialog(Project project) {
        super(project, true);
        _modules = PsiUtils.getModules(project);
        _project = project;

        setTitle(CodeExplorerBundle.message("dialog.call-chains-from.title"));

        init();
    }

    protected JComponent createCenterPanel() {
        _panel = new JPanel();
        _panel.setLayout(new GridBagLayout());

        _panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(JBColor.GRAY), BorderFactory.createEmptyBorder(PAD, PAD, PAD, PAD)));

        _panel.add(new JLabel(CodeExplorerBundle.message("dialog.call-chains-from.text")), createConstraints(0, false, 0));

//      TODO  ClassNameReferenceEditor classChooser = new ClassNameReferenceEditor(PsiManager.getInstance(_project), null);
        ClassNameReferenceEditor classChooser = new ClassNameReferenceEditor(_project, null);
        PackageNameReferenceEditorCombo packageChooser = new PackageNameReferenceEditorCombo("", _project, null,
                CodeExplorerBundle.message("dialog.choose-package"));
        JComboBox moduleCombo = new JComboBox(_modules.toArray());

        _packageChooser = packageChooser;

        setChooserSize(classChooser);

        DocumentListener l = new DocumentAdapter() {
            public void documentChanged(DocumentEvent event) {
                updateOkButton();
            }
        };

        classChooser.addDocumentListener(l);

        _choosers.add(classChooser);
        _choosers.add(packageChooser);
        _choosers.add(moduleCombo);

        _scopeGetters.put(classChooser, new ClassGetter());
        _scopeGetters.put(packageChooser, new PackageGetter());
        _scopeGetters.put(moduleCombo, new ModuleGetter());

        addChoosers(CodeExplorerBundle.message("scope.class"), CodeExplorerBundle.message("scope.package"), CodeExplorerBundle.message("scope.module"));
        updateOkButton();

        return _panel;
    }

    private static void setChooserSize(ClassNameReferenceEditor classChooser) {
        Dimension size = classChooser.getPreferredSize();
        size.width = classChooser.getFontMetrics(classChooser.getFont()).charWidth('a') * 50;
        classChooser.setPreferredSize(size);
    }

    private void addChoosers(String... labels) {
        int i = 0;

        ButtonGroup group = new ButtonGroup();
        for (final JComponent chooser : _choosers) {
            String label = labels[i];
            JRadioButton button = new JRadioButton(label);
            group.add(button);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    enableChoosers(chooser);
                    updateOkButton();
                }
            });
            int row = 2 * i + 1;
            _panel.add(button, createConstraints(row, false, PAD));
            _panel.add(chooser, createConstraints(row + 1, true, 0));

            if (i == 0) {
                button.setSelected(true);
                enableChoosers(chooser);
            }

            i++;
        }
    }

    private void updateOkButton() {
        JComponent chooser = getActiveChooser();
        ScopeGetter getter = _scopeGetters.get(chooser);
        boolean enabled = _packageChooser.equals(chooser) || !StringUtils.isEmpty(getter.getScopeName(chooser));

        getOKAction().setEnabled(enabled);
    }

    private GridBagConstraints createConstraints(int col, boolean fill, int topPadding) {
        return LayoutUtils.create(0, col, fill, new Insets(topPadding, 0, 0, 0));
    }

    private void enableChoosers(JComponent active) {
        for (JComponent c : _choosers) {
            c.setEnabled(c.equals(active));
        }
    }

    private JComponent getActiveChooser() {
        for (JComponent chooser : _choosers) {
            if (chooser.isEnabled()) {
                return chooser;
            }
        }
        return null;
    }

    public MethodParent getScope(Project project) {
        if (!isOK()) {
            return null;
        }

        JComponent chooser = getActiveChooser();
        ScopeGetter getter = _scopeGetters.get(chooser);
        return getter.getScope(project, chooser);
    }

    private interface ScopeGetter<Comp extends JComponent> {
        MethodParent getScope(Project project, Comp chooser);

        String getScopeName(Comp chooser);
    }

    private class ClassGetter implements ScopeGetter<ClassNameReferenceEditor> {
        public MethodParent getScope(Project project, ClassNameReferenceEditor comp) {
            String name = getScopeName(comp);
            if (!StringUtils.isEmpty(name)) {
                PsiClass aClass = ClassUtil.findPsiClass(PsiManager.getInstance(project), name);
                return ParentFactory.getInstance().create(aClass);
            }
            return null;
        }

        public String getScopeName(ClassNameReferenceEditor comp) {
            return comp.getChildComponent().getText();
        }
    }

    private class PackageGetter implements ScopeGetter<PackageNameReferenceEditorCombo> {
        public MethodParent getScope(Project project, PackageNameReferenceEditorCombo comp) {
            String name = getScopeName(comp);
            if (name != null) {
                PsiPackage aPackage = JavaPsiFacade.getInstance(project).findPackage(name);
                return ParentFactory.getInstance().create(aPackage);
            }
            return null;
        }

        public String getScopeName(PackageNameReferenceEditorCombo comp) {
            return comp.getChildComponent().getText();
        }
    }

    private class ModuleGetter implements ScopeGetter<JComboBox> {
        public MethodParent getScope(Project project, JComboBox comp) {
            String name = getScopeName(comp);
            if (name != null) {
                Module module = ModuleManager.getInstance(project).findModuleByName(name);
                return ParentFactory.getInstance().create(module);
            }
            return null;
        }

        public String getScopeName(JComboBox comp) {
            return (String) comp.getSelectedItem();
        }
    }
}

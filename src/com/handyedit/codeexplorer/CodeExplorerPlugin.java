package com.handyedit.codeexplorer;

import com.handyedit.codeexplorer.explore.MethodExplorer;
import com.handyedit.codeexplorer.explore.filter.CompositeFilter;
import com.handyedit.codeexplorer.explore.filter.LibraryFilter;
import com.handyedit.codeexplorer.explore.filter.MethodFilter;
import com.handyedit.codeexplorer.math.Subgraph;
import com.handyedit.codeexplorer.model.DependencyModel;
import com.handyedit.codeexplorer.model.MethodNode;
import com.handyedit.codeexplorer.res.CodeExplorerBundle;
import com.handyedit.codeexplorer.ui.dialog.MethodFiltersPanel;
import com.handyedit.codeexplorer.ui.graph.DiagramComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.Icons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

@State(
  name = "CodeExplorer",
  storages = {
    @Storage(
      id ="CodeExplorer",
      file = "$WORKSPACE_FILE$"
    )
    }
)
public class CodeExplorerPlugin implements ProjectComponent, Configurable, PersistentStateComponent<SettingsBean> {

    private static final String COMPONENT_ID = "CodeExplorer";
    private static final String WINDOW_ID = "Code explorer";

    private Project _project;

    private CodeExplorerSettings _settings = new CodeExplorerSettings();

    private CompositeFilter _filter;

    private MethodFiltersPanel _filtersPanel;

    public CodeExplorerPlugin(Project project) {
        _project = project;
    }

    @NotNull
    public String getComponentName() {
        return COMPONENT_ID;
    }

    public void initComponent() {
        StartupManager.getInstance(_project).registerPostStartupActivity(new Runnable() {
            public void run() {
                registerToolWindow();
            }
        });
    }

    private void registerToolWindow() {
        ToolWindow window = ToolWindowManager.getInstance(_project).registerToolWindow(
                WINDOW_ID, false, ToolWindowAnchor.RIGHT);
        window.hide(null);
        window.setAvailable(false, null);
        window.setIcon(Icons.METHOD_ICON);
    }

    private DiagramComponent createDiagram(String title) {
        DiagramComponent diagram = DiagramComponent.create(_project);
        Content content = ContentFactory.SERVICE.getInstance().createContent(diagram.getComponent(), title, true);

        ToolWindow window = ToolWindowManager.getInstance(_project).getToolWindow(WINDOW_ID);
        window.getContentManager().addContent(content);
        window.setAvailable(true, null);

        Disposer.register(content, diagram);

        window.getContentManager().setSelectedContent(content);

        return diagram;
    }

    private DiagramComponent addDiagram(String title) {
        return createDiagram(title);
    }

    /**
     * Opens the graph in new CodeExplorer tab.
     * called from UI thread: AnAction.actionPerformed()
     *
     * @param deps graph to show in the tab
     * @param exploredMethod method with explored structure
     * @param select method to select
     * @param title tab title
     */
    public void openGraph(Subgraph<PsiMethod> deps, PsiMethod exploredMethod, PsiMethod select, String title) {
        title = getTitle(exploredMethod, title);
        DiagramComponent diagram = addDiagram(title);
        DependencyModel model = diagram.getModel();

        Subgraph<MethodNode> nodes = MethodNode.getNodesGraph(deps);
        model.addDependencies(nodes, MethodNode.create(exploredMethod));

        diagram.updateGraphNow();
        if (select == null) {
            select = exploredMethod;
        }
        if (select != null) {
            diagram.select(MethodNode.create(select));
        }

        ToolWindow window = ToolWindowManager.getInstance(_project).getToolWindow(WINDOW_ID);
        window.show(null);
    }

    private String getTitle(PsiMethod exploredMethod, String title) {
        if (title == null) {
            if (exploredMethod != null) {
                title = exploredMethod.getName();
            }
            if (title == null) {
                title = CodeExplorerBundle.message("def-tab-name");
            }
        }
        return title;
    }

    public void closeActiveTab() {
        ToolWindow window = ToolWindowManager.getInstance(_project).getToolWindow(WINDOW_ID);
        ContentManager contentManager = window.getContentManager();
        Content selected = contentManager.getSelectedContent();
        if (selected != null) {
            contentManager.removeContent(selected, true);
        }
        if (contentManager.getContentCount() == 0) {
            window.setAvailable(false, null);
            window.hide(null);
        }
    }

    public void disposeComponent() {
        ToolWindowManager.getInstance(_project).unregisterToolWindow(WINDOW_ID);
    }

    public void projectOpened() {
    }

    public void projectClosed() {
        ToolWindow window = ToolWindowManager.getInstance(_project).getToolWindow(WINDOW_ID);
        window.setAvailable(false, null);
        window.getContentManager().removeAllContents(true);
    }

    public static CodeExplorerSettings getSettings(Project project) {
        return getInstance(project)._settings;
    }

    public static CodeExplorerPlugin getInstance(Project project) {
        return project.getComponent(CodeExplorerPlugin.class);
    }

    @Nls
    public String getDisplayName() {
        return "CodeExplorer";
    }

    public Icon getIcon() {
        return Icons.METHOD_ICON;
    }

    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        if (_filtersPanel == null) {
            _filtersPanel = new MethodFiltersPanel(_project);
        }
        _filtersPanel.setFilter(getFilter());
        return _filtersPanel;
    }

    public boolean isModified() {
        return !_filtersPanel.getFilter().equals(getFilter());
    }

    public void apply() throws ConfigurationException {
        _filter = _filtersPanel.getFilter();
    }

    public void reset() {
        _filtersPanel.setFilter(getFilter());
    }

    public void disposeUIResources() {
    }

    public CompositeFilter getFilter() {
        if (_filter == null) {
            _filter = new CompositeFilter();
            _filter.add(LibraryFilter.INSTANCE);
        }
        return _filter;
    }

    public static MethodExplorer getExplorer(PsiMethod method) {
        CompositeFilter filter = getInstance(method.getProject()).getFilter();
        return MethodExplorer.getInstance(filter);
    }

    public SettingsBean getState() {
        return new SettingsBean(getFilter().getFilters(), _settings.isShowMethodBody());
    }

    public void loadState(SettingsBean bean) {
        List<MethodFilter> filters = bean.getFilters();
        _filter = new CompositeFilter(filters);
        _settings.setShowMethodBody(bean.isShowMethodBody());
    }
}

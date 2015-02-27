package com.handyedit.codeexplorer.ui.action.graph;

import com.intellij.openapi.project.Project;

/**
 * Adds calls from selected method to the graph.
 *
 * @author Alexei Orishchenko
 */
public class ShowStructureAction extends ShowDependenciesAction {

    public static final String ACTION_ID = "CodeExplorer.ShowStructure";

    protected boolean isStructure(Project project) {
        return true;
    }
}

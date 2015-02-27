package com.handyedit.codeexplorer.ui.action.graph;

import com.intellij.openapi.project.Project;

/**
 * Adds selected method usages to the graph.
 *
 * @author Alexei Orishchenko
 */
public class ShowUsagesAction extends ShowDependenciesAction {

    public static final String ACTION_ID = "CodeExplorer.ShowUsages";
    
    protected boolean isStructure(Project project) {
        return false;
    }
}

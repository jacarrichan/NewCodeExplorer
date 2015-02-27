package com.handyedit.codeexplorer.explore;

import com.handyedit.codeexplorer.util.PsiUtils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;

import java.util.Collections;
import java.util.Set;

/**
 * Method parent factory.
 * Creates method parent instance by PsiClass, PsiPackage, Module or Project
 * or by method and parent type.
 *
 * @author Alexei Orishchenko
 */
public class ParentFactory {

    private static final ParentFactory INSTANCE = new ParentFactory();

    public static ParentFactory getInstance() {
        return INSTANCE;
    }

    public MethodParent create(PsiClass aClass) {
        return aClass != null ? new PClass(aClass) : null;
    }

    public MethodParent create(PsiPackage aPackage) {
        return aPackage != null ? new PPackage(aPackage) : null;
    }

    public MethodParent create(Module module) {
        return module != null ? new PModule(module) : null;
    }

    public MethodParent getParent(PsiMethod method, ParentType type) {
        if (method == null) {
            return null;
        }
        if (type == null) {
            return createProject(method);
        }

        switch (type) {
            case CLASS:
                return createClass(method);
            case PACKAGE:
                return createPackage(method);
            case MODULE:
                return createModule(method);
            default:
                return createProject(method);
        }
    }

    public MethodParent create(Project project) {
        return project != null ? new PProject(project) : null;
    }

    public MethodParent createClass(PsiMethod method) {
        return create(getParentClass(method));
    }

    public MethodParent createPackage(PsiMethod method) {
        return create(getParentPackage(method));
    }

    public MethodParent createModule(PsiMethod method) {
        return create(getParentModule(method));
    }

    public MethodParent createProject(PsiMethod method) {
        return create(method != null ? method.getProject() : null);
    }

    private static PsiClass getParentClass(PsiMethod method) {
        return method != null ? method.getContainingClass() : null;
    }

    private static PsiPackage getParentPackage(PsiMethod method) {
        return method != null ? PsiUtils.getPackage(method) : null;
    }

    private static Module getParentModule(PsiMethod method) {
        return method != null ? PsiUtils.getModule(method) : null;
    }

    private static class PClass implements MethodParent {
        private PsiClass _class;

        private PClass(PsiClass aClass) {
            _class = aClass;
        }

        public String getName() {
            return _class.getName();
        }

        public String getQualifiedName() {
            return _class.getQualifiedName();
        }

        public String getKey() {
            return "class";
        }

        public Set<PsiClass> getClasses() {
            return Collections.singleton(_class);
        }

        public boolean contains(PsiMethod method) {
            return _class.equals(getParentClass(method));
        }

        public ParentType getType() {
            return ParentType.CLASS;
        }
    }

    private static class PPackage implements MethodParent {
        private PsiPackage _package;

        private PPackage(PsiPackage aPackage) {
            _package = aPackage;
        }

        public String getName() {
            return _package.getQualifiedName();
        }

        public String getQualifiedName() {
            return getName();
        }

        public String getKey() {
            return "package";
        }

        public Set<PsiClass> getClasses() {
            return PsiUtils.getClasses(_package);
        }

        public boolean contains(PsiMethod method) {
            return _package.equals(getParentPackage(method));
        }

        public ParentType getType() {
            return ParentType.PACKAGE;
        }
    }

    private static class PModule implements MethodParent {
        private Module _module;

        private PModule(Module module) {
            _module = module;
        }

        public String getName() {
            return _module.getName();
        }

        public String getQualifiedName() {
            return getName();
        }

        public String getKey() {
            return "module";
        }

        public Set<PsiClass> getClasses() {
            return PsiUtils.getClasses(_module);
        }

        public boolean contains(PsiMethod method) {
            return _module.equals(getParentModule(method));
        }

        public ParentType getType() {
            return ParentType.MODULE;
        }
    }

    private static class PProject implements MethodParent {

        private Project _project;

        private PProject(Project project) {
            _project = project;
        }

        public String getName() {
            return "project";
        }

        public String getQualifiedName() {
            return getName();
        }

        public String getKey() {
            return "project";
        }

        public Set<PsiClass> getClasses() {
            return PsiUtils.getClasses(_project);
        }

        public boolean contains(PsiMethod method) {
            return true;
        }

        public ParentType getType() {
            return ParentType.PROJECT;
        }
    }
}

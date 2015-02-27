package com.handyedit.codeexplorer.util;

import com.handyedit.codeexplorer.explore.filter.MethodFilter;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.MethodReferencesSearch;
import com.intellij.psi.search.searches.OverridingMethodsSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.Query;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * PSI utility methods.
 *
 * @author Alexei Orishchenko
 */
public class PsiUtils {

    /**
     * Package name of Java file with specified element, or null if the element isn't in Java file.
     *
     * @param elem PSI element
     * @return package name of Java file with specified element
     */
    public static PsiPackage getPackage(PsiElement elem) {
        PsiFile file = elem.getContainingFile();
        if (file == null || !(file instanceof PsiJavaFile)) {
            return null;
        }
        PsiJavaFile javaFile = (PsiJavaFile) file;
        return JavaPsiFacade.getInstance(elem.getProject()).findPackage(javaFile.getPackageName());
    }

    /**
     * Returns class name for non-anonymous class or base class name for anonymous class.
     *
     * @param aClass PSI class
     * @param showClass full name if true else camel word
     * @return class name for non-anonymous class or base class name for anonymous class
     */
    public static String getClassName(PsiClass aClass, boolean showClass) {
        String name;
        if (aClass instanceof PsiAnonymousClass) {
            PsiAnonymousClass anonymous = (PsiAnonymousClass) aClass;
            name = anonymous.getBaseClassType().getClassName();
            name = getShortName(name, showClass);
            PsiMethod parentMethod = getParentMethod(anonymous);
            if (parentMethod != null) {
                name = parentMethod.getName() + "(): " + name;
            }
        } else {
            name = aClass.getName();
            name = getShortName(name, showClass);
        }
        PsiClass parent = getParent(aClass);
        if (parent != null) {
            name = getClassName(parent, showClass) + "." + name;
        }
        return name;
    }

    private static String getShortName(String name, boolean showClass) {
        if (!showClass) {
            return com.handyedit.codeexplorer.util.StringUtils.getCamelWord(name);
        }
        return name;
    }

    private static PsiMethod getParentMethod(PsiElement elem) {
        elem = PsiTreeUtil.getParentOfType(elem, PsiMethod.class, PsiClass.class);
        return elem instanceof PsiMethod ? (PsiMethod) elem : null;
    }

    public static PsiClass getParent(PsiClass aClass) {
        if (aClass == null) {
            return null;
        }
        if (aClass instanceof PsiAnonymousClass) {
            return PsiTreeUtil.getParentOfType(aClass, PsiClass.class);
        }
        return aClass.getContainingClass();
    }

    /**
     * Opens file with specified element in IDE editor.
     *
     * @param elem PSI element
     * @return file editor for opened file
     */
    private static Editor openEditor(PsiElement elem) {
        if (elem == null) {
            return null;
        }

        PsiFile psiFile = elem.getContainingFile();
        if (psiFile == null) {
            return null;
        }
        VirtualFile file = psiFile.getVirtualFile();
        if (file == null) {
            return null;
        }
        OpenFileDescriptor descriptor = new OpenFileDescriptor(elem.getProject(), file);
        return FileEditorManager.getInstance(elem.getProject()).openTextEditor(descriptor, true);
    }

    /**
     * Opens editor with specified element and scrolls to the element.
     *
     * @param elem PSI element
     */
    public static void scrollTo(PsiElement elem) {
        Editor editor = PsiUtils.openEditor(elem);
        if (editor == null) {
            return;
        }
        editor.getCaretModel().moveToOffset(elem.getTextOffset());
        editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
    }

    public static PsiClass getTopLevelClass(PsiFile psiFile) {
        if (psiFile != null && psiFile instanceof PsiJavaFile) {
            PsiJavaFile javaFile = (PsiJavaFile) psiFile;
            PsiClass[] classes = javaFile.getClasses();
            return classes.length > 0 ? classes[0] : null;
        } else {
            return null;
        }
    }

    public static String asString(@NotNull PsiMethod method, boolean showClass) {
        return asString(method, showClass, false, false, false);
    }

    public static String asHTML(@NotNull PsiMethod method, boolean showBody) {
        return asString(method, true, true, showBody, true);
    }

    private static String asString(@NotNull PsiMethod method, boolean showClass, boolean signature, boolean showBody, boolean html) {
        StringBuffer result = new StringBuffer();

        PsiClass psiClass = method.getContainingClass();
        if (psiClass != null) {
            String className = PsiUtils.getClassName(psiClass, showClass);
            result.append(className);
            result.append(".");
        }

        result.append(method.getName());
        result.append("(");
        if (signature) {
            result.append(getSignature(method, html));
        }
        result.append(")");
        PsiCodeBlock body = method.getBody();
        if (showBody && body != null) {
            result.append(" ");

            ColoredSourceVisitor visitor = new ColoredSourceVisitor();
            body.accept(visitor);

            result.append(visitor.getText());
        }
        return result.toString();
    }

    private static String getSignature(PsiMethod method, boolean html) {
        PsiParameter[] params = method.getParameterList().getParameters();
        List<String> result = new ArrayList<String>();
        for (PsiParameter param: params) {
            String typeName = param.getType().getPresentableText();
            if (html) {
                typeName = StringUtil.escapeXml(typeName);
            }
            result.add(typeName + " " + param.getName());
        }
        return StringUtils.join(result, ", ");
    }

    /**
     * Returns the element under cursor in the current editor or
     * top level class for file selected in project view if the editor isn't opened.
     *
     * @param psiFile PSI file selected in project view
     * @param editor current editor
     * @return element under cursor in the editor or class from file selected in the project view
     */
    public static PsiElement getElement(PsiFile psiFile, Editor editor) {
        if (psiFile == null) {
            return null;
        }

        if (editor == null) { // context menu for file
            return getTopLevelClass(psiFile);
        }

        int offset = editor.getCaretModel().getOffset();
        return psiFile.findElementAt(offset);
    }

    /**
     * Returns class that contains specified element or
     * null if element is null or outside a class (import, package statements)
     *
     * @param elem PSI element
     * @return parent class or null if element is null or outside a class
     */
    public static PsiClass getContainingClass(PsiElement elem) {
        if (elem == null || elem instanceof PsiMethod) {
            return (PsiClass) elem;
        }
        return PsiTreeUtil.getParentOfType(elem, PsiClass.class);
    }

    /**
     * Returns method that contains specified element or
     * null if element is null or outside method (field, class declaration).
     * 
     * @param elem PSI element
     * @return parent method or null if element is null or outside method
     */
    public static PsiMethod getContainingMethod(PsiElement elem) {
        if (elem == null || elem instanceof PsiMethod) {
            return (PsiMethod) elem;
        }
        return PsiTreeUtil.getParentOfType(elem, PsiMethod.class);
    }

    public static PsiMethod getCalledContainingMethod(PsiElement elem) {
        PsiMethod result = getCalledDeclaredMethod(elem);
        if (result != null) {
            return result;
        }

        PsiElement parent = PsiTreeUtil.getParentOfType(elem, PsiMethodCallExpression.class, PsiMethod.class);
        return getCalledDeclaredMethod(parent);
    }

    private static PsiMethod getCalledDeclaredMethod(PsiElement elem) {
        if (elem == null || elem instanceof PsiMethod) {
            return (PsiMethod) elem;
        }
        if (elem instanceof PsiMethodCallExpression) {
            PsiMethodCallExpression call = (PsiMethodCallExpression) elem;
            return call.resolveMethod();
        }
        return null;
    }

    public static Set<PsiMethod> getUsages(PsiMethod method) {
        return getUsages(method, MethodFilter.EMPTY);
    }

    public static Set<PsiMethod> getUsages(PsiMethod method, @NotNull MethodFilter filter) {
        Set<PsiMethod> result = new HashSet<PsiMethod>();

        Query<PsiReference> query = MethodReferencesSearch.search(method, true);
        for(PsiReference ref: query.findAll()) {
            PsiMethod m = getMethod(ref, filter);
            if (m != null) {
                result.add(m);
            }
        }
        return result;
    }

    private static PsiMethod getMethod(PsiReference ref, @NotNull MethodFilter filter) {
        PsiElement methodCall = ref.getElement();
        PsiMethod parentMethod = PsiTreeUtil.getParentOfType(methodCall, PsiMethod.class);
        if (parentMethod != null) {
            if (filter.match(parentMethod)) {
                return parentMethod;
            }
        }
        return null;
    }

    public static List<PsiMethod> getMethodsCalledFrom(PsiMethod method, @NotNull MethodFilter filter) {
        List<PsiMethod> result = new ArrayList<PsiMethod>();

        PsiCodeBlock body = method.getBody();
        if (body != null) {
            for (PsiMethodCallExpression call: PsiUtils.getCalls(body, filter)) {
                PsiMethod ref = call.resolveMethod();
                if (ref != null) {
                    result.add(ref);
                }
            }
        }
        return result;
    }

    public static List<PsiMethodCallExpression> getCalls(PsiElement parent, @NotNull final MethodFilter filter) {
        if (parent == null) {
            return Collections.emptyList();
        }

        final List<PsiMethodCallExpression> result = new ArrayList<PsiMethodCallExpression>();

        JavaRecursiveElementVisitor visitor = new JavaRecursiveElementVisitor() {
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                super.visitMethodCallExpression(expression);
                PsiMethod method = expression.resolveMethod();
                if (method != null) {
                    if (filter.match(method)) {
                        result.add(expression);
                    }
                }
            }
        };
        parent.accept(visitor);

        return result;
    }

    public static List<PsiMethod> getMethods(PsiElement parent) {
        final List<PsiMethod> result = new ArrayList<PsiMethod>();

        JavaRecursiveElementVisitor visitor = new JavaRecursiveElementVisitor() {
            public void visitMethod(PsiMethod method) {
                result.add(method);
                super.visitMethod(method);
            }
        };
        parent.accept(visitor);

        return result;
    }

    public static boolean isLibraryMethod(@NotNull PsiMethod method) {
        return isLibraryClass(method.getContainingClass());
    }

    public static boolean isLibraryClass(PsiClass psiClass) {
        VirtualFile file = PsiUtil.getVirtualFile(psiClass);
        return !ModuleUtil.projectContainsFile(psiClass.getProject(), file, false);
    }

    public static boolean isStringType(PsiType type) {
        if (type != null && type instanceof PsiClassType) {
            PsiClassType classType = (PsiClassType) type;
            return CommonClassNames.JAVA_LANG_STRING.equals(classType.getCanonicalText());
        }
        return false;
    }

    /**
     * Returns module names
     *
     * @param project project
     * @return names of all project modules
     */
    public static Set<String> getModules(Project project) {
        Set<String> result = new HashSet<String>();
        for (Module module: ModuleManager.getInstance(project).getModules()) {
            result.add(module.getName());
        }
        return result;
    }

    public static Set<PsiClass> getClasses(Module module) {
        Set<PsiClass> result = new HashSet<PsiClass>();
        Set<VirtualFile> sources = getSources(module);
        for (VirtualFile file: sources) {
            PsiFile psiFile = PsiManager.getInstance(module.getProject()).findFile(file);
            if (psiFile instanceof PsiJavaFile) {
                PsiJavaFile javaFile = (PsiJavaFile) psiFile;
                for (PsiClass aClass: javaFile.getClasses()) {
                    result.add(aClass);
                }
            }
        }
        return result;
    }

    public static Set<PsiClass> getClasses(Project project) {
        Set<PsiClass> classes = new HashSet<PsiClass>();
        for (Module module: ModuleManager.getInstance(project).getModules()) {
            classes.addAll(PsiUtils.getClasses(module));
        }
        return classes;
    }

    public static Set<VirtualFile> getSources(Module module) {
        Set<VirtualFile> result = new HashSet<VirtualFile>();
        VirtualFile[] roots = ModuleRootManager.getInstance(module).getSourceRoots();
        for (VirtualFile root: roots) {
            addFiles(root, result);
        }
        return result;
    }

    private static void addFiles(VirtualFile dir, Set<VirtualFile> result) {
        for (VirtualFile file: dir.getChildren()) {
            if (file.isDirectory()) {
                addFiles(file, result);
            } else {
                result.add(file);
            }
        }
    }

    public static Module getModule(PsiElement element) {
        return ModuleUtil.findModuleForPsiElement(element);
    }

    public static PsiMethod getInterfaceMethod(@NotNull PsiMethod method, @NotNull MethodFilter filter) {
        for (HierarchicalMethodSignature sig: method.getHierarchicalMethodSignature().getSuperSignatures()) {
            PsiMethod superMethod = sig.getMethod();
            if (isIntefaceMethod(superMethod) && filter.match(superMethod)) {
                return superMethod;
            }
        }
        return null;
    }

    public static Set<PsiMethod> getImplementations(@NotNull PsiMethod method, @NotNull MethodFilter filter) {
        Set<PsiMethod> result = new HashSet<PsiMethod>();
        if (isIntefaceMethod(method)) {
            Query<PsiMethod> query = OverridingMethodsSearch.search(method);
            for (PsiMethod impl: query.findAll()) {
                if (filter.match(impl)) {
                    result.add(impl);
                }
            }
        }
        return result;
    }

    public static boolean isIntefaceMethod(@NotNull PsiMethod method) {
        PsiClass psiClass = method.getContainingClass();
        return psiClass != null && psiClass.isInterface();
    }

    public static PsiPackage getPackage(PsiDirectory dir) {
        for (PsiFile file: dir.getFiles()) {
            if (file instanceof PsiJavaFile) {
                PsiJavaFile javaFile = (PsiJavaFile) file;
                return JavaPsiFacade.getInstance(javaFile.getProject()).findPackage(javaFile.getPackageName());
            }
        }
        for (PsiDirectory childDir: dir.getSubdirectories()) {
            PsiPackage childPackage = getPackage(childDir);
            if (childPackage != null) {
                return childPackage.getParentPackage();
            }
        }
        return null;
    }

    public static Set<PsiClass> getClasses(PsiPackage aPackage) {
        Set<PsiClass> classes = new HashSet<PsiClass>();
        PsiUtils.addClasses(aPackage, classes);
        return classes;
    }

    private static void addClasses(PsiPackage aPackage, Collection<PsiClass> classes) {
        for (PsiClass aClass: aPackage.getClasses()) {
            classes.add(aClass);
        }
        for (PsiPackage child: aPackage.getSubPackages()) {
            addClasses(child, classes);
        }
    }
}

package com.handyedit.codeexplorer.model;

import com.handyedit.codeexplorer.math.Edge;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.ClassUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Save or load graph from file.
 *
 * @author Alexei Orishchenko
 */
public class GraphIO {

    private static final String ATTR_NAME = "name";
    private static final String ATTR_CLASS = "class";

    private static final String ROOT_ELEM = "com/handyedit/codeexplorer";
    private static final String METHOD_ELEM = "method";
    private static final String CALL_ELEM = "call";

    private DependencyModel _model;

    public GraphIO(DependencyModel model) {
        _model = model;
    }

    public void save(File file) throws IOException {
        Element root = new Element(ROOT_ELEM);
        Document doc = new Document(root);
        for (MethodNode node: _model.getNodes()) {
            addMethod(root, node);
        }
        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
        out.output(doc, new BufferedWriter(new FileWriter(file)));
    }

    private void addMethod(Element root, MethodNode node) {
        Element result = new Element(METHOD_ELEM);
        setMethodLocation(result, node);
        addCalls(result, node);
        root.addContent(result);
    }

    private void addCalls(Element parent, MethodNode node) {
        for (Edge<MethodNode> edge: _model.getEdgesFrom(node)) {
            Element result = new Element(CALL_ELEM);
            setMethodLocation(result, edge.getTo());
            parent.addContent(result);
        }
    }

    private void setMethodLocation(Element result, MethodNode node) {
        result.setAttribute(ATTR_NAME, node.getMethod().getName());

        PsiClass psiClass = node.getMethod().getContainingClass();
        setClassName(result, psiClass);
    }

    private void setClassName(Element result, @NotNull PsiClass psiClass) {
        StringBuilder className = new StringBuilder();
        ClassUtil.formatClassName(psiClass, className); // works with anonymous classes
        result.setAttribute(ATTR_CLASS, className.toString());
    }

    public void load(File file, Project project) throws IOException, JDOMException {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(file);
        Element root = doc.getRootElement();
        if (root != null) {
            List<Element> children = root.getChildren(METHOD_ELEM);
            addMethods(children, project);
            addCalls(children, project);
        }
    }

    private void addMethods(List<Element> elems, Project project) {
        for (Element child: elems) {
            _model.add(MethodNode.create(getMethod(child, project)));
        }
    }

    private void addCalls(List<Element> methods, Project project) {
        for (Element method: methods) {
            MethodNode from = MethodNode.create(getMethod(method, project));
            List<Element> calls = method.getChildren(CALL_ELEM);
            for (Element call: calls) {
                MethodNode to = MethodNode.create(getMethod(call, project));
                _model.add(new Edge<MethodNode>(from ,to));
            }
        }
    }

    private PsiMethod getMethod(Element elem, Project project) {
        String name = elem.getAttributeValue(ATTR_NAME);
        String className = elem.getAttributeValue(ATTR_CLASS);
        if (name != null && className != null) {
            // works with anonymous classes
            PsiClass psiClass = ClassUtil.findPsiClass(PsiManager.getInstance(project), className);
            if (psiClass != null) {
                PsiMethod[] methods = psiClass.findMethodsByName(name, true);
                if (methods.length > 0) {
                    return methods[0];
                }
            }
        }
        return null;
    }
}

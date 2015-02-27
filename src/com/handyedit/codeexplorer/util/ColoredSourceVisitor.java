package com.handyedit.codeexplorer.util;

import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;

import java.awt.*;

/**
 * Visitor that produces colored method source (HTML).
 *
 * @author Alexei Orishchenko
 */
public class ColoredSourceVisitor extends JavaRecursiveElementVisitor {

    private StringBuffer _text = new StringBuffer();

    public void visitElement(PsiElement element) {
        if (element.getChildren().length == 0) {
            appendText(element, null);
        } else {
            super.visitElement(element);
        }
    }

    public void visitKeyword(PsiKeyword keyword) {
        appendText(keyword, SyntaxHighlighterColors.KEYWORD);
    }

    public void visitLiteralExpression(PsiLiteralExpression expression) {
        if (PsiUtils.isStringType(expression.getType())) {
            appendText(expression, SyntaxHighlighterColors.STRING);
        } else {
            super.visitLiteralExpression(expression);
        }
    }

    public void visitComment(PsiComment comment) {
        appendText(comment, SyntaxHighlighterColors.JAVA_BLOCK_COMMENT);
    }

    public String getText() {
        return _text.toString();
    }

    private void appendText(PsiElement element, TextAttributesKey colorKey) {
        if (colorKey != null) {
            EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
            TextAttributes attr = scheme.getAttributes(colorKey);
            Color color = attr.getForegroundColor();
            _text.append("<font color=rgb(");
            appendColor(color);
            _text.append(")>");
        }
        _text.append(StringUtil.escapeXml(element.getText()));
        if (colorKey != null) {
            _text.append("</font>");
        }
    }

    private void appendColor(Color color) {
        _text.append(color.getRed());
        _text.append(", ");
        _text.append(color.getGreen());
        _text.append(", ");
        _text.append(color.getBlue());
    }
}

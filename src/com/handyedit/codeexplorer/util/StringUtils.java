package com.handyedit.codeexplorer.util;

/**
 * String utility methods
 *
 * @author Alexei Orishchenko
 */
public class StringUtils {

    private static final String NBSP = "&nbsp;";

    public static String toHTLM(String s) {
        s = s.replace("\n", "<br>");
        s = s.replace("\t", NBSP);
        s = s.replace(" ", NBSP);
        return "<html>" + s + "</html>";
    }

    public static String getCamelWord(String s) {
        StringBuffer result = new StringBuffer();
        for(String part: org.apache.commons.lang.StringUtils.splitByCharacterTypeCamelCase(s)) {
            if (!part.isEmpty()) {
                result.append(part.charAt(0));
            }
        }
        return result.toString();
    }
}

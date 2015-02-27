package com.handyedit.codeexplorer;

/**
 * @author Alexei Orishchenko
 */
public class CodeExplorerSettings {

    private boolean _isHorisontalOrientation = false;
    private boolean _isStructureByClick = true;

    private boolean _isShowClassName = true;
    private boolean _isGotoSource = false;

    private boolean _isShowMethodBody = true;

    public boolean isShowClassName() {
        return _isShowClassName;
    }

    public void setShowClassName(boolean showClassName) {
        _isShowClassName = showClassName;
    }

    public boolean isGotoSource() {
        return _isGotoSource;
    }

    public void setGotoSource(boolean gotoSource) {
        _isGotoSource = gotoSource;
    }

    public boolean isHorisontalOrientation() {
        return _isHorisontalOrientation;
    }

    public void setHorisontalOrientation(boolean horisontalOrientation) {
        _isHorisontalOrientation = horisontalOrientation;
    }

    public boolean isStructureByClick() {
        return _isStructureByClick;
    }

    public void setStructureByClick(boolean structureByClick) {
        _isStructureByClick = structureByClick;
    }

    public boolean isShowMethodBody() {
        return _isShowMethodBody;
    }

    public void setShowMethodBody(boolean showMethodBody) {
        _isShowMethodBody = showMethodBody;
    }
}

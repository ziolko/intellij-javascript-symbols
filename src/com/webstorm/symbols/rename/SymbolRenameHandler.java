package com.webstorm.symbols.rename;

import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.refactoring.rename.RenameHandler;
import com.webstorm.symbols.SymbolUtils;
import com.webstorm.symbols.rename.SymbolRenameDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SymbolRenameHandler implements RenameHandler {
    @Override
    public boolean isAvailableOnDataContext(DataContext dataContext) {
        final PsiElement psiElement = LangDataKeys.PSI_ELEMENT.getData(dataContext);

        final JSLiteralExpression jsLiteralExpression = SymbolUtils.getJSLiteraExpression(psiElement);
        if(jsLiteralExpression != null && SymbolUtils.isSymbol(jsLiteralExpression)) return true;

        final JSProperty jsProperty = SymbolUtils.getJSProperty(psiElement);
        if(jsProperty != null && SymbolUtils.isSymbol(jsProperty)) return true;

        return false;
    }

    @Override
    public boolean isRenaming(DataContext dataContext) {
        return isAvailableOnDataContext(dataContext);
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile, DataContext dataContext) {
        final PsiElement psiElement = LangDataKeys.PSI_ELEMENT.getData(dataContext);
        final JSLiteralExpression jsLiteralExpression = SymbolUtils.getJSLiteraExpression(psiElement);
        final JSProperty jsProperty = SymbolUtils.getJSProperty(psiElement);

        if(!SymbolUtils.isSymbol(jsLiteralExpression) && !SymbolUtils.isSymbol(jsProperty)){
            return;
        }

        RenameDialog dialog = new SymbolRenameDialog(project, psiElement, null, editor);
        dialog.show();
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] psiElements, DataContext dataContext) {

    }
}

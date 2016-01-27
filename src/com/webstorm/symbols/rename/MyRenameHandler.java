package com.webstorm.symbols.rename;

import com.intellij.lang.javascript.psi.JSLiteralExpression;
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

public class MyRenameHandler implements RenameHandler {
    @Nullable
    private static JSLiteralExpression getElement(DataContext dataContext) {
        return  SymbolUtils.getJSLiteraExpression(LangDataKeys.PSI_ELEMENT.getData(dataContext));
    }

    @Override
    public boolean isAvailableOnDataContext(DataContext dataContext) {
        final JSLiteralExpression element = getElement(dataContext);
        return element != null && SymbolUtils.isSymbol(element);
    }

    @Override
    public boolean isRenaming(DataContext dataContext) {
        return isAvailableOnDataContext(dataContext);
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile, DataContext dataContext) {
        final JSLiteralExpression element = getElement(dataContext);

        if(element == null || !SymbolUtils.isSymbol(element)){
            return;
        }

        RenameDialog dialog = new SymbolRenameDialog(project, element, null, editor);
        dialog.show();
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] psiElements, DataContext dataContext) {

    }
}

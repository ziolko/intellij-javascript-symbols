package com.webstorm.symbols.rename;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.refactoring.rename.RenameHandler;
import com.webstorm.symbols.SymbolUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SymbolRenameHandler implements RenameHandler {

    @Nullable
    private PsiElement getSymbolPsiElement(DataContext dataContext) {
        final PsiElement firstPsiElement = LangDataKeys.PSI_ELEMENT.getData(dataContext);
        return SymbolUtils.getSymbolElement(firstPsiElement);

//        TODO: fix rename for JSON string values
//        final Caret caret = LangDataKeys.CARET.getData(dataContext);
//        final PsiFile psiFile = LangDataKeys.PSI_FILE.getData(dataContext);
//
//        if(caret == null || psiFile == null) return null;
//
//        final int caretPosition = caret.getOffset();
//        final PsiElement psiElement = psiFile.findElementAt(caretPosition);
//
//        return SymbolUtils.getSymbolElement(psiElement);
    }

    @Override
    public boolean isAvailableOnDataContext(DataContext dataContext) {
        return getSymbolPsiElement(dataContext) != null;
    }

    @Override
    public boolean isRenaming(DataContext dataContext) {
        return isAvailableOnDataContext(dataContext);
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile, DataContext dataContext) {
        final PsiElement symbolPsiElement = getSymbolPsiElement(dataContext);

        if(symbolPsiElement == null) {
            return;
        }

        RenameDialog dialog = new SymbolRenameDialog(project, symbolPsiElement, null, editor);
        dialog.show();
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] psiElements, DataContext dataContext) {

    }
}

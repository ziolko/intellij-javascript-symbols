package com.webstorm.symbols.rename;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.rename.RenameDialog;
import com.webstorm.symbols.SymbolUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SymbolRenameDialog extends RenameDialog {
    public SymbolRenameDialog(@NotNull Project project, @NotNull PsiElement psiElement, @Nullable PsiElement nameSuggestionContext, Editor editor) {
        super(project, psiElement, nameSuggestionContext, editor);
    }

    @Override
    public String[] getSuggestedNames() {
        if(!SymbolUtils.isSymbol(getPsiElement())) {
            return super.getSuggestedNames();
        }

        return new String[] { SymbolUtils.getSymbolFromPsiElement(getPsiElement()) };
    }

    @Override
    protected void canRun() throws ConfigurationException {
        if (!areButtonsValid()) {
            throw new ConfigurationException("" + getNewName() + " is not a valid symbol");
        }
    }

    @Override
    protected boolean areButtonsValid() {
        return SymbolUtils.isSymbol(getNewName(), false);
    }
}

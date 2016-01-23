package com.webstorm.symbols.findUsages;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesHandlerFactory;
import com.intellij.psi.PsiElement;
import com.webstorm.symbols.psi.SymbolLiteralExpressionImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MyFindUsagesHandlerFactory extends FindUsagesHandlerFactory {
    @Override
    public boolean canFindUsages(@NotNull PsiElement psiElement) {
        return psiElement instanceof SymbolLiteralExpressionImpl;
    }

    @Nullable
    @Override
    public FindUsagesHandler createFindUsagesHandler(@NotNull PsiElement psiElement, boolean forHighlightUsages) {
        return new MyFindUsagesHandler(psiElement);
    }
}

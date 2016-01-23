package com.webstorm.symbols.reference;

import com.intellij.codeInsight.TargetElementEvaluator;
import com.intellij.lang.javascript.psi.impl.JSLiteralExpressionImpl;
import com.intellij.lang.javascript.psi.impl.JSTextReference;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.webstorm.symbols.SymbolUtils;
import com.webstorm.symbols.psi.SymbolLiteralExpressionImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SymbolReferenceTargetEvaluator implements TargetElementEvaluator {
    @Override
    public boolean includeSelfInGotoImplementation(@NotNull PsiElement psiElement) {
        return false;
    }

    @Nullable
    @Override
    public PsiElement getElementByReference(@NotNull PsiReference psiReference, int i) {
        if(psiReference instanceof JSTextReference && SymbolUtils.isSymbol(psiReference.getElement())) {
            return new SymbolLiteralExpressionImpl((JSLiteralExpressionImpl) psiReference.getElement());
        }

        return null;
    }
}

package com.webstorm.symbols;

import com.intellij.psi.PsiElement;
import com.intellij.usages.impl.rules.UsageType;
import com.intellij.usages.impl.rules.UsageTypeProvider;
import org.jetbrains.annotations.Nullable;

public class SymbolUsageTypeProvider implements UsageTypeProvider {
    private static final UsageType JAVASCRIPT_SYMBOL = new UsageType("JavaScript symbol");

    @Nullable
    @Override
    public UsageType getUsageType(PsiElement element) {
        if(SymbolUtils.isSymbol(element)) {
            return JAVASCRIPT_SYMBOL;
        }

        return null;
    }
}

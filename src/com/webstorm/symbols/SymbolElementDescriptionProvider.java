package com.webstorm.symbols;

import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.ElementDescriptionProvider;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewTypeLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SymbolElementDescriptionProvider implements ElementDescriptionProvider {
    @Nullable
    @Override
    public String getElementDescription(@NotNull PsiElement element, @NotNull ElementDescriptionLocation location) {
        if(SymbolUtils.isSymbol(SymbolUtils.getJSLiteralExpression(element)) && location instanceof UsageViewTypeLocation){
            return "JavaScript symbol";
        }

        return null;
    }
}

package com.webstorm.symbols;

import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.psi.PsiElement;
import com.intellij.usages.impl.rules.UsageType;
import com.intellij.usages.impl.rules.UsageTypeProvider;
import org.jetbrains.annotations.Nullable;

public class SymbolUsageTypeProvider implements UsageTypeProvider {
    private static final UsageType JAVASCRIPT_SYMBOL = new UsageType("JavaScript symbol");

    @Nullable
    @Override
    public UsageType getUsageType(PsiElement element) {
        final JSLiteralExpression jsLiteralExpression = SymbolUtils.getJSLiteralExpression(element);
        final JSProperty jsProperty = SymbolUtils.getJSProperty(element);
        final JsonStringLiteral jsonStringLiteral = SymbolUtils.getJsonStringLiteral(element);
        final JsonProperty jsonProperty = SymbolUtils.getJsonProperty(element);

        if (SymbolUtils.isSymbol(jsLiteralExpression) ||
            SymbolUtils.isSymbol(jsProperty) ||
            SymbolUtils.isSymbol(jsonStringLiteral) ||
            SymbolUtils.isSymbol(jsonProperty)) {
            return JAVASCRIPT_SYMBOL;
        }

        return null;
    }
}

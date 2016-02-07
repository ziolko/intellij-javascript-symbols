package com.webstorm.symbols.declaration;

import com.google.common.collect.Lists;
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import com.webstorm.symbols.SymbolUtils;
import com.webstorm.symbols.reference.SymbolReferencesSearch;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class SymbolGoToDeclarationHandler implements GotoDeclarationHandler {
    @Nullable
    @Override
    public PsiElement[] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        final JSLiteralExpression jsLiteralExpression = SymbolUtils.getJSLiteraExpression(sourceElement);

        if(!SymbolUtils.isSymbol(jsLiteralExpression)) return null;

        final SymbolReferencesSearch symbolReferencesSearch = new SymbolReferencesSearch();
        final SearchScope searchScope = GlobalSearchScope.projectScope(jsLiteralExpression.getProject());
        final ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(jsLiteralExpression, searchScope, false);

        final ArrayList<PsiElement> psiElements = Lists.newArrayList();

        symbolReferencesSearch.processQuery(searchParameters, new Processor<PsiReference>() {
            @Override
            public boolean process(PsiReference psiReference) {
                psiElements.add(psiReference.getElement());
                return true;
            }
        });


        return psiElements.toArray(new PsiElement[psiElements.size()]);
    }

    @Nullable
    @Override
    public String getActionText(DataContext context) {
        return null;
    }
}

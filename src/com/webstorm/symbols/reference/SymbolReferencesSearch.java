package com.webstorm.symbols.reference;

import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import com.intellij.util.indexing.FileBasedIndex;
import com.webstorm.symbols.SymbolUtils;
import com.webstorm.symbols.index.JSSymbolsIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

public class SymbolReferencesSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {
    @Override
    public void processQuery(@NotNull ReferencesSearch.SearchParameters searchParameters, @NotNull Processor<PsiReference> processor) {
        final JSLiteralExpression jsLiteralExpression = SymbolUtils.getJSLiteraExpression(searchParameters.getElementToSearch());
        if(jsLiteralExpression == null) {
            return;
        }

        final String searchedSymbolText = SymbolUtils.getSymbolFromText(ApplicationManager.getApplication().runReadAction(
                (Computable<String>) jsLiteralExpression::getText
        ), true);

        if(searchedSymbolText == null) {
            return;
        }

        if(searchParameters.getEffectiveSearchScope() instanceof LocalSearchScope) {
            processPsiFile(jsLiteralExpression.getContainingFile(), jsLiteralExpression, processor);
        }
        else if(searchParameters.getEffectiveSearchScope() instanceof GlobalSearchScope) {
            final GlobalSearchScope globalSearchScope = (GlobalSearchScope) searchParameters.getEffectiveSearchScope();
            final Collection<VirtualFile> filesWithSymbols = ApplicationManager.getApplication().runReadAction((Computable<Collection<VirtualFile>>) () ->
                FileBasedIndex.getInstance().getContainingFiles(JSSymbolsIndex.INDEX_ID, searchedSymbolText, globalSearchScope)
            );

            filesWithSymbols.forEach(virtualFile -> {
                final PsiFile psiFile = ApplicationManager.getApplication().runReadAction((Computable<PsiFile>) () ->
                    PsiManager.getInstance(globalSearchScope.getProject()).findFile(virtualFile)
                );

                processPsiFile(psiFile, jsLiteralExpression, processor);
            });
        }
    }

    private void processPsiFile(PsiFile psiFile, JSLiteralExpression searchedElement, Processor<PsiReference> processor) {
        ApplicationManager.getApplication().runReadAction(() -> {
            String searchedSymbolText = SymbolUtils.getSymbolFromText(searchedElement.getText(), true);

            SymbolUtils.processSymbolsInPsiFile(psiFile, element -> {
                final String symbolText = SymbolUtils.getSymbolFromText(element.getText(), true);

                if (Objects.equals(symbolText, searchedSymbolText)) {
                    processor.process(new SymbolReference(searchedElement, element));
                }

                return true;
            });
        });
    }
}
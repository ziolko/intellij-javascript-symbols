package com.webstorm.symbols.reference;

import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSProperty;
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

public class SymbolReferencesSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {
    @Override
    public void processQuery(final @NotNull ReferencesSearch.SearchParameters searchParameters, final @NotNull Processor<PsiReference> processor) {
        final PsiElement psiElement = searchParameters.getElementToSearch();
        final String searchedSymbolText = SymbolUtils.getSymbolFromPsiElement(psiElement);

        if(searchedSymbolText == null) {
            return;
        }

        if(searchParameters.getEffectiveSearchScope() instanceof LocalSearchScope) {
            processPsiFile(psiElement.getContainingFile(), psiElement, processor);
        }
        else if(searchParameters.getEffectiveSearchScope() instanceof GlobalSearchScope) {
            final GlobalSearchScope globalSearchScope = (GlobalSearchScope) searchParameters.getEffectiveSearchScope();
            final Collection<VirtualFile> filesWithSymbols = ApplicationManager.getApplication().runReadAction(new Computable<Collection<VirtualFile>>() {
                        @Override
                        public Collection<VirtualFile> compute() {
                            return FileBasedIndex.getInstance().getContainingFiles(JSSymbolsIndex.INDEX_ID, searchedSymbolText, globalSearchScope);
                        }
                    }
            );

            for (final VirtualFile virtualFile : filesWithSymbols) {
                final PsiFile psiFile = ApplicationManager.getApplication().runReadAction(
                    new Computable<PsiFile>() {
                               @Override
                               public PsiFile compute() {
                                   return PsiManager.getInstance(globalSearchScope.getProject()).findFile(virtualFile);
                               }
                           }
                    );

                SymbolReferencesSearch.this.processPsiFile(psiFile, psiElement, processor);
            }
        }
    }

    private void processPsiFile(final PsiFile psiFile, final PsiElement searchedElement, final Processor<PsiReference> processor) {
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                final String searchedSymbolText = SymbolUtils.getSymbolFromPsiElement(searchedElement);
                SymbolUtils.processSymbolsInPsiFile(psiFile, new Processor<PsiElement>() {
                    @Override
                    public boolean process(PsiElement element) {
                        final String symbolText = SymbolUtils.getSymbolFromPsiElement(element);

                        if(symbolText != null && symbolText.equals(searchedSymbolText)) {
                            processor.process(new SymbolReference(searchedElement, element));
                        }

                        return true;
                    }
                });
            }
        });
    }
}
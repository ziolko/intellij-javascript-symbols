package com.webstorm.symbols.angular;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import com.intellij.util.indexing.FileBasedIndex;
import com.webstorm.symbols.SymbolUtils;
import com.webstorm.symbols.reference.SymbolReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class AngularSymbolReferencesSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {
    @Override
    public void processQuery(final @NotNull ReferencesSearch.SearchParameters searchParameters, final @NotNull Processor<PsiReference> processor) {
        if(!AngularSymbolUtils.isAngularPluginEnabled()) {
            return;
        }

        final PsiElement psiElement = searchParameters.getElementToSearch();

        if(searchParameters.getScope() instanceof GlobalSearchScope) {
            final GlobalSearchScope globalSearchScope = (GlobalSearchScope) searchParameters.getScope();
            final Collection<PsiFile> angularFilesWithSymbols = getPsiFilesWithText(psiElement, globalSearchScope);

            for(final PsiFile psiFile : angularFilesWithSymbols) {
                processPsiFileWithInjectedAngularDirectives(psiFile, psiElement, processor);
            }
        } else {
            processPsiFileWithInjectedAngularDirectives(SymbolUtils.getRootPsiFile(psiElement), psiElement, processor);
        }
    }

    private Collection<PsiFile> getPsiFilesWithText(final PsiElement searchedElement, final GlobalSearchScope globalSearchScope) {
        final String searchedSymbolText = SymbolUtils.getSymbolFromPsiElement(searchedElement);

        if (searchedSymbolText == null) {
            return Lists.newArrayList();
        }

        final Collection<VirtualFile> angularFilesWithSymbols = ApplicationManager.getApplication().runReadAction(
                new Computable<Collection<VirtualFile>>() {
                    @Override
                    public Collection<VirtualFile> compute() {
                        return FileBasedIndex.getInstance().getContainingFiles(AngularSymbolsIndex.INDEX_ID, searchedSymbolText, globalSearchScope);
                    }
                }
        );

        return Collections2.transform(angularFilesWithSymbols, new Function<VirtualFile, PsiFile>() {
            @Override
            public PsiFile apply(final VirtualFile virtualFile) {
                return ApplicationManager.getApplication().runReadAction(
                        new Computable<PsiFile>() {
                            @Override
                            public PsiFile compute() {
                                return PsiManager.getInstance(searchedElement.getProject()).findFile(virtualFile);
                            }
                        }
                );
            }
        });
    }

    private void processPsiFileWithInjectedAngularDirectives(final PsiFile psiFile, final PsiElement searchedElement, final Processor<PsiReference> processor) {
        for (final PsiFile injectedFile : AngularSymbolUtils.getInjectedAngularPsiFiles(psiFile)) {
            for (SymbolReference reference : SymbolUtils.getSymbolReferencesInPsiFile(injectedFile, searchedElement)) {
                processor.process(reference);
            }
        }
    }
}
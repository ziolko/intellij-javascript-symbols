package com.webstorm.symbols;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.JBColor;
import com.intellij.util.ProcessingContext;
import com.intellij.util.indexing.FileBasedIndex;
import com.webstorm.symbols.index.JSSymbolsIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class SymbolCompletionContributor extends CompletionContributor {
    public SymbolCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(JSTokenTypes.STRING_LITERAL), getProvider());
    }

    private static CompletionProvider<CompletionParameters> getProvider() {
        return new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters,
                                          ProcessingContext processingContext,
                                          @NotNull CompletionResultSet completionResultSet) {

                final Collection<String> keys = FileBasedIndex.getInstance().getAllKeys(JSSymbolsIndex.INDEX_ID, completionParameters.getEditor().getProject());
                final GlobalSearchScope globalSearchScope = GlobalSearchScope.projectScope(completionParameters.getEditor().getProject());
                for (String key : keys) {
                    final List<Integer> values = FileBasedIndex.getInstance().getValues(JSSymbolsIndex.INDEX_ID, key, globalSearchScope);
                    if(values.size() == 0) continue;
                    completionResultSet.addElement(LookupElementBuilder
                            .create(key)
                            .withItemTextForeground(JBColor.BLUE)
                    );
                }
            }
        };
    }
}

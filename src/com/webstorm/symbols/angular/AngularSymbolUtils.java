package com.webstorm.symbols.angular;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.lang.Language;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import com.webstorm.symbols.settings.SettingsComponent;
import org.angularjs.editor.AngularJSInjector;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AngularSymbolUtils {
    public static boolean isAngularPluginEnabled() {
        try {
            Class.forName("org.angularjs.editor.AngularJSInjector");
            return true;
        } catch (Throwable ex) {
            // Class or one of its dependencies is not present...
            return false;
        }
    }

    public static Set<String> getSymbolsInPlainText(@NotNull final String text) {
        final Set<String> result = Sets.newHashSet();

        // The regexp below search for all single or double quoted strings in given text
        // http://stackoverflow.com/questions/249791/regex-for-quoted-string-with-escaping-quotes/10786066#10786066
        final Matcher quoteTextMatcher = Pattern.compile("\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"|'([^'\\\\]*(\\\\.[^'\\\\]*)*)'").matcher(text);

        // For each single or double quoted substring check if it is a symbol
        while(quoteTextMatcher.find()) {
            final String expression = quoteTextMatcher.group();
            result.addAll(getSymbolsInPlainText(expression.substring(1, expression.length() - 1)));
        }

        // Take all symbol regexps and check if given text matches it
        final String[] symbolRegExps = SettingsComponent.getInstance().getRegExp();
        for(String symbolRegExp : symbolRegExps) {
            final Matcher matcher = Pattern.compile(symbolRegExp).matcher(text);
            while(matcher.find()) {
                result.add(matcher.group());
            }
        }

        return result;
    }

    public static List<PsiFile> getInjectedAngularPsiFiles(@NotNull final PsiFile psiFile) {
        final List<PsiFile> result = Lists.newArrayList();

        for(final PsiElement directive : getAngularDirectivesInFile(psiFile)) {
            InjectedLanguageManager.getInstance(psiFile.getProject()).enumerate(directive, new PsiLanguageInjectionHost.InjectedPsiVisitor() {
                @Override
                public void visit(@NotNull PsiFile psiFile, @NotNull List<PsiLanguageInjectionHost.Shred> list) {
                    result.add(psiFile);
                }
            });
        }

        return result;
    }

    public static int countReferencesInAngularDirectives(final PsiElement psiElement) {
        if(!isAngularPluginEnabled()) {
            return 0;
        }

        final GlobalSearchScope searchScope = GlobalSearchScope.projectScope(psiElement.getProject());

        final AngularSymbolReferencesSearch angularSymbolReferencesSearch = new AngularSymbolReferencesSearch();
        final ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(psiElement, searchScope, false);
        final List<PsiReference> referenceList = Lists.newArrayList();

        angularSymbolReferencesSearch.processQuery(searchParameters, new Processor<PsiReference>() {
            @Override
            public boolean process(PsiReference psiReference) {
                referenceList.add(psiReference);
                return true;
            }
        });

        return referenceList.size();
    }

    private static List<PsiElement> getAngularDirectivesInFile(@NotNull final PsiFile psiFile) {
        final List<PsiElement> result = Lists.newArrayList();

        if(!AngularSymbolUtils.isAngularPluginEnabled()) {
            return result;
        }

        final MultiHostRegistrar multiHostRegistrar = new MultiHostRegistrar() {
            @NotNull
            @Override
            public MultiHostRegistrar startInjecting(@NotNull Language language) { return this; }

            @NotNull
            @Override
            public MultiHostRegistrar addPlace(@NonNls @Nullable String s, @NonNls @Nullable String s1, @NotNull PsiLanguageInjectionHost psiLanguageInjectionHost, @NotNull TextRange textRange) {
                result.add(psiLanguageInjectionHost.getOriginalElement());
                return this;
            }

            @Override
            public void doneInjecting() { }
        };

        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                psiFile.acceptChildren(new PsiRecursiveElementVisitor() {
                    @Override
                    public void visitElement(PsiElement element) {
                        new AngularJSInjector().getLanguagesToInject(multiHostRegistrar, element);
                        super.visitElement(element);
                    }
                });
            }
        });

        return result;
    }
}

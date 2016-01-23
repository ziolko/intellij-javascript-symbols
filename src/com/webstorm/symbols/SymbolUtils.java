package com.webstorm.symbols;

import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SymbolUtils {
    public static boolean isSymbol(@NotNull PsiElement psiElement) {
        if(psiElement instanceof JSLiteralExpression) {
            final JSLiteralExpression literal = (JSLiteralExpression) psiElement;
            if(!literal.isQuotedLiteral()) return false;
            final String text = ApplicationManager.getApplication().runReadAction((Computable<String>) () -> literal.getText());
            return literal.isQuotedLiteral() && isSymbol(text, true);
        }

        return false;
    }

    public static boolean isSymbol(@NotNull String text, boolean withQuotes) {
        return getSymbolFromText(text, withQuotes) != null;
    }

    public static @Nullable String getSymbolFromPsiElement(@NotNull PsiElement psiElement) {
        if(psiElement instanceof JSLiteralExpression) {
            final JSLiteralExpression literal = (JSLiteralExpression) psiElement;
            if(!literal.isQuotedLiteral()) return null;
            final String text = ApplicationManager.getApplication().runReadAction((Computable<String>) () -> literal.getText());
            return getSymbolFromText(text, true);
        }

        return null;
    }

    public static @Nullable String getSymbolFromText(@NotNull String text, boolean withQuotes) {
        if(withQuotes) {
            if(text.length() < 2) return null;

            char firstChar = text.charAt(0);
            char lastChar = text.charAt(text.length() - 1);

            if(firstChar != lastChar) return null;
            if(firstChar != '\'' && firstChar != '"') return null;

            text = text.substring(1, text.length() - 1);
        }

        if(!text.matches("^:[a-zA-Z0-9\\-]+$")) return null;

        return text;
    }

    public static void processSymbolsInPsiFile(@NotNull PsiFile file, @NotNull Processor<PsiElement> processor) {
        file.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                super.visitElement(element);

                if(element instanceof JSLiteralExpression) {
                    final JSLiteralExpression literal = (JSLiteralExpression) element;

                    if(literal.isQuotedLiteral() && isSymbol(literal.getText(), true)) {
                        processor.process(element);
                    }
                }
            }
        });
    }
}

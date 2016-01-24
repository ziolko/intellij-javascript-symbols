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
    public static boolean isSymbol(@Nullable JSLiteralExpression psiElement) {
        if(psiElement == null) return false;
        if(!psiElement.isQuotedLiteral()) return false;
        final String text = ApplicationManager.getApplication().runReadAction((Computable<String>) () -> psiElement.getText());
        return isSymbol(text, true);
    }

    public static boolean isSymbol(@NotNull String text, boolean withQuotes) {
        return getSymbolFromText(text, withQuotes) != null;
    }

    public static @Nullable String getSymbolFromPsiElement(@Nullable JSLiteralExpression psiElement) {
        if(psiElement == null) return null;
        if(!psiElement.isQuotedLiteral()) return null;
        final String text = ApplicationManager.getApplication().runReadAction((Computable<String>) () -> psiElement.getText());
        return getSymbolFromText(text, true);
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

    public static void processSymbolsInPsiFile(@NotNull PsiFile file, @NotNull Processor<JSLiteralExpression> processor) {
        file.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                super.visitElement(element);
                final JSLiteralExpression jsLiteralExpression = getJSLiteraExpression(element);
                if(isSymbol(jsLiteralExpression)) {
                    processor.process(jsLiteralExpression);
                }
            }
        });
    }

    @Nullable
    public static JSLiteralExpression getJSLiteraExpression(@Nullable PsiElement psiElement) {
        if(psiElement instanceof JSLiteralExpression) return (JSLiteralExpression) psiElement;
        if(psiElement != null && psiElement.getParent() instanceof JSLiteralExpression) {
            return (JSLiteralExpression) psiElement.getParent();
        }
        return null;
    }
}

package com.webstorm.symbols;

import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.Processor;
import com.webstorm.symbols.settings.SettingsComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SymbolUtils {
    public static boolean isSymbol(final @Nullable JSLiteralExpression psiElement) {
        if(psiElement == null) return false;
        if(!psiElement.isQuotedLiteral()) return false;
        final String text = ApplicationManager.getApplication().runReadAction(new Computable<String>() {
            @Override
            public String compute() {
                return psiElement.getText();
            }
        });
        return isSymbol(text, true);
    }

    public static boolean isSymbol(final @Nullable JSProperty jsProperty) {
        if(jsProperty == null) return false;
        final String text =  ApplicationManager.getApplication().runReadAction(new Computable<String>() {
            @Override
            public String compute() {
                return jsProperty.getName();
            }
        });
        return isSymbol(text, false);
    }

    public static boolean isSymbol(@NotNull String text, boolean withQuotes) {
        return getSymbolFromText(text, withQuotes) != null;
    }

    public static @Nullable String getSymbolFromPsiElement(final @Nullable JSLiteralExpression psiElement) {
        if(psiElement == null) return null;
        if(!psiElement.isQuotedLiteral()) return null;
        final String text = ApplicationManager.getApplication().runReadAction(new Computable<String>() {
            @Override
            public String compute() {
                return psiElement.getText();
            }
        });
        return getSymbolFromText(text, true);
    }

    public static @Nullable String getSymbolFromPsiElement(final @Nullable JSProperty psiElement) {
        if(psiElement == null) return null;
        final String text = ApplicationManager.getApplication().runReadAction(new Computable<String>() {
            @Override
            public String compute() {
                return psiElement.getNameIdentifier() != null ? psiElement.getNameIdentifier().getText() : null;
            }
        });

        if(text == null) return null;
        return getSymbolFromText(text, isQuoted(text));
    }

    public static @Nullable String getSymbolFromPsiElement(final @Nullable PsiElement psiElement) {
        final JSLiteralExpression jsLiteralExpression = getJSLiteraExpression(psiElement);
        final JSProperty jsProperty = getJSProperty(psiElement);

        if(jsLiteralExpression != null) {
            return SymbolUtils.getSymbolFromPsiElement(jsLiteralExpression);
        }
        if(jsProperty != null) {
            return SymbolUtils.getSymbolFromPsiElement(jsProperty);
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

        final String[] symbolRegExps = SettingsComponent.getInstance().getRegExp();

        for(String symbolRegExp : symbolRegExps) {
            if(text.matches(symbolRegExp)) {
                return text;
            }
        }

        return null;
    }

    public static void processSymbolsInPsiFile(@NotNull PsiFile file, final @NotNull Processor<PsiElement> processor) {
        file.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                final JSLiteralExpression jsLiteralExpression = getJSLiteraExpression(element);
                final JSProperty jsProperty = getJSProperty(element);

                if(isSymbol(jsLiteralExpression)) {
                    processor.process(jsLiteralExpression);
                } else if(isSymbol(jsProperty)){
                    processor.process(jsProperty);
                    super.visitElement(element);
                } else {
                    super.visitElement(element);
                }
            }
        });
    }

    @Nullable
    public static JSLiteralExpression getJSLiteraExpression(@Nullable final PsiElement psiElement) {
        if(psiElement == null) return null;
        if(psiElement instanceof JSLiteralExpression) return (JSLiteralExpression) psiElement;

        final PsiElement parent = ApplicationManager.getApplication().runReadAction(new Computable<PsiElement>() {
            @Override
            public PsiElement compute() {
                return psiElement.getParent();
            }
        });

        return (parent instanceof JSLiteralExpression) ? (JSLiteralExpression) parent : null;
    }

    @Nullable
    public static JSProperty getJSProperty(@Nullable final PsiElement psiElement) {
        return (psiElement instanceof JSProperty) ? (JSProperty) psiElement : null;
    }

    public static boolean isQuoted(final @Nullable String text) {
        return text != null && (text.charAt(0) == '\'' || text.charAt(0) == '"');
    }

    @Nullable
    public static PsiElement getSymbolElement(@Nullable final PsiElement sourceElement) {
        final JSLiteralExpression jsLiteralExpression = SymbolUtils.getJSLiteraExpression(sourceElement);
        if(SymbolUtils.isSymbol(jsLiteralExpression)) return jsLiteralExpression;

        final JSProperty jsProperty = SymbolUtils.getJSProperty(sourceElement);
        if(SymbolUtils.isSymbol(jsProperty)) return jsProperty;

        if(sourceElement instanceof LeafPsiElement) {
            final JSProperty parentJsProperty = ApplicationManager.getApplication().runReadAction(new Computable<JSProperty>() {
                @Override
                public JSProperty compute() {
                    return SymbolUtils.getJSProperty(sourceElement.getParent());
                }
            });

            if(SymbolUtils.isSymbol(parentJsProperty)) return parentJsProperty;
        }

        return null;
    }
}

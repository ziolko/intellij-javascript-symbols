package com.webstorm.symbols;

import com.google.common.collect.Lists;
import com.intellij.injected.editor.DocumentWindow;
import com.intellij.injected.editor.VirtualFileWindow;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.Processor;
import com.webstorm.symbols.reference.SymbolReference;
import com.webstorm.symbols.settings.SettingsComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class SymbolUtils {
    public static boolean isSymbol(final @Nullable JSLiteralExpression psiElement) {
        return getSymbolFromPsiElement(psiElement) != null;
    }

    public static boolean isSymbol(final @Nullable JSProperty jsProperty) {
        return getSymbolFromPsiElement(jsProperty) != null;
    }

    public static boolean isSymbol(final @Nullable JsonStringLiteral jsonStringLiteral) {
        return getSymbolFromPsiElement(jsonStringLiteral) != null;
    }

    public static boolean isSymbol(final @Nullable JsonProperty jsonProperty) {
        return getSymbolFromPsiElement(jsonProperty) != null;
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

        if(text == null || !isQuoted(text)) return null;
        return getSymbolFromText(text, true);
    }

    public static @Nullable String getSymbolFromPsiElement(final @Nullable JsonStringLiteral psiElement) {
        if(psiElement == null) return null;
        final String text = ApplicationManager.getApplication().runReadAction(new Computable<String>() {
            @Override
            public String compute() {
                return psiElement.getText();
            }
        });

        return getSymbolFromText(text, true);
    }

    public static @Nullable String getSymbolFromPsiElement(final @Nullable JsonProperty psiElement) {
        if(psiElement == null) return null;
        final String text = ApplicationManager.getApplication().runReadAction(new Computable<String>() {
            @Override
            public String compute() {
                return psiElement.getName();
            }
        });

        return getSymbolFromText(text, false);
    }

    public static @Nullable String getSymbolFromPsiElement(final @Nullable PsiElement psiElement) {
        final JSLiteralExpression jsLiteralExpression = getJSLiteralExpression(psiElement);
        final JSProperty jsProperty = getJSProperty(psiElement);
        final JsonStringLiteral jsonStringLiteral = getJsonStringLiteral(psiElement);
        final JsonProperty jsonProperty = getJsonProperty(psiElement);

        if(jsLiteralExpression != null) {
            return SymbolUtils.getSymbolFromPsiElement(jsLiteralExpression);
        }
        if(jsProperty != null) {
            return SymbolUtils.getSymbolFromPsiElement(jsProperty);
        }
        if(jsonStringLiteral != null) {
            return SymbolUtils.getSymbolFromPsiElement(jsonStringLiteral);
        }

        if(jsonProperty != null) {
            return SymbolUtils.getSymbolFromPsiElement(jsonProperty);
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

    public static void processSymbolsInPsiFile(@NotNull final PsiFile file, final @NotNull Processor<PsiElement> processor) {
        file.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                final JSLiteralExpression jsLiteralExpression = getJSLiteralExpression(element);
                final JSProperty jsProperty = getJSProperty(element);
                final JsonStringLiteral jsonStringLiteral = getJsonStringLiteral(element);

                if(isSymbol(jsLiteralExpression)) {
                    processor.process(jsLiteralExpression);
                } else if(isSymbol(jsProperty)) {
                    processor.process(jsProperty);
                    super.visitElement(element);
                } else if(isSymbol(jsonStringLiteral)) {
                    processor.process(jsonStringLiteral);
                } else {
                    super.visitElement(element);
                }
            }
        });
    }

    public static List<SymbolReference> getSymbolReferencesInPsiFile(final PsiFile psiFile, final PsiElement searchedElement) {
        final List<SymbolReference> result = Lists.newArrayList();
        final String searchedSymbolText = SymbolUtils.getSymbolFromPsiElement(searchedElement);

        final Processor<PsiElement> symbolProcessor = new Processor<PsiElement>() {
            @Override
            public boolean process(PsiElement element) {
                final String symbolText = SymbolUtils.getSymbolFromPsiElement(element);

                if (Objects.equals(symbolText, searchedSymbolText)) {
                    result.add(new SymbolReference(searchedElement, element));
                }

                return true;
            }
        };

        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                SymbolUtils.processSymbolsInPsiFile(psiFile, symbolProcessor);
            }
        });

        return result;
    }

    @Nullable
    public static JSLiteralExpression getJSLiteralExpression(@Nullable final PsiElement psiElement) {
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

    @Nullable
    public static JsonStringLiteral getJsonStringLiteral(@Nullable final PsiElement psiElement) {
        return (psiElement instanceof JsonStringLiteral) ? (JsonStringLiteral) psiElement : null;
    }

    @Nullable
    public static JsonProperty getJsonProperty(@Nullable final PsiElement psiElement) {
        return (psiElement instanceof JsonProperty) ? (JsonProperty) psiElement : null;
    }

    public static boolean isQuoted(final @Nullable String text) {
        return text != null && (text.charAt(0) == '\'' || text.charAt(0) == '"');
    }

    @Nullable
    public static PsiElement getSymbolElement(@Nullable final PsiElement sourceElement) {
        final JSLiteralExpression jsLiteralExpression = SymbolUtils.getJSLiteralExpression(sourceElement);
        if(SymbolUtils.isSymbol(jsLiteralExpression)) return jsLiteralExpression;

        final JsonStringLiteral jsonStringLiteral = SymbolUtils.getJsonStringLiteral(sourceElement);
        if(SymbolUtils.isSymbol(jsonStringLiteral)) return jsonStringLiteral;

        final JSProperty jsProperty = SymbolUtils.getJSProperty(sourceElement);
        if(SymbolUtils.isSymbol(jsProperty)) return jsProperty;

        final JsonProperty jsonProperty = SymbolUtils.getJsonProperty(sourceElement);
        if(SymbolUtils.isSymbol(jsonProperty)) return jsonProperty;

        if(!(sourceElement instanceof LeafPsiElement)) {
            return null;
        }

        final PsiElement parent = ApplicationManager.getApplication().runReadAction(new Computable<PsiElement>() {
            @Override
            public PsiElement compute() {
                return sourceElement.getParent();
            }
        });

        final JSProperty parentJsProperty = SymbolUtils.getJSProperty(parent);
        final JsonStringLiteral parentJsonStringLiteral = SymbolUtils.getJsonStringLiteral(parent);

        if(SymbolUtils.isSymbol(parentJsProperty)) return parentJsProperty;
        if(SymbolUtils.isSymbol(parentJsonStringLiteral)) return parentJsonStringLiteral;

        return null;
    }

    @Nullable
    public static PsiFile getRootPsiFile(PsiElement psiElement) {
        if(psiElement == null || psiElement.getContainingFile() == null) return null;

        if(psiElement.getContainingFile().getVirtualFile() instanceof VirtualFileWindow) {
            final DocumentWindow documentWindow = ((VirtualFileWindow)psiElement.getContainingFile().getVirtualFile()).getDocumentWindow();
            final Document document = documentWindow.getDelegate();

            final PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(psiElement.getProject());
            return psiDocumentManager.getPsiFile(document);
        }

        return psiElement.getContainingFile();
    }
}

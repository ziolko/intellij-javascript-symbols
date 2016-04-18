package com.webstorm.symbols.annotate;

import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.JBColor;
import com.intellij.util.indexing.FileBasedIndex;
import com.webstorm.symbols.SymbolUtils;
import com.webstorm.symbols.angular.AngularSymbolUtils;
import com.webstorm.symbols.index.JSSymbolsIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class SymbolAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        final PsiElement symbolPsiElement = SymbolUtils.getSymbolElement(element);
        if(symbolPsiElement == null) return;

        final String symbol = SymbolUtils.getSymbolFromPsiElement(symbolPsiElement);

        final JSLiteralExpression jsLiteralExpression = SymbolUtils.getJSLiteralExpression(symbolPsiElement);
        final JSProperty jsProperty = SymbolUtils.getJSProperty(symbolPsiElement);
        final JsonStringLiteral jsonStringLiteral = SymbolUtils.getJsonStringLiteral(symbolPsiElement);

        final TextRange range;

        if(jsLiteralExpression != null) {
            range = new TextRange(jsLiteralExpression.getTextRange().getStartOffset() + 1, jsLiteralExpression.getTextRange().getEndOffset() - 1);
        } else if(jsProperty != null && jsProperty.getNameIdentifier() != null) {
            if(symbol == null) return;
            range = ApplicationManager.getApplication().runReadAction(new Computable<TextRange>() {
                @Override
                public TextRange compute() {
                    final PsiElement nameIdentifier = jsProperty.getNameIdentifier();
                    final String text = nameIdentifier.getText();
                    final int offset = SymbolUtils.isQuoted(text) ? 1 : 0;
                    final int start = nameIdentifier.getTextRange().getStartOffset() + offset;
                    final int end = nameIdentifier.getTextRange().getEndOffset() - offset;
                    if(end <= start) return null;

                    return new TextRange(start, end);
                }
            });
        } else if(jsonStringLiteral != null) {
            range = ApplicationManager.getApplication().runReadAction(new Computable<TextRange>() {
                @Override
                public TextRange compute() {
                    final TextRange range = jsonStringLiteral.getTextRange();
                    return new TextRange(range.getStartOffset() + 1, range.getEndOffset() - 1);
                }
            });
        } else {
            return;
        }

        if(range == null) return;

        final TextAttributes textAttributes = new TextAttributes();
        textAttributes.setForegroundColor(JBColor.BLUE);

        if(isSymbolPsiElementReferenced(symbolPsiElement)) {
            holder.createInfoAnnotation(range, null).setEnforcedTextAttributes(textAttributes);
        } else {
            textAttributes.setEffectType(EffectType.WAVE_UNDERSCORE);
            textAttributes.setEffectColor(JBColor.ORANGE);
            textAttributes.setErrorStripeColor(JBColor.ORANGE);

            final Annotation annotation = holder.createWarningAnnotation(range, "This symbol is not referenced in current project.");
            annotation.setEnforcedTextAttributes(textAttributes);
        }
    }

    private boolean isSymbolPsiElementReferenced(final PsiElement psiElement) {
        final String symbol = SymbolUtils.getSymbolFromPsiElement(psiElement);

        final GlobalSearchScope searchScope = GlobalSearchScope.projectScope(psiElement.getProject());
        final Collection<VirtualFile> references = FileBasedIndex.getInstance().getContainingFiles(JSSymbolsIndex.INDEX_ID, symbol, searchScope);
        if (references.size() > 1) return true;

        final List<Integer> values = FileBasedIndex.getInstance().getValues(JSSymbolsIndex.INDEX_ID, symbol, searchScope);
        if(values.size() > 1 || (values.size() == 1 && values.get(0) > 1)) return true;

        return values.size() + AngularSymbolUtils.countReferencesInAngularDirectives(psiElement) > 1;
    }
}

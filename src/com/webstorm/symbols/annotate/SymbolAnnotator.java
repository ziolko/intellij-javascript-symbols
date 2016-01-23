package com.webstorm.symbols.annotate;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.JBColor;
import com.intellij.util.indexing.FileBasedIndex;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.webstorm.symbols.SymbolUtils;
import com.webstorm.symbols.index.JSSymbolsIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SymbolAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        final String symbol = SymbolUtils.getSymbolFromPsiElement(element);
        if(symbol == null) return;

        final TextRange range = new TextRange(element.getTextRange().getStartOffset() + 1,
                element.getTextRange().getEndOffset() - 1);

        final TextAttributes textAttributes = new TextAttributes();
        textAttributes.setForegroundColor(JBColor.BLUE);

        if(isSymbolReferenced(symbol, element)) {
            holder.createInfoAnnotation(range, null).setEnforcedTextAttributes(textAttributes);
        } else {
            textAttributes.setEffectType(EffectType.WAVE_UNDERSCORE);
            textAttributes.setEffectColor(JBColor.ORANGE);
            textAttributes.setErrorStripeColor(JBColor.ORANGE);

            final Annotation annotation = holder.createWarningAnnotation(range, "This symbol is not referenced in current project.");
            annotation.setEnforcedTextAttributes(textAttributes);
        }
    }

    private boolean isSymbolReferenced(String symbol, PsiElement element) {
        final GlobalSearchScope searchScope = GlobalSearchScope.projectScope(element.getProject());
        final Collection<VirtualFile> references = FileBasedIndex.getInstance().getContainingFiles(JSSymbolsIndex.INDEX_ID, symbol, searchScope);
        if (references.size() == 0) return false;
        if (references.size() > 1) return true;

        final List<Integer> values = FileBasedIndex.getInstance().getValues(JSSymbolsIndex.INDEX_ID, symbol, searchScope);
        return values.size() != 0 && (values.size() > 1 || values.get(0) > 1);
    }
}

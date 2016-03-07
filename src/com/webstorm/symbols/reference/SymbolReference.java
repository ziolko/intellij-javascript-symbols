package com.webstorm.symbols.reference;

import com.intellij.lang.ASTNode;
import com.intellij.lang.javascript.JSElementTypes;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.impl.JSChangeUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.webstorm.symbols.SymbolUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

class SymbolReference implements PsiReference {
    private PsiElement baseElement, element;

    public SymbolReference(PsiElement baseElement, PsiElement element) {
        this.baseElement = baseElement;
        this.element = element;
    }

    @Override
    public PsiElement getElement() {
        return element;
    }

    @Override
    public TextRange getRangeInElement() {
        final JSProperty jsProperty = SymbolUtils.getJSProperty(element);

        if(jsProperty != null) {
            PsiElement nameIdentifier = ApplicationManager.getApplication().runReadAction(new Computable<PsiElement>() {
                @Override
                public PsiElement compute() {
                    return jsProperty.getNameIdentifier();
                }
            });

            if(nameIdentifier != null) return new TextRange(0, nameIdentifier.getTextLength());
        }

        return new TextRange(0, element.getTextLength());
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return baseElement;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return element.getText();
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        final JSLiteralExpression jsLiteralExpression = SymbolUtils.getJSLiteraExpression(element);
        final JSProperty jsProperty = SymbolUtils.getJSProperty(element);

        if(jsLiteralExpression != null) {
            final ElementManipulator<JSLiteralExpression> manipulator = ElementManipulators.getManipulator(jsLiteralExpression);
            return manipulator.handleContentChange(jsLiteralExpression, newElementName);
        }

        if(jsProperty != null) {
            ASTNode renamedNode = jsProperty.findNameIdentifier();
            ASTNode parent = jsProperty.getNode();
            if(renamedNode != null && renamedNode.getElementType() == JSElementTypes.REFERENCE_EXPRESSION && renamedNode.getFirstChildNode() != null) {
                parent = renamedNode;
                renamedNode = renamedNode.getFirstChildNode();
            }

            assert renamedNode != null;
            ASTNode nameElement;
            try {
                nameElement = JSChangeUtil.createNameIdentifier(jsProperty.getProject(), newElementName, renamedNode.getElementType());
            } catch(Exception e) {
                nameElement = JSChangeUtil.createNameIdentifier(jsProperty.getProject(), newElementName, JSTokenTypes.STRING_LITERAL);
            }

            parent.replaceChild(renamedNode, nameElement);
        }

        return element;
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        throw new IncorrectOperationException("Not implemented");
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return Objects.equals(element.getText(), baseElement.getText());
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    @Override
    public boolean isSoft() {
        return true;
    }
}
package com.webstorm.symbols.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.javascript.psi.JSExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.lang.javascript.psi.stubs.JSElementIndexingData;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import com.webstorm.symbols.SymbolUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SymbolLiteralExpressionImpl implements JSLiteralExpression, PsiNamedElement {
    private JSLiteralExpression impl;

    public SymbolLiteralExpressionImpl(JSLiteralExpression impl) {
        this.impl = impl;
    }

    @Nullable
    @Override
    public String getName() {
        if(!SymbolUtils.isSymbol(impl)) return impl.getName();
        return SymbolUtils.getSymbolFromPsiElement(impl);
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        return this;
    }


    @Override
    @NotNull
    public SearchScope getUseScope() {
        return GlobalSearchScope.projectScope(impl.getProject());
    }

    /*** Methods delegated to JSLiteralExpression ***/
    @Override
    @NotNull
    public PsiReference[] getReferences() {
        return impl.getReferences();
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        impl.accept(visitor);
    }

    @Override
    public boolean isQuotedLiteral() {
        return impl.isQuotedLiteral();
    }

    @Override
    public boolean isRegExpLiteral() {
        return impl.isRegExpLiteral();
    }

    @Override
    public boolean isBinaryLiteral() {
        return impl.isBinaryLiteral();
    }

    @Override
    public boolean isOctalLiteral() {
        return impl.isOctalLiteral();
    }

    @Override
    public boolean isHexLiteral() {
        return impl.isHexLiteral();
    }

    @Override
    public boolean isNumericLiteral() {
        return impl.isNumericLiteral();
    }

    @Override
    public boolean isBooleanLiteral() {
        return impl.isBooleanLiteral();
    }

    @Override
    public JSExpression replace(JSExpression other) {
        return impl.replace(other);
    }

    @Override
    @Nullable
    public String getValueAsPropertyName() {
        return impl.getValueAsPropertyName();
    }

    @Nullable
    @Override
    public String getSignificantValue() {
        return impl.getSignificantValue();
    }

    @Override
    @Nullable
    public Object getValue() {
        return impl.getValue();
    }

    @Override
    @Nullable
    public JSElementIndexingData getIndexingData() {
        return impl.getIndexingData();
    }

    @Override
    @NotNull
    public Language getLanguage() {
        return impl.getLanguage();
    }

    @Override
    public String toString() {
        return impl.toString();
    }

    @Nullable
    @Override
    public ItemPresentation getPresentation() {
        return impl.getPresentation();
    }

    @Override
    public PsiElement addBefore(@NotNull PsiElement element, PsiElement anchor) throws IncorrectOperationException {
        return impl.addBefore(element, anchor);
    }

    @Override
    public PsiElement addAfter(@NotNull PsiElement element, PsiElement anchor) throws IncorrectOperationException {
        return impl.addAfter(element, anchor);
    }

    @Override
    public PsiElement addRangeBefore(@NotNull PsiElement first, @NotNull PsiElement last, PsiElement anchor) throws IncorrectOperationException {
        return impl.addRangeBefore(first, last, anchor);
    }

    @Override
    public PsiElement addRangeAfter(PsiElement first, PsiElement last, PsiElement anchor) throws IncorrectOperationException {
        return impl.addRangeAfter(first, last, anchor);
    }

    @Override
    public PsiElement add(@NotNull PsiElement element) throws IncorrectOperationException {
        return impl.add(element);
    }

    @Override
    public PsiElement addRange(PsiElement first, PsiElement last) throws IncorrectOperationException {
        return impl.addRange(first, last);
    }

    @Override
    public PsiElement replace(@NotNull PsiElement newElement) throws IncorrectOperationException {
        return impl.replace(newElement);
    }

    @Override
    public PsiElement getParent() {
        return impl.getParent();
    }

    @Override
    @NotNull
    public GlobalSearchScope getResolveScope() {
        return impl.getResolveScope();
    }

    @Override
    @NotNull
    public ASTNode getNode() {
        return impl.getNode();
    }

    @Override
    @NotNull
    public PsiFile getContainingFile() {
        return impl.getContainingFile();
    }

    @Override
    public boolean isWritable() {
        return impl.isWritable();
    }

    @Override
    public boolean isValid() {
        return impl.isValid();
    }

    @Override
    public PsiManager getManager() {
        return impl.getManager();
    }

    @Override
    @NotNull
    public Project getProject() {
        return impl.getProject();
    }

    @Override
    public boolean isPhysical() {
        return impl.isPhysical();
    }

    @Nullable
    @Override
    public PsiElement getContext() {
        return impl.getContext();
    }

    @Override
    @NotNull
    public PsiElement[] getChildren() {
        return impl.getChildren();
    }

    @Override
    public PsiElement getFirstChild() {
        return impl.getFirstChild();
    }

    @Override
    public PsiElement getLastChild() {
        return impl.getLastChild();
    }

    @Override
    public PsiElement getNextSibling() {
        return impl.getNextSibling();
    }

    @Override
    public PsiElement getPrevSibling() {
        return impl.getPrevSibling();
    }

    @Override
    public TextRange getTextRange() {
        return impl.getTextRange();
    }

    @Override
    public int getStartOffsetInParent() {
        return impl.getStartOffsetInParent();
    }

    @Override
    public int getTextLength() {
        return impl.getTextLength();
    }

    @Nullable
    @Override
    public PsiElement findElementAt(int offset) {
        return impl.findElementAt(offset);
    }

    @Override
    public int getTextOffset() {
        return impl.getTextOffset();
    }

    @Override
    public String getText() {
        return impl.getText();
    }

    @Override
    @NotNull
    public char[] textToCharArray() {
        return impl.textToCharArray();
    }

    @Override
    public boolean textContains(char c) {
        return impl.textContains(c);
    }

    @Nullable
    @Override
    public <T> T getCopyableUserData(@NotNull Key<T> key) {
        return impl.getCopyableUserData(key);
    }

    @Override
    public <T> void putCopyableUserData(@NotNull Key<T> key, T value) {
        impl.putCopyableUserData(key, value);
    }

    @Override
    public PsiElement copy() {
        return impl.copy();
    }

    @Override
    public void checkAdd(@NotNull PsiElement element) throws IncorrectOperationException {
        impl.checkAdd(element);
    }

    @Override
    public void delete() throws IncorrectOperationException {
        impl.delete();
    }

    @Override
    public void checkDelete() throws IncorrectOperationException {
        impl.checkDelete();
    }

    @Override
    public void deleteChildRange(PsiElement first, PsiElement last) throws IncorrectOperationException {
        impl.deleteChildRange(first, last);
    }

    @Override
    public void acceptChildren(@NotNull PsiElementVisitor visitor) {
        impl.acceptChildren(visitor);
    }

    @Nullable
    @Override
    public PsiReference getReference() {
        return impl.getReference();
    }

    @Nullable
    @Override
    public PsiReference findReferenceAt(int offset) {
        return impl.findReferenceAt(offset);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        return impl.processDeclarations(processor, state, lastParent, place);
    }

    @Override
    @NotNull
    public PsiElement getNavigationElement() {
        return impl.getNavigationElement();
    }

    @Override
    public PsiElement getOriginalElement() {
        return impl.getOriginalElement();
    }

    @Override
    public void navigate(boolean requestFocus) {
        impl.navigate(requestFocus);
    }

    @Override
    public boolean canNavigate() {
        return impl.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return impl.canNavigateToSource();
    }

    @Override
    public boolean isEquivalentTo(PsiElement another) {
        return impl.isEquivalentTo(another);
    }

    @Override
    public boolean textMatches(@NotNull CharSequence text) {
        return impl.textMatches(text);
    }

    @Override
    public boolean textMatches(@NotNull PsiElement element) {
        return impl.textMatches(element);
    }

    @Override
    @Nullable
    public Icon getIcon(int flags) {
        return impl.getIcon(flags);
    }

    @Nullable
    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        return impl.getUserData(key);
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {
        impl.putUserData(key, value);
    }
}

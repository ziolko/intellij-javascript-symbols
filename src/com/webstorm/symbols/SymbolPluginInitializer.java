package com.webstorm.symbols;

import com.intellij.codeInsight.TargetElementEvaluator;
import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.json.JsonLanguage;
import com.intellij.lang.LanguageExtension;
import com.intellij.lang.javascript.JavaScriptSupportLoader;
import com.intellij.lang.javascript.JavascriptLanguage;
import com.intellij.openapi.components.ApplicationComponent;
import com.webstorm.symbols.reference.SymbolReferenceTargetEvaluator;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class SymbolPluginInitializer implements ApplicationComponent {
    @Override
    public void initComponent() {
        final TargetElementUtilBase targetElementUtil = TargetElementUtilBase.getInstance();

        try {
            final Field targetElementEvaluator = targetElementUtil.getClass().getDeclaredField("targetElementEvaluator");
            boolean wasAccessible = targetElementEvaluator.isAccessible();

            targetElementEvaluator.setAccessible(true);

            // Explicitly add my target evaluator
            final LanguageExtension<TargetElementEvaluator> targetEvaluatorList = (LanguageExtension<TargetElementEvaluator>) targetElementEvaluator.get(targetElementUtil);
            targetEvaluatorList.addExplicitExtension(JavascriptLanguage.INSTANCE, new SymbolReferenceTargetEvaluator());
            targetEvaluatorList.addExplicitExtension(JavaScriptSupportLoader.ECMA_SCRIPT_6, new SymbolReferenceTargetEvaluator());
            targetEvaluatorList.addExplicitExtension(JsonLanguage.INSTANCE, new SymbolReferenceTargetEvaluator());

            targetElementEvaluator.setAccessible(wasAccessible);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "Symbol-Initializer";
    }

}

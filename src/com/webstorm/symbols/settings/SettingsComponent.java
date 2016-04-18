package com.webstorm.symbols.settings;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.indexing.FileBasedIndex;
import com.webstorm.symbols.angular.AngularSymbolsIndex;
import com.webstorm.symbols.index.JSSymbolsIndex;
import org.jetbrains.annotations.NotNull;

public class SettingsComponent implements com.intellij.openapi.components.ApplicationComponent {
    private static final String REG_EXP_KEY = "com.webstor.symbols.reg-exp";

    public static SettingsComponent getInstance() {
        return ApplicationManager.getApplication().getComponent(SettingsComponent.class);
    }

    public String[] getRegExp() {
        final String[] result = PropertiesComponent.getInstance().getValues(REG_EXP_KEY);
        return (result != null) ? result : new String[] { "^:[a-zA-Z0-9]+[a-zA-Z0-9\\-]*$" };
    }

    public void setRegExp(final @NotNull String[] regExp) {
        FileBasedIndex.getInstance().requestRebuild(JSSymbolsIndex.INDEX_ID);
        FileBasedIndex.getInstance().requestRebuild(AngularSymbolsIndex.INDEX_ID);

        PropertiesComponent.getInstance().setValues(REG_EXP_KEY, regExp);
    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "JavaScript symbols settings component";
    }
}

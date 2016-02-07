package com.webstorm.symbols.settings;

import com.google.common.collect.Lists;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SettingsForm implements Configurable {
    private JPanel myPanel;
    private RegExpListPanel myListPanel;

    @Nls
    @Override
    public String getDisplayName() {
        return "JavaScript Symbols";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return myPanel;
    }

    @Override
    public boolean isModified() {
        final String[] oldValue = SettingsComponent.getInstance().getRegExp();
        final Object[] newValue = myListPanel.getListItems();

        return !Arrays.equals(oldValue, newValue);
    }

    @Override
    public void apply() throws ConfigurationException {
        Object[] options = myListPanel.getListItems();
        String[] result = new String[options.length];

        for(int i = 0; i < options.length; i++) result[i] = (String) options[i];

        SettingsComponent.getInstance().setRegExp(result);
    }

    @Override
    public void reset() {
        final ArrayList<String> options = Lists.newArrayList(SettingsComponent.getInstance().getRegExp());
        myListPanel.resetFrom(options);
    }

    @Override
    public void disposeUIResources() {

    }

    private void createUIComponents() {
        final ArrayList<String> options = Lists.newArrayList(SettingsComponent.getInstance().getRegExp());
        myListPanel = new RegExpListPanel("JavaScript Symbol regular expressions", options);
    }
}

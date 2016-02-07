package com.webstorm.symbols.settings;

import com.intellij.ui.AddEditDeleteListPanel;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RegExpListPanel extends AddEditDeleteListPanel<String> {
    public RegExpListPanel(String title, List<String> initialList) {
        super(title, initialList);
    }

    @Nullable
    @Override
    protected String editSelectedItem(String item) {
        final RegExpFormDialog regExpFormDialog = new RegExpFormDialog(item);

        if(regExpFormDialog.showAndGet()) {
            return regExpFormDialog.getRegExp();
        }

        return null;
    }

    @Nullable
    @Override
    protected String findItemToAdd() {
        return editSelectedItem("");
    }

    void resetFrom(List<String> patterns) {
        myListModel.clear();
        for (String pattern : patterns) {
            myListModel.addElement(pattern);
        }
    }
};
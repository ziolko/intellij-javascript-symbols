package com.webstorm.symbols.settings;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import org.intellij.lang.regexp.RegExpLanguage;
import org.intellij.lang.regexp.intention.CheckRegExpForm;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class RegExpFormDialog extends DialogWrapper {
    private final CheckRegExpForm checkRegExpForm;
    private final PsiFile psiFile;

    RegExpFormDialog(String regExp) {
        super(false);
        setTitle("JavaScript Symbol RegExp Definition");

        final Project[] projects = ProjectManager.getInstance().getOpenProjects();
        final Project project = projects.length > 0 ? projects[0] :  ProjectManager.getInstance().getDefaultProject();

        psiFile = PsiFileFactory.getInstance(project).createFileFromText(RegExpLanguage.INSTANCE, regExp);
        checkRegExpForm = new CheckRegExpForm(psiFile);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return checkRegExpForm.getRootPanel();
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return checkRegExpForm.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        return super.doValidate();
    }

    public String getRegExp() {
        return psiFile.getText();
    }
}




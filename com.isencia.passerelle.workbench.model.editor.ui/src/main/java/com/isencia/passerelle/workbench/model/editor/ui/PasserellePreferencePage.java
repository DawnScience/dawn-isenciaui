package com.isencia.passerelle.workbench.model.editor.ui;

import java.io.File;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.isencia.passerelle.project.repository.api.RepositoryService;

public class PasserellePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
  public static final String SUBMODEL_DRILLDOWN = "com.isencia.passerelle.submodel.drilldown";
  public PasserellePreferencePage() {
    super();
    setPreferenceStore(Activator.getDefault().getPreferenceStore());
    setDescription("Preferences for the workflow editor and for running the workflow.");

  }

  @Override
  protected void createFieldEditors() {
    final BooleanFieldEditor expert = new BooleanFieldEditor(PreferenceConstants.EXPERT, "Expert mode", getFieldEditorParent());
    addField(expert);

    final DirectoryFieldEditor subModelRoot = new DirectoryFieldEditor(RepositoryService.SUBMODEL_ROOT, "Submodel Root", getFieldEditorParent());
    addField(subModelRoot);
    final BooleanFieldEditor submodelDrillDown = new BooleanFieldEditor(SUBMODEL_DRILLDOWN, "Open submodel in separate editor", getFieldEditorParent());
    addField(submodelDrillDown);

  }

  public void init(IWorkbench workbench) {
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    String submodelPath = store.getString(RepositoryService.SUBMODEL_ROOT);
    if (submodelPath == null || submodelPath.trim().equals("")) {
      File userHome = new File(System.getProperty("user.home"));
      File defaultSubmodelPath = new File(userHome, ".passerelle/submodel-repository");
      submodelPath = System.getProperty(RepositoryService.SUBMODEL_ROOT, defaultSubmodelPath.getAbsolutePath());
      store.setValue(RepositoryService.SUBMODEL_ROOT, submodelPath);

    }
  }

}

package com.isencia.passerelle.workbench.model.editor.ui.editpart;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ptolemy.kernel.util.ChangeListener;
import ptolemy.kernel.util.ChangeRequest;
import ptolemy.kernel.util.Changeable;
import ptolemy.kernel.util.NamedObj;

import com.isencia.passerelle.workbench.model.editor.ui.Activator;
import com.isencia.passerelle.workbench.model.editor.ui.INameable;
import com.isencia.passerelle.workbench.model.editor.ui.PreferenceConstants;
import com.isencia.passerelle.workbench.model.editor.ui.properties.EntityPropertySource;
import com.isencia.passerelle.workbench.model.editor.ui.views.ActorAttributesView;
import com.isencia.passerelle.workbench.model.ui.command.AttributeCommand;
import com.isencia.passerelle.workbench.model.ui.command.ChangeActorPropertyCommand;
import com.isencia.passerelle.workbench.model.ui.command.IRefreshConnections;
import com.isencia.passerelle.workbench.model.ui.command.RenameCommand;
import com.isencia.passerelle.workbench.model.ui.command.SetConstraintCommand;
import com.isencia.passerelle.workbench.model.ui.utils.EclipseUtils;
import com.isencia.passerelle.workbench.model.utils.ModelChangeRequest;
import com.isencia.passerelle.workbench.model.utils.ModelUtils;

/**
 * Base Edit Part
 */
abstract public class AbstractBaseEditPart extends
		org.eclipse.gef.editparts.AbstractGraphicalEditPart implements
		ChangeListener {

	protected Set<Image> images = new HashSet<Image>();

	public Set<Image> getImages() {
		return images;
	}

	protected Image createImage(ImageDescriptor imageDescriptor) {
		Image image = imageDescriptor.createImage();
		images.add(image);
		return image;
	}

	public AbstractBaseEditPart() {
		super();
	}

	private static final Logger logger = LoggerFactory
			.getLogger(AbstractBaseEditPart.class);

	protected IPropertySource propertySource = null;

	private IPropertyChangeListener expertUpdater;

	public void activate() {

		if (isActive())
			return;
		super.activate();
		if (getEntity() instanceof Changeable) {
			Changeable changeable = (Changeable) getEntity();
			changeable.addChangeListener(this);
		}

		if (expertUpdater == null)
			expertUpdater = new IPropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent event) {
					if (event.getProperty().equals(PreferenceConstants.EXPERT)) {
						final PropertySheet sheet = (PropertySheet) EclipseUtils
								.getPage().findView(IPageLayout.ID_PROP_SHEET);
						if (sheet != null) {
							TabbedPropertySheetPage page = (TabbedPropertySheetPage) sheet
									.getCurrentPage();
							if (page != null)
								page.refresh();
						}
					}
				}
			};
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(
				expertUpdater);

	}

	/**
	 * Makes the EditPart insensible to changes in the model by removing itself
	 * from the model's list of listeners.
	 */
	public void deactivate() {

		Activator.getDefault().getPreferenceStore()
				.removePropertyChangeListener(expertUpdater);

		if (!isActive())
			return;
		if (getEntity() instanceof Changeable) {
			Changeable changeable = (Changeable) getEntity();
			changeable.removeChangeListener(this);
		}
		super.deactivate();
	}

	/**
	 * Returns the model associated with this as a NamedObj.
	 * 
	 * @return The model of this as an NamedObj.
	 */
	public NamedObj getEntity() {
		return (NamedObj) getModel();
	}

	/**
	 * Returns the Figure of this, as a node type figure.
	 * 
	 * @return Figure as a NodeFigure.
	 */
	protected Figure getComponentFigure() {
		return (Figure) getFigure();
	}

	/**
	 * Updates the visual aspect of this.
	 */
	public void refreshVisuals() {
		double[] location = ModelUtils.getLocation(getEntity());
		Rectangle r = new Rectangle(new Point(location[0], location[1]),
				getComponentFigure().getPreferredSize(-1, -1));
		if (getParent() instanceof GraphicalEditPart)
			((GraphicalEditPart) getParent()).setLayoutConstraint(this,
					getFigure(), r);
	}

	public Object getAdapter(Class key) {
		if (IPropertySource.class == key) {
			return getPropertySource();
		}
		return super.getAdapter(key);
	}

	protected IPropertySource getPropertySource() {
		if (propertySource == null) {
			propertySource = new EntityPropertySource(getEntity(), getFigure());
		}
		return propertySource;
	}

	@Override
	public void changeExecuted(ChangeRequest changerequest) {

		getLogger().trace("Change Executed");
		Object source = changerequest.getSource();
		if (changerequest instanceof ModelChangeRequest) {
			Class<?> type = ((ModelChangeRequest) changerequest).getType();
			if (IPropertySource.class.isAssignableFrom(type) || AttributeCommand.class.isAssignableFrom(type)) {
				onChangePropertyResource(source);
				return;
			}
			if (SetConstraintCommand.class.equals(type)) {
				if (source == this.getModel() && source instanceof NamedObj) {
					refresh();
				}
				return;
			}
			if (RenameCommand.class.equals(type)) {
				if (source == this.getModel() && source instanceof NamedObj) {
					String name = getText(source);
					if ((getComponentFigure() instanceof INameable)
							&& name != null
							&& !name.equals(((INameable) getComponentFigure())
									.getName())) {
						((INameable) getComponentFigure()).setName(name);
						getFigure().repaint();
					}
				}
				return;
			}
			if (IRefreshConnections.class.isAssignableFrom(type)) {
				refreshConnections(type,source);
				return;
			}

		}

	}

	protected void refreshConnections(Class type,Object source) {
		try {
			refreshSourceConnections();
			refreshTargetConnections();
		} catch (Exception e) {

		}
	}

	protected void onChangePropertyResource(Object source) {
		if (source == this.getModel()) {
			// Execute the dummy command force a dirty state
			getViewer().getEditDomain().getCommandStack().execute(
					new ChangeActorPropertyCommand());
		}
	}

	protected String getText(Object source) {
		return ((NamedObj) source).getDisplayName();
	}

	@Override
	public void changeFailed(ChangeRequest changerequest, Exception exception) {
		getLogger().trace("Change Failed : " + exception.getMessage());
	}

	public Logger getLogger() {
		return logger;
	}

}

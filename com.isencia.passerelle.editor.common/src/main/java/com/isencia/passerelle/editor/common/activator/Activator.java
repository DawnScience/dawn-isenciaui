package com.isencia.passerelle.editor.common.activator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import ptolemy.kernel.util.NamedObj;

import com.isencia.passerelle.ext.ActorOrientedClassProvider;
import com.isencia.passerelle.ext.ModelElementClassProvider;
import com.isencia.passerelle.validation.version.VersionSpecification;

public class Activator implements BundleActivator {
  private ActorOrientedClassProviderTracker repoSvcTracker;
  private static Activator plugin;

  private ServiceRegistration apSvcReg;
  public static Activator getDefault() {
    return plugin;
  }

  public Activator() {
  }

  public void start(BundleContext context) throws Exception {

    plugin = this;
    repoSvcTracker = new ActorOrientedClassProviderTracker(context);
    repoSvcTracker.open();
    
    apSvcReg = context.registerService(ModelElementClassProvider.class.getName(), new ModelElementClassProvider() {
      public Class<? extends NamedObj> getClass(String className, VersionSpecification versionSpec) throws ClassNotFoundException {
        return (Class<? extends NamedObj>) this.getClass().getClassLoader().loadClass(className);
      }
    }, null);

  }

  public void stop(BundleContext context) throws Exception {
    apSvcReg.unregister();
    repoSvcTracker.close();
  }

  public ActorOrientedClassProvider getActorOrientedClassProvider() {
    // TODO use waitforservice
    return repoSvcTracker != null ? repoSvcTracker.repoService : null;
  }

  private static class ActorOrientedClassProviderTracker extends ServiceTracker {

    private ActorOrientedClassProvider repoService;

    public ActorOrientedClassProviderTracker(BundleContext context) {
      super(context, ActorOrientedClassProvider.class.getName(), null);
    }

    @Override
    public Object addingService(ServiceReference reference) {
      repoService = (ActorOrientedClassProvider) super.addingService(reference);
      return repoService;
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
      super.removedService(reference, service);
      repoService = null;
    }
  }
}

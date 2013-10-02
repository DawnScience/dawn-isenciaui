package com.isencia.passerelle.workbench.model.launch;

public interface IModelListener {

	public void executionStarted(final boolean requireDebug);

	public void executionTerminated(final int returnCode);
}

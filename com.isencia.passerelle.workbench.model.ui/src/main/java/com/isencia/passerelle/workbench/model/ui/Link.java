package com.isencia.passerelle.workbench.model.ui;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Link {
	private List source = new ArrayList();

	public List getSource() {
		return source;
	}

	public List getDestination() {
		return destination;
	}

	private List destination = new ArrayList();

	public Link(Enumeration source, Enumeration destination) {
		super();
		while (source.hasMoreElements()) {
			this.source.add(source.nextElement());
		}
		while (destination.hasMoreElements()) {
			this.destination.add(destination.nextElement());
		}

	}

}

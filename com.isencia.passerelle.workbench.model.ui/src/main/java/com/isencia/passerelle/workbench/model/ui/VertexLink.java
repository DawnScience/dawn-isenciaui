package com.isencia.passerelle.workbench.model.ui;

import java.io.Serializable;

import ptolemy.actor.IOPort;
import ptolemy.actor.TypedIORelation;
import ptolemy.kernel.Relation;
import ptolemy.moml.Vertex;

public class VertexLink implements Serializable {


	public VertexLink(IOPort port,Vertex vertex,boolean isSourceVertex) {
		super();
		this.relation = (TypedIORelation)vertex.getContainer();
		this.port = port;
		
		if (isSourceVertex){
			this.sourceVertex = vertex;
		}else{
			this.targetVertex = vertex;
		}
	}
	public VertexLink(Vertex vertex1,Vertex vertex2,boolean isSourceVertex) {
		super();
		this.relation = (TypedIORelation)vertex2.getContainer();
		if (isSourceVertex){
			this.targetVertex = vertex2;
			this.sourceVertex = vertex1;
		}else{
			this.sourceVertex = vertex2;
			this.targetVertex = vertex1;
		}
		
	}

	private Relation relation;
	public Relation getRelation() {
		return relation;
	}

	private IOPort port;

	public IOPort getPort() {
		return port;
	}

	private Vertex sourceVertex;
	public Vertex getSourceVertex() {
		return sourceVertex;
	}

	private Vertex targetVertex;
	public Vertex getTargetVertex() {
		return targetVertex;
	}


}

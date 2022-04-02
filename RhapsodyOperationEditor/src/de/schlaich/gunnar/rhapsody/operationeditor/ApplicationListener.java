package de.schlaich.gunnar.rhapsody.operationeditor;

import com.telelogic.rhapsody.core.IRPDiagram;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.RPApplicationListener;

public class ApplicationListener extends RPApplicationListener {

	@Override
	public boolean afterAddElement(IRPModelElement pModelElement) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean afterProjectClose(String bstrProjectName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean beforeProjectClose(IRPProject pProject) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onDiagramOpen(IRPDiagram pDiagram) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDoubleClick(IRPModelElement pModelElement) {
		// TODO Auto-generated method stub
		System.out.println("doubleClick ModelElement: " + pModelElement.getClass().toString()+ " Name: " + pModelElement.getName());
		return false;
	}

	@Override
	public boolean onFeaturesOpen(IRPModelElement pModelElement) {
		System.out.println("open Feature ModelElement: " + pModelElement.getClass().toString()+ " Name: " + pModelElement.getName());
		return false;
	}

}

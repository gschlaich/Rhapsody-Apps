package de.schlaich.gunnar.rhapsody.utilities.generateInitCode;

import java.util.Comparator;

public class ATreeNodeComparer implements Comparator<TreeNode> {

	public ATreeNodeComparer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compare(TreeNode o1, TreeNode o2) {
		
		if(o1==null)
		{
			return 0;
		}
		
		if(o2==null)
		{
			return 0;
		}
		
		if(o1.equals(o2))
		{
			return 1;
		}
		
		return 0;
	}
	
	public int getHashCode(TreeNode aTreeNode)
	{
		if(aTreeNode==null)
		{
			return 0;
		}
		
		int hashCode = aTreeNode.hashCode();
		
		int classNameHash = aTreeNode.getClass().getName().hashCode();
		
		return (hashCode ^ classNameHash); 
	}

	
}

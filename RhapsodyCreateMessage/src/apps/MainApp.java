package apps;

import java.util.List;

import com.ibm.rhapsody.apps.*;
import com.ibm.rhapsody.apps.utils.Reporter;
import com.telelogic.rhapsody.core.*;


public class MainApp extends App {
	
	String autoGenStereotypeName = "AutoGenerated";
	String autoGenHashPropertyName = "CG.Operation.AutoGenHash";

	
	/*
	* This method is called on invoking an app inside Rhapsody.
	* rhapsody - Instance of an active Rhapsody application 
	* selected - Selected element in Rhapsody
	*/
	@SuppressWarnings("unchecked")
	public void execute(IRPApplication rhapsody, IRPModelElement selected) {
		
		
		//check selected
		if(selected instanceof IRPClass == false)
		{
			print("Selected is not a class");
			return;
		}
		
		IRPClass msgClass = (IRPClass)selected;
		
		IRPProject project = msgClass.getProject();
		if(project==null)
		{
			print("No project loaded");
			return;
		}
		
		//check for generalizes
		if(msgClass.getGeneralizations().getCount()>0)
		{
			print("Selected class has already generalizations");
			return;
		}
		
		IRPModelElement owner = msgClass.getOwner();
		if(owner instanceof IRPPackage == false)
		{
			print("Selected class is not in a package");
			return;
		}
		
		String subSystemName = msgClass.getPropertyValue("CG.Class.USMSubSystemName");
		
		
		if((subSystemName==null)||(subSystemName==""))
		{
			print("Subsystem not set");
			return;
		}
		
		
		String msgName = msgClass.getName();
		
		//check for correct msg name
		
		if(msgName.startsWith("CMsg"+subSystemName)==false)
		{
			//name does not fit conventions... change to correct name
			msgName = "CMsg"+subSystemName+msgName;
			
			IRPClass existsClass = project.findClass(msgName);
			if(existsClass!=null)
			{
				print("Message " + msgName + " already exists");
				return;
			}
			
			//rename class...
			msgClass.setName(msgName);
		}
		
		
		msgClass.setPropertyValue("CPP_CG.Class.In", "const $type*");
		
		String senderName = msgName+"Sender";
		String receiverName = msgName+"Receiver";
		
		IRPClass senderClass = msgClass.addClass(senderName);
		IRPClass receiverClass = msgClass.addClass(receiverName);
		
		IRPClass IMessage = project.findClass("IMessage");
		
		if(IMessage==null)
		{
			print("IMessage not found");
			return;
		}
		
		IRPClass IMsgSender = project.findClass("IMsgSender");
		if(IMsgSender==null)
		{
			print("IMsgSender not found");
			return;
		}
		
		IRPClass IMsgReceiver = project.findClass("IMsgReceiver");
		if(IMsgReceiver==null)
		{
			print("IMsgReceiver not found");
			return;
		}
		
		msgClass.addGeneralization(IMessage);
		senderClass.addGeneralization(IMsgSender);
		receiverClass.addGeneralization(IMsgReceiver);
		
		String callbackName = "on"+msgName.substring(1);
		IRPOperation callbackOperation = receiverClass.addOperation(callbackName);
		IRPArgument aMsg = callbackOperation.addArgument("aMsg");
		aMsg.setType(msgClass);
		callbackOperation.setVisibility("Protected");
		callbackOperation.setIsVirtual(1);
		callbackOperation.setIsAbstract(1);
		setAutoGen(callbackOperation);
		
		IRPClass CSimpleAttribute = project.findClass("CSimpleAttribute");
		
		if(CSimpleAttribute==null)
		{
			print("Could not find CSimpleAttribute");
			return;
		}
				
		IRPDependency simpleAttribute = msgClass.addDependencyTo(CSimpleAttribute);
		
		simpleAttribute.addStereotype("Usage", "Dependency");
		
		IRPDependency friendDependency = msgClass.addDependencyTo(receiverClass);
		List<IRPStereotype> stereotypes = project.getAllStereotypes().toList();
		for(IRPStereotype stereotype:stereotypes)
		{
			if(stereotype.getName().equals("Friend"))
			{
				friendDependency.setStereotype(stereotype);
				break;
			}
			
		}
	
		StringBuffer describeBodyBuffer = new StringBuffer();
		
		//check for attributeNames
		List<IRPAttribute> attributes = msgClass.getAttributes().toList();
		
		for(IRPAttribute attribute:attributes)
		{
			attribute.setVisibility("Private");
			String attributeName = attribute.getName();
			
			if(attributeName.startsWith("my")==false)
			{
				attributeName = "my"+ attributeName.substring(0,1).toUpperCase()+attributeName.substring(1);
				attribute.setName(attributeName);
			}

			String contextName = attributeName.substring(2);
			
			IRPOperation getter = msgClass.addOperation("get"+contextName);
			getter.setIsConst(1);
			String getterBody = "return "+attributeName+";";
			
			getter.setBody(getterBody);
			getter.setReturns(attribute.getType());
			
			setAutoGen(getter);
			
			describeBodyBuffer.append(" context.set(\""+contextName+"\", "+attributeName+"); \n");
			
		}
		
		IRPOperation describeOperation = msgClass.addOperation("describe");
		
		IRPClass ISerContext = project.findClass("ISerContext");
		if(ISerContext==null)
		{
			print("Could not find class ISerContext");
			return; 
		}
		
		IRPArgument context = describeOperation.addArgument("context");
		
		context.setType(ISerContext);
		context.setArgumentDirection("InOut");
		
		describeOperation.setIsVirtual(1);
		describeOperation.setBody(describeBodyBuffer.toString());
		setAutoGen(describeOperation);
		
		IRPOperation accept = msgClass.addOperation("accept");
		accept.setIsConst(1);
		
		IRPArgument aReceiver = accept.addArgument("aReceiver");
		
		aReceiver.setType(IMsgReceiver);
		aReceiver.setPropertyValue("CPP_CG.Class.In", "$type*");
		
		StringBuffer acceptBody = new StringBuffer();
		
		acceptBody.append(receiverName + "* recv = ("+ receiverName +"*)aReceiver;\n");
		acceptBody.append("recv->"+callbackName+"(this);\n");
		
		accept.setBody(acceptBody.toString());
		setAutoGen(accept);
		
		StringBuffer attributesConstr = new StringBuffer();
		String initializer = "";
		
		//create constructor arguments
		for(IRPAttribute attribute:attributes)
		{
			String argumentName = "a"+attribute.getName().substring(2);
			
			attributesConstr.append(argumentName);
			attributesConstr.append(",");
			attributesConstr.append(attribute.getType().getName());
			attributesConstr.append(",");
			initializer+=(attribute.getName()+"("+argumentName+"),");	
		}
		
		
		IRPOperation constructor = msgClass.addConstructor(attributesConstr.toString());
		
		List<IRPArgument> arguments = constructor.getArguments().toList();
		int i = 0;
		for(IRPArgument argument:arguments)
		{
			argument.setType(attributes.get(i).getType());
			i++;
		}
		constructor.setInitializer(initializer.substring(0, initializer.length()-1));
		
		String sendMsgName = "send"+msgName.substring(1);
		
		IRPOperation sendMsg = senderClass.addOperation(sendMsgName);
		sendMsg.setReturns(project.findType("long"));
		setAutoGen(sendMsg);
		
		String sendMsgBody = "return send(new " + msgName + "(";
		
		for(IRPAttribute attribute:attributes)
		{
			String argName = "a"+attribute.getName().substring(2);
			IRPArgument arg = sendMsg.addArgument(argName);
			arg.setType(attribute.getType());
			
			sendMsgBody+=argName+", ";
		}
		
		sendMsgBody = sendMsgBody.substring(0, sendMsgBody.length()-2);
		sendMsgBody+="));";
		
		sendMsg.setBody(sendMsgBody);
		
		IRPOperation recvConstructor = receiverClass.addConstructor("");
		recvConstructor.setInitializer("IMsgReceiver(&theMsgChannel)");
		setAutoGen(recvConstructor);
		
		IRPOperation sendConstructor = senderClass.addConstructor("");
		sendConstructor.setInitializer("IMsgSender(&theMsgChannel)");
		sendConstructor.setVisibility("Protected");
		setAutoGen(sendConstructor);
		
		print("Message "+msgName+" generated");
		
		//set StandardOperations
		String standardOperationsKey = "CG.Class.StandardOperations";
		String standardOperations = "privateCopyCtor,privateAssignmentOp,USMMem,USMMsg";
		
		msgClass.setPropertyValue(standardOperationsKey, standardOperations);
		
		standardOperations = "privateCopyCtor,privateAssignmentOp,USMMsgReceiver";
		receiverClass.setPropertyValue(standardOperationsKey, standardOperations);
		
		standardOperations = "privateCopyCtor,privateAssignmentOp,USMMsgSender";
		senderClass.setPropertyValue(standardOperationsKey, standardOperations);

	}	
	
	private void print(String aMsg)
	{
		println(aMsg);
		System.out.println(aMsg);
	}
	
	private void setAutoGen(IRPOperation aOperation)
	{	
		aOperation.addStereotype(autoGenStereotypeName, "Operation");
		String body = aOperation.getBody();
		int hash = genHash(aOperation);
		aOperation.setPropertyValue(autoGenHashPropertyName, Integer.toHexString(hash));
	}
	
	private Boolean isAutoGen(IRPOperation aOperation)
	{
		String hashString = aOperation.getPropertyValue(autoGenHashPropertyName);
		int hash = 0;
		if(hashString.isEmpty())
		{
			return false;
		}
		try
		{
			hash = Integer.parseInt(hashString,16);
		}
		catch( NumberFormatException e)
		{
			return false;
		}
		int bHash = genHash(aOperation);
		if(bHash==0)
		{
			return true;
		}
		if(bHash==hash)
		{
			return true;
		}
		return false;
	}
	
	private int genHash(IRPOperation aOperation)
	{
		String body = aOperation.getBody();
		if(body==null)
		{
			return 0;
		}
		if(body.equals(""))
		{
			return 0;
		}
		body = body.replaceAll("\\s+", "");
		int hash = body.hashCode();
		return hash;
	}
	
	
	
	
    /*
     *  Debug app by launching it as "Java Application" is Eclipse.
	 *  Note: Select an element in Rhapsody in order to simulate launching app on a selected element in the browser.
     */	
	public static void main(String[] args) {
		
		MainApp app = new MainApp();
		String connectionstring = null;
		
		if (args.length >= 1) 
		{
			connectionstring = args[0];
		}
		executeApp(app, connectionstring);
		
	}	
	
	public static void executeApp(App aApp, String aConnectionstring)
	{
		IRPApplication actualApp = null;
		
		if(aConnectionstring!=null)
		{
			try
			{
				actualApp = RhapsodyAppServer.getActiveRhapsodyApplicationByID(aConnectionstring);
			}
			catch(Exception e)
			{
				System.out.println("connectionstring "+ aConnectionstring + " is not an active rhapsody application ");
			}
		}
		else
		{
			System.out.println("no connectionstring set");
		}
		
		if(actualApp==null)
		{
        
			aApp.invokeFromMain();
			return;
		}
		
		IRPModelElement selectedElement = actualApp.getSelectedElement();
		
		
		if(aConnectionstring!=null)
		{
			actualApp.writeToOutputWindow("log", "ConnectiongString: " + aConnectionstring + "\n");
		}
		else
		{
			actualApp.writeToOutputWindow("log", "ConnectiongString was null\n");
		}
		
		actualApp.writeToOutputWindow("log", "start...\n");
		
		aApp.execute(actualApp, selectedElement);
	}
}

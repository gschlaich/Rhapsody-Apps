		File rhapsodyShareFile = new File(System.getenv("OMROOT"));

		if (rhapsodyShareFile.exists() == false)
		{
			trace("Rhapsody Share file does not exist");

		}

		File rhapsodyFile = rhapsodyShareFile.getParentFile();

		if (rhapsodyFile.exists() == false)
		{
			trace("Rhapsody file does not exist");

		}

		ProcessBuilder processBuilder = new ProcessBuilder(rhapsodyFile.getAbsolutePath() + "\\rhapsody.exe",
				"-hiddenui");

		try
		{
			Process p = processBuilder.start();
		}
		catch(IOException e)
		{
			trace(e.getMessage());

		}

		// wait for rhapsody to start
		try
		{
			Thread.sleep(1000);
		}
		catch(InterruptedException e)
		{
			trace(e.getMessage());

		}

		List<String> idList = RhapsodyAppServer.getActiveRhapsodyApplicationIDList();

		for (String id : idList)
		{
			myRhapsodyApp = RhapsodyAppServer.getActiveRhapsodyApplicationByID(id);
			if (myRhapsodyApp.activeProject() == null)
			{
				tempApp = myRhapsodyApp;// no project loaded..
			}
		}

		if (tempApp == null)
		{
			trace("No empty Rhapsody Application found");

		}

		// tempApp.setHiddenUI(true);
		return tempApp;

	}

	/**
	 * Creates a new Rhapsody project in a temporary directory and imports a .sbs or
	 * .sbsx file
	 * 
	 * @param sbsFilePath  The absolute path to the .sbs or .sbsx file to import
	 * @param tempDirPath  The temporary directory where the project will be created
	 * @param aTraceAction Optional trace action for logging (overrides global trace
	 *                     action for this call)
	 * @return An IRPProject instance with the imported file, or null if creation
	 *         fails
	 */
	public IRPProject createProjectWithSBS(String sbsFilePath, String tempDirPath)
	{

		// Validate input files
		File sbsFile = new File(sbsFilePath);
		if (!sbsFile.exists())
		{
			trace("SBS/SBSX file does not exist: " + sbsFilePath);
			return null;
		}

		String fileExtension = sbsFilePath.toLowerCase();
		if (!fileExtension.endsWith(".sbs") && !fileExtension.endsWith(".sbsx"))
		{
			trace("File is not a .sbs or .sbsx file: " + sbsFilePath);
			return null;
		}

		// Create temp directory if it doesn't exist
		File tempDir = new File(tempDirPath);
		if (!tempDir.exists())
		{
			if (!tempDir.mkdirs())
			{
				trace("Failed to create temporary directory: " + tempDirPath);
				return null;
			}
		}

		// Generate a unique project name based on the SBS file name
		String projectName = sbsFile.getName();
		projectName = projectName.substring(0, projectName.lastIndexOf('.')); // Remove extension
		projectName = "Project_" + System.currentTimeMillis() + "_" + projectName;

		trace("Creating new project: " + projectName);
		trace("SBS file: " + sbsFilePath);
		trace("Project directory: " + tempDirPath);

		// Create a temporary Rhapsody application
		if (myRhapsodyApp == null)
		{
			createTempRhapsodyApp();
		}

		if (myRhapsodyApp == null)
		{
			trace("Failed to create temporary Rhapsody application");
			return null;
		}

		try
		{

			// check for root folder
			File rootFolder = sbsFile.getParentFile();

			// 7 times parent to get to the root folder of the project
			for (int i = 0; i < 7; i++)
			{

				if (rootFolder != null)
				{
					rootFolder = rootFolder.getParentFile();
				}
			}

			trace("Root folder: " + (rootFolder != null ? rootFolder.getAbsolutePath() : "null"));

			File templateProjectFile = new File(rootFolder, myTemplateProjectName);

			// load Template project
			myProject = myRhapsodyApp.openProject(templateProjectFile.toString());

			if (myProject == null)
			{
				trace("Failed to open template project: " + templateProjectFile.toString());
				return null;
			}

			trace("Template project opened successfully: " + templateProjectFile.toString());

			// Save the project to the temporary directory
			String tempProjectFilePath = new File(tempDir, projectName + ".rpy").getAbsolutePath();

			myProject.saveAs(tempProjectFilePath);

		}
		catch(Exception e)
		{
			trace("Exception creating project: " + e.getMessage());
			e.printStackTrace();
			return null;
		}

		// Add the SBS file to the model using addToModelEx
		try
		{
			trace("Adding SBS file to model: " + sbsFilePath);

			myRhapsodyApp.addToModelEx(sbsFilePath, IRPApplication.AddToModel_Mode.AS_REFERENCE, 1, 0);

			trace("SBS file successfully added to model");
		}
		catch(Exception e)
		{
			trace("Failed to add SBS file to model using addToModelEx: " + e.getMessage());
			e.printStackTrace();
			return null;
		}

		trace("Project with SBS file created successfully");
		
		IRPModelElement addedElement = myProject.find
		
		if (addedElement != null)
		{
			trace("Added element found: " + addedElement.getName());
			IRPComponent activeComponent = myProject.getActiveComponent();
			if (activeComponent != null)
			{
				activeComponent.addScopeElement(addedElement);
			}			
		}
		else
		{
			trace("Added element not found in project");
		}
		
		

		trace("Saving project...");
		myProject.save();
		trace("Project saved successfully");

		return myProject;
	}

	

	public void closeProject()
	{

		myProject.close();
		
		myRhapsodyApp.quit();
		
	}

}
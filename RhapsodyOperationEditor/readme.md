# Rhapsody Operation Editor
A C++ editor for operations in IBM rhapsody. This is a IBM Rhapsody Extension. Uses RSyntaxTextarea java C++ Editor with AutoComplete and Eclipse CDT as AST Parser. It supports simple syntax checking, check if Type is known and operational roundtrips.

Tested with Rhapsody C++ Developer 8.4 - C++ Developer 10.0.2

<img width="974" height="502" alt="image" src="https://github.com/user-attachments/assets/f8c265c9-5b91-4402-97ff-34776bea4081" />


Additionally required java libraries (not included):

- jdom.jar ( part of the Rhapsody installation)
- swt.jar ( part of the Rhapsody installation)
- rhapsody.jar ( part of the Rhapsody installation)
- apps.jar ( part of the Rhapsody installation)
- cdt.jar ( https://www.eclipse.org/cdt/ )
- equinox.common.jar ( https://www.eclipse.org/equinox/ )
- several other jar libraries, will be automatic added by maven

uses Maven. For the Rhapsody jar libraries use environment variable OMROOT for the path to the share folder


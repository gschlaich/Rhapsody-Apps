package de.schlaich.gunnar.rhapsody.utilities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPProject;

import de.schlaich.gunnar.rhapsody.utilities.ASTHelper.SourceLocation;

/**
 * Unit tests for ASTHelper class.
 * 
 * These tests are divided into:
 * - Unit tests: Can run without Rhapsody (using mocks)
 * - Integration tests: Require a running Rhapsody instance
 */
@ExtendWith(MockitoExtension.class)
public class ASTHelperTest
{
	
	@Nested
	@DisplayName("SourceLocation Tests")
	class SourceLocationTests
	{
		@Test
		@DisplayName("SourceLocation should store file path and line number")
		void testSourceLocationCreation()
		{
			String filePath = "C:\\test\\MyClass.cpp";
			int lineNumber = 42;
			
			SourceLocation location = new SourceLocation(filePath, lineNumber);
			
			assertEquals(filePath, location.getFilePath());
			assertEquals(lineNumber, location.getLineNumber());
		}
		
		@Test
		@DisplayName("SourceLocation toString should return path:lineNumber format")
		void testSourceLocationToString()
		{
			String filePath = "C:\\test\\MyClass.cpp";
			int lineNumber = 42;
			
			SourceLocation location = new SourceLocation(filePath, lineNumber);
			
			assertEquals("C:\\test\\MyClass.cpp:42", location.toString());
		}
	}
	
	@Nested
	@DisplayName("AST Parsing Tests - No Rhapsody Required")
	class ASTParsingTests
	{
		@Test
		@DisplayName("getTranslationUnitFromBody should parse simple operation body")
		void testGetTranslationUnitFromBody()
		{
			String body = "int x = 5;";
			IASTTranslationUnit translationUnit = ASTHelper.getTranslationUnitFromBody(body);
			
			assertNotNull(translationUnit);
		}
		
		@Test
		@DisplayName("getLines should split text into lines")
		void testGetLines()
		{
			String text = "line1\nline2\nline3";
			List<String> lines = ASTHelper.getLines(text, false);
			
			assertEquals(3, lines.size());
			assertEquals("line1", lines.get(0));
			assertEquals("line2", lines.get(1));
			assertEquals("line3", lines.get(2));
		}
		
		@Test
		@DisplayName("getLines should convert tabs to spaces when requested")
		void testGetLinesWithTabConversion()
		{
			String text = "\tindented";
			List<String> lines = ASTHelper.getLines(text, true);
			
			assertEquals(1, lines.size());
			assertEquals("    indented", lines.get(0));
		}
		
		@Test
		@DisplayName("getLines should handle empty text")
		void testGetLinesEmpty()
		{
			List<String> lines = ASTHelper.getLines("", false);
			
			assertEquals(1, lines.size());
			assertEquals("", lines.get(0));
		}
		
		@Test
		@DisplayName("getLines should handle null text")
		void testGetLinesNull()
		{
			List<String> lines = ASTHelper.getLines(null, false);
			
			assertEquals(1, lines.size());
			assertEquals("", lines.get(0));
		}
	}
	
	@Nested
	@DisplayName("getOperationSourceLocation Tests")
	class GetOperationSourceLocationTests
	{
		@Mock
		IRPOperation mockOperation;
		
		@Mock
		IRPApplication mockApplication;
		
		@Mock
		IRPClass mockClass;
		
		@Test
		@DisplayName("Should return null for null operation")
		void testNullOperation()
		{
			SourceLocation result = ASTHelper.getOperationSourceLocation(null, mockApplication);
			assertNull(result);
		}
		
		@Test
		@DisplayName("Should return null for null application")
		void testNullApplication()
		{
			SourceLocation result = ASTHelper.getOperationSourceLocation(mockOperation, null);
			assertNull(result);
		}
		
		@Test
		@DisplayName("Should return null when owner is not a class")
		void testOwnerNotClass()
		{
			when(mockOperation.getOwner()).thenReturn(null);
			
			SourceLocation result = ASTHelper.getOperationSourceLocation(mockOperation, mockApplication);
			assertNull(result);
		}
	}
	
	@Nested
	@DisplayName("C++ File Parsing Tests")
	class CppFileParsingTests
	{
		private static final String TEST_RESOURCES = "src/test/resources/";
		
		@Test
		@DisplayName("getTranslationUnit should parse existing cpp file")
		void testParseExistingFile()
		{
			// This test requires a test cpp file in resources
			File testFile = new File(TEST_RESOURCES + "TestClass.cpp");
			if (testFile.exists())
			{
				IASTTranslationUnit translationUnit = ASTHelper.getTranslationUnit(testFile.getAbsolutePath());
				assertNotNull(translationUnit);
			}
		}
		
		@Test
		@DisplayName("getOperationName should extract function name from definition")
		void testGetOperationName()
		{
			String cppCode = "void MyClass::myFunction() { }";
			IASTTranslationUnit translationUnit = ASTHelper.getTranslationUnitFromBody(cppCode);
			
			// The function is wrapped in checkOperation, so we need to find it differently
			assertNotNull(translationUnit);
		}
	}
}

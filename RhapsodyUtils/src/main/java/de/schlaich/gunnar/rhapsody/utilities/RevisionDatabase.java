package de.schlaich.gunnar.rhapsody.utilities;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

class RevisionDatabase
{
	//private static final String JDBC_URL = "jdbc:sqlite:./cache.db";

	private String myJDBC_URL = null;
	
	private static final String[] DDL = {

			// 1) unit
			"CREATE TABLE IF NOT EXISTS unit (\n" 
					+ "  unit_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
					+ "  name    TEXT    NOT NULL UNIQUE\n" 
					+ ");",

			// 2) revision
			"CREATE TABLE IF NOT EXISTS revision (\n" 
					+ "  rev_id   INTEGER PRIMARY KEY AUTOINCREMENT,\n"
					+ "  rev_code INTEGER NOT NULL UNIQUE,\n" 
					+ "  author   TEXT    NOT NULL,\n"
					+ "  date     TEXT    NOT NULL,\n" 
					+ "  message TEXT,\n" 
					+ "  jiraIssue TEXT\n" 
					+ ");",

			// 3) unit_revision
			"CREATE TABLE IF NOT EXISTS unit_revision (\n"
					+ "  unit_id   INTEGER NOT NULL REFERENCES unit(unit_id) ON DELETE CASCADE,\n"
					+ "  rev_id    INTEGER NOT NULL REFERENCES revision(rev_id) ON DELETE CASCADE,\n"
					+ "  file_hash TEXT,\n" 
					+ "  PRIMARY KEY (unit_id, rev_id)\n" 
					+ ");",

			// 4) guid
			"CREATE TABLE IF NOT EXISTS guid (\n" 
					+ "  guid     TEXT,\n"
					+ "  unit_id  INTEGER NOT NULL REFERENCES unit(unit_id) ON DELETE CASCADE,\n" 
					+ "  guid_id INTEGER PRIMARY KEY AUTOINCREMENT\n"
					+ ");",

			// 5) guid_revision
			"CREATE TABLE IF NOT EXISTS guid_revision (\n"
					+ "  guid_id INTEGER NOT NULL REFERENCES guid(guid_id) ON DELETE CASCADE,\n"
					+ "  rev_id INTEGER NOT NULL REFERENCES revision(rev_id) ON DELETE CASCADE,\n"
					+ "  PRIMARY KEY (guid_id, rev_id)\n" 
					+ ");",
					
		    // 6) guid_owner
			"CREATE TABLE IF NOT EXISTS guid_owner (\n"
					+ "  parent_guid_id INTEGER NOT NULL REFERENCES guid(guid_id) ON DELETE CASCADE,\n"
					+ "  child_guid_id INTEGER NOT NULL REFERENCES guid(guid_id) ON DELETE CASCADE,\n"
					+ "  PRIMARY KEY (parent_guid_id, child_guid_id)\n"
					+ ");",

			// Indizes
			"CREATE INDEX IF NOT EXISTS idx_gr_guid ON guid_revision(guid_id);",
			"CREATE INDEX IF NOT EXISTS idx_ur_unit ON unit_revision(unit_id);",
			"CREATE INDEX IF NOT EXISTS idx_ur_rev  ON unit_revision(rev_id);",
			"CREATE INDEX IF NOT EXISTS idx_go_child ON guid_owner(child_guid_id);"
					
	};

	private Consumer<String> myTraceAction = null;

	private Connection myConnection;

	public RevisionDatabase(File aTempDir, Consumer<String> aTraceAction)
	{
		
		if (aTempDir.exists() == false)
		{
			aTempDir.mkdirs();
		}
		
		
		myJDBC_URL = "jdbc:sqlite:" + aTempDir.getAbsolutePath() + File.separator + "cache.db";
		
		myTraceAction = aTraceAction;

		createDatabase();

	}

	private void trace(String aMessage)
	{
		if (myTraceAction == null)
		{
			// no traceaction set...
			return;
		}

		aMessage = "SVN: " + aMessage;

		myTraceAction.accept(aMessage);
	}

	void testSQLite() throws ClassNotFoundException, SQLException
	{
		Class.forName("org.sqlite.JDBC"); // Treiber laden
		try (Connection c = java.sql.DriverManager.getConnection("jdbc:sqlite::memory:"))
		{
			trace("SQLite-JDBC is working: " + c.getMetaData().getDriverVersion());
		}
	}

	private void createDatabase( )
	{

		trace("Creating Database on " + myJDBC_URL);
		
		try (Connection con = DriverManager.getConnection(myJDBC_URL))
		{
			myConnection = con;

			try (Statement st = myConnection.createStatement())
			{
				st.execute("PRAGMA journal_mode=WAL;"); // parallele Leser/Schreiber
				st.execute("PRAGMA synchronous   = NORMAL;"); // Pflicht für WAL-Performance
				st.execute("PRAGMA cache_size    = -20000;"); // ~80 MB Cache (20 000 kB)
				st.execute("PRAGMA foreign_keys  = ON;");     // FK-Prüfung nicht vergessen
			}

			try (Statement st = myConnection.createStatement())
			{
				for (String sql : DDL)
				{
					st.execute(sql);
				}
			}

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		trace("Database created");

	}

	public int addRevision(int aRevCode, String aAuthor, String aDate, String aMessage, String aJiraId)
			throws SQLException
	{
		
		try (PreparedStatement sel = myConnection.prepareStatement("SELECT rev_id FROM revision WHERE rev_code = ?"))
		{
			sel.setInt(1, aRevCode);
			try (ResultSet rs = sel.executeQuery())
			{
				if (rs.next()) return rs.getInt(1);
			}
		}
		// Revision neu anlegen
		try (PreparedStatement ins = myConnection.prepareStatement(
				"INSERT INTO revision (rev_code, author, date, message, jiraIssue) VALUES (?,?,?,?,?)",
				Statement.RETURN_GENERATED_KEYS))
		{
			ins.setInt(1, aRevCode);
			ins.setString(2, aAuthor);
			ins.setString(3, aDate);
			ins.setString(4, aMessage);
			ins.setString(5, aJiraId);
			ins.executeUpdate();
			try (ResultSet keys = ins.getGeneratedKeys())
			{
				keys.next();
				return keys.getInt(1);
			}
		}

	}

	public int addUnit(String aUnitName) throws SQLException
	{
		try (PreparedStatement sel = myConnection.prepareStatement("SELECT unit_id FROM unit WHERE name = ?"))
		{
			sel.setString(1, aUnitName);
			try (ResultSet rs = sel.executeQuery())
			{
				if (rs.next()) return rs.getInt(1);
			}
		}

		// Neu anlegen
		try (PreparedStatement ins = myConnection.prepareStatement("INSERT INTO unit(name) VALUES (?)",
				Statement.RETURN_GENERATED_KEYS))
		{
			ins.setString(1, aUnitName);
			ins.executeUpdate();
			try (ResultSet keys = ins.getGeneratedKeys())
			{
				keys.next();
				return keys.getInt(1);
			}
		}
	}
	
	public boolean addGUID(String aGUID, String aUnitName) throws SQLException
	{
		int unitId = addUnit(aUnitName);
		
		int getGuidId = getGuidId(aGUID);
		
		if (getGuidId != -1)
		{
			return false;
		}

		
		try (PreparedStatement ins = myConnection.prepareStatement("INSERT INTO guid(guid, unit_id) VALUES (?,?)",
				Statement.RETURN_GENERATED_KEYS))
		{
			ins.setString(1, aGUID);
			ins.setInt(2, unitId);
			ins.executeUpdate();
			try (ResultSet keys = ins.getGeneratedKeys())
			{
				keys.next();
				return true;
			}
		}
	}
	
	
	public int getGuidId(String aGUID) throws SQLException
	{
		try (PreparedStatement sel = myConnection.prepareStatement("SELECT guid_id FROM guid WHERE guid = ?"))
		{
			sel.setString(1, aGUID);
			try (ResultSet rs = sel.executeQuery())
			{
				if (rs.next()) return rs.getInt(1);
			}
		}

		return -1;
	}
	

	private boolean hasRevisionId(int aRevisionId) throws SQLException
	{
		try (PreparedStatement ps = myConnection.prepareStatement("SELECT 1 FROM revision WHERE rev_id = ?"))
		{
			ps.setInt(1, aRevisionId);
			try (ResultSet rs = ps.executeQuery())
			{
				if (!rs.next())
				{
					return false;
				}
			}
		}
		return true;
	}

	private boolean hasUnitId(int aUnitId) throws SQLException
	{
		try (PreparedStatement ps = myConnection.prepareStatement("SELECT 1 FROM unit WHERE unit_id = ?"))
		{
			ps.setInt(1, aUnitId);
			try (ResultSet rs = ps.executeQuery())
			{
				if (!rs.next())
				{
					return false;
				}
			}
		}
		return true;
	}

	public boolean hasUnitRevision(int aUnitId, int aRevisionId) throws SQLException
	{
		if (hasRevisionId(aRevisionId) == false)
		{
			return false;
		}

		if (hasUnitId(aUnitId) == false)
		{
			return false;
		}

		return hasUnitRevisionNoCheck(aUnitId, aRevisionId);
	}
	
	public boolean addUnitRevision(String aUnitName, int aRevCode) throws SQLException
	{
		int revisionId = hasRevision(aRevCode);
		if (revisionId == -1)
		{
			return false;
		}
		int unitId = addUnit(aUnitName);
		
		
		if (hasUnitRevision(unitId, revisionId) == true)
		{
			return true;
		}
		
		// Neu anlegen

		try (PreparedStatement ins = myConnection.prepareStatement(
				"INSERT INTO unit_revision(unit_id, rev_id) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS))
		{
			ins.setInt(1, unitId);
			ins.setInt(2, revisionId);
			ins.executeUpdate();
			try (ResultSet keys = ins.getGeneratedKeys())
			{
				keys.next();
				int id = keys.getInt(1);
				if (id == -1)
				{
					return false;
				}
				
                return true;			
             }
		}

	}
	
	
	private boolean hasUnitRevisionNoCheck(int aUnitId, int aRevisionId) throws SQLException
    {
        try (PreparedStatement ps = myConnection
                .prepareStatement("SELECT 1 FROM unit_revision WHERE unit_id = ? AND rev_id = ?"))
        {
            ps.setInt(1, aUnitId);
            ps.setInt(2, aRevisionId);
            try (ResultSet rs = ps.executeQuery())
            {
                if (!rs.next())
                {
                    return false;
                }
            }
        }

        return true;
    }
	
	public int hasRevision(int aRevCode) throws SQLException
	{
		try (PreparedStatement ps = myConnection.prepareStatement("SELECT rev_id FROM revision WHERE rev_code = ?"))
		{
			ps.setInt(1, aRevCode);
			try (ResultSet rs = ps.executeQuery())
			{
				if (!rs.next())
				{
					return -1;
				}
				return rs.getInt(1);
			}
		}
	}
	
	private int hasUnit(String aUnitName) throws SQLException
	{
		
		try (PreparedStatement ps = myConnection.prepareStatement("SELECT unit_id FROM unit WHERE name = ?"))
		{
			ps.setString(1, aUnitName);
			try (ResultSet rs = ps.executeQuery())
			{
				if (!rs.next())
				{
					return -1;
				}
				return rs.getInt(1);
			}
		}
	}
	
	public boolean reopenConnection() throws SQLException
	{
		if(myConnection.isClosed())
		{
			myConnection = DriverManager.getConnection(myJDBC_URL);
		
		}
		return true;
	}
	
	public boolean hasUnitRevision(String aUnitName, int aRevCode) throws SQLException
	{
		int unitId = hasUnit(aUnitName);
		if (unitId == -1)
		{
			return false;
		}
		
		int aRevisionId = hasRevision(aRevCode);

		if (aRevisionId == -1)
		{
			return false;
		}
		
		return hasUnitRevisionNoCheck(unitId, aRevisionId);
		
	}
	
	private boolean addRevisionIdToGuidId(int aGuidId, int aRevisionId) throws SQLException
	{
		try (PreparedStatement ps = myConnection
				.prepareStatement("INSERT INTO guid_revision (guid_id, rev_id) VALUES (?, ?)"))
		{
			ps.setInt(1, aGuidId);
			ps.setInt(2, aRevisionId);
			ps.executeUpdate();
		}
		return true;
	}
	
	private boolean getRevisionIdByGuidId(int aGuidId, int aRevisionId) throws SQLException
	{
		try (PreparedStatement ps = myConnection
				.prepareStatement("SELECT 1 FROM guid_revision WHERE guid_id = ? AND rev_id = ?"))
		{
			ps.setInt(1, aGuidId);
			ps.setInt(2, aRevisionId);
			try (ResultSet rs = ps.executeQuery())
			{
				if (!rs.next())
				{
					return false;
				}
			}
		}
		return true;
	}
	
	
	public boolean addRevisionToGUID(String aGUID, int aRevCode) throws SQLException
	{
		int guidId = getGuidId(aGUID);
		
		if (guidId == -1)
		{
			return false;
		}
		
		
		int aRevisionId = hasRevision(aRevCode);

		if (aRevisionId == -1)
		{
			return false;
		}
		
		if (getRevisionIdByGuidId(guidId, aRevisionId) == true)
		{
			return true;
		}

		return addRevisionIdToGuidId(guidId, aRevisionId);

	}
	
	public boolean addChildtoGuid(String aParentGUID, String aChildGUID) throws SQLException
	{
		int parentGuidId = getGuidId(aParentGUID);
		int childGuidId = getGuidId(aChildGUID);

		if (parentGuidId == -1 || childGuidId == -1)
		{
			return false;
		}

		try (PreparedStatement ps = myConnection.prepareStatement("INSERT INTO guid_owner (parent_guid_id, child_guid_id) VALUES (?, ?)"))
		{
			ps.setInt(1, parentGuidId);
			ps.setInt(2, childGuidId);
			ps.executeUpdate();
		}

		return true;

	}
	
	

	public List<String> getGUIDsByRevision(int aRevCode) throws SQLException
	{
		int revisionId = hasRevision(aRevCode);
		if (revisionId == -1)
		{
			return null;
		}
		
		List<String> guids = new ArrayList<>();

		try (PreparedStatement pstmt = myConnection.prepareStatement("SELECT guid FROM guid_revision WHERE rev_id = ?"))
		{
			pstmt.setInt(1, revisionId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next())
			{
				String guid = rs.getString("guid");
				guids.add(guid);
			}
		}
		
		return guids;
	}
	
	public List<String> getGUIDsByUnit(String aUnitName) throws SQLException
	{
		int unitId = hasUnit(aUnitName);
		if (unitId == -1)
		{
			return null;
		}

		List<String> guids = new ArrayList<>();

		try (PreparedStatement pstmt = myConnection.prepareStatement("SELECT guid FROM guid WHERE unit_id = ?"))
		{
			pstmt.setInt(1, unitId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next())
			{
				String guid = rs.getString("guid");
				guids.add(guid);
			}
		}

		return guids;

	}
	
	public boolean isParentOf(String aParentGUID, String aChildGUID) throws SQLException
	{
		int parentGuidId = getGuidId(aParentGUID);
		int childGuidId = getGuidId(aChildGUID);

		if (parentGuidId == -1 || childGuidId == -1)
		{
			return false;
		}

		try (PreparedStatement ps = myConnection
				.prepareStatement("SELECT 1 FROM guid_owner WHERE parent_guid_id = ? AND child_guid_id = ?"))
		{
			ps.setInt(1, parentGuidId);
			ps.setInt(2, childGuidId);
			try (ResultSet rs = ps.executeQuery())
			{
				if (!rs.next())
				{
					return false;
				}
			}
		}

		return true;

	}

	public List<String> getGUIDsByRevision(String aUnitName, int aRevCode) throws SQLException
	{
		
		int revisionId = hasRevision(aRevCode);
		int unitId = hasUnit(aUnitName);
		
		if (revisionId == -1)
		{
			return null;
		}
		
		if (unitId == -1)
		{
			return null;
		}
		
		List<String> guids = new ArrayList<>();
		try (PreparedStatement pstmt = myConnection.prepareStatement(
				"SELECT g.guid FROM guid g JOIN guid_revision gr ON g.guid_id = gr.guid_id WHERE gr.rev_id = ?"/* AND g.unit_id = ?"*/))
		{
			pstmt.setInt(1, revisionId);
			//pstmt.setInt(2, unitId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next())
			{
				String guid = rs.getString("guid");
				guids.add(guid);
			}
		}
		
		return guids;
				
	}

	public List<Integer> getRevisionIdsByGUID(String aGUID) throws SQLException
	{
		List<Integer> revisionIds = new ArrayList<>();
		
		int guidId = getGuidId(aGUID);
		
		if (guidId == -1)
		{
			return null;
		}
		
		// Revisionen holen
		
		try (PreparedStatement pstmt = myConnection
				.prepareStatement("SELECT gr.rev_id FROM guid_revision gr WHERE gr.guid_id = ?"))
		{
			pstmt.setInt(1, guidId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next())
			{
				int revisionId = rs.getInt("rev_id");
				revisionIds.add(revisionId);
			}
		}
		
		return revisionIds;
	}
	
	public int getRevision(int aRevisionId) throws SQLException
	{
		try (PreparedStatement ps = myConnection.prepareStatement("SELECT rev_code FROM revision WHERE rev_id = ?"))
		{
			ps.setInt(1, aRevisionId);
			ResultSet rs = ps.executeQuery();
		
			if (!rs.next())
			{
				return -1;
			}
			return rs.getInt(1);	
		}	
	}
	
	
	

	public boolean addGUIDsToRevision(List<String> aGUIDs, int aRevCode) throws SQLException
	{
		
		int revisionId = hasRevision(aRevCode);
		if (revisionId == -1)
		{
			return false;
		}
		
		for(String guid : aGUIDs)
		{
			int guidId = getGuidId(guid);

			if (guidId == -1)
			{
				return false;
			}

			if(addRevisionIdToGuidId(guidId, revisionId)==false)
            {
                return false;
            }
			
			
		}

		return true;
	}
	
	
	/*
	
	public boolean addGUIDs(List<String> aGUIDs, String aUnitName, int aRevCode) throws SQLException
	{

		int unitId = hasUnit(aUnitName);
		int revisionId = hasRevision(aRevCode);

		if (revisionId == -1)
		{
			return false;
		}

		if (unitId == -1)
		{
			return false;
		}

		for (String guid : aGUIDs)
		{
			int guidId = addGUID(guid, aUnitName);

			if (addRevisionIdToGuidId(guidId, revisionId) == false)
			{
				return false;
			}

		}

		return true;

	}
	*/
	
	public RevisionData getRevisionData(int aRevisionId) throws SQLException
	{
		try (PreparedStatement ps = myConnection
				.prepareStatement("SELECT rev_code, author, date, message, jira_issue FROM revision WHERE rev_id = ?"))
		{
			ps.setInt(1, aRevisionId);
			ResultSet rs = ps.executeQuery();

			if (!rs.next())
			{
				return null;
			}

			int revCode = rs.getInt("rev_code");
			String author = rs.getString("author");
			String date = rs.getString("date");
			String message = rs.getString("message");
			String jiraIssue = rs.getString("jira_issue");

			return new RevisionData(revCode, author, date, message, jiraIssue);
		}
	}
	
	public class RevisionData
	{
		
		private int myRevCode;
		private String myRevisionMessage;
		private String myJiraIssue;
		private String myAuthor;
		private String myDate;

		public RevisionData(int aRevCode, String aAuthor, String aDate, String aRevisionMessage, String aJiraIssue)
		{		
			myRevCode = aRevCode;
			myRevisionMessage = aRevisionMessage;
			myJiraIssue = aJiraIssue;
			myAuthor = aAuthor;
			myDate = aDate;
		}
	
		public int getRevCode()
		{
			return myRevCode;
		}

		public String getAuthor()
		{
			return myAuthor;
		}
		
		public String getDate()
		{
			return myDate;
		}
		
		public String getRevisionMessage()
		{
			return myRevisionMessage;
		}

		public String getJiraIssue()
		{
			return myJiraIssue;
		}
	}


}


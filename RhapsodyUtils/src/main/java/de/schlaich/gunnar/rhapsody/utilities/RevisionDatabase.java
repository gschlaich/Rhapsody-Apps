package de.schlaich.gunnar.rhapsody.utilities;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Vollständig optimierte und funktionskomplette Variante der ursprünglichen {@code RevisionDatabase}.
 * <p>
 *  Alle ehemals öffentlichen Methoden sind vorhanden, nutzen jedoch:</p>
 * <ul>
 *   <li>einmalig erzeugte {@link PreparedStatement}s&nbsp;→ vermeidet ständiges Parse/Plan‑Overhead</li>
 *   <li>{@code INSERT OR IGNORE} / {@code ON CONFLICT DO NOTHING}&nbsp;→ vermeidet Doppel‑SELECTs</li>
 *   <li>Batchfähige Inserts und "SELECT&nbsp;1 … LIMIT&nbsp;1"‑Existenzprüfungen</li>
 *   <li>manuelles Commit (für Bulk‑Operationen typische Faktor‑5–20‑Beschleunigung)</li>
 * </ul>
 */
public class RevisionDatabase implements AutoCloseable {

    /* ---------------------------------------------------------------------
     *  SQL – DDL
     * ------------------------------------------------------------------ */
    private static final String[] DDL = {
            // 1) unit
            "CREATE TABLE IF NOT EXISTS unit (" +
                    "unit_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL UNIQUE);",

            // 2) revision
            "CREATE TABLE IF NOT EXISTS revision (" +
                    "rev_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "rev_code INTEGER NOT NULL UNIQUE," +
                    "author TEXT NOT NULL," +
                    "date TEXT NOT NULL," +
                    "message TEXT," +
                    "jiraIssue TEXT);",

            // 3) unit_revision
            "CREATE TABLE IF NOT EXISTS unit_revision (" +
                    "unit_id INTEGER NOT NULL REFERENCES unit(unit_id) ON DELETE CASCADE," +
                    "rev_id INTEGER NOT NULL REFERENCES revision(rev_id) ON DELETE CASCADE," +
                    "file_hash TEXT," +
                    "PRIMARY KEY (unit_id, rev_id));",

            // 4) guid
            "CREATE TABLE IF NOT EXISTS guid (" +
                    "guid     TEXT UNIQUE," +
                    "unit_id  INTEGER NOT NULL REFERENCES unit(unit_id) ON DELETE CASCADE," +
                    "guid_id  INTEGER PRIMARY KEY AUTOINCREMENT);",

            // 5) guid_revision
            "CREATE TABLE IF NOT EXISTS guid_revision (" +
                    "guid_id INTEGER NOT NULL REFERENCES guid(guid_id) ON DELETE CASCADE," +
                    "rev_id  INTEGER NOT NULL REFERENCES revision(rev_id) ON DELETE CASCADE," +
                    "PRIMARY KEY (guid_id, rev_id));",

            // 6) guid_owner
            "CREATE TABLE IF NOT EXISTS guid_owner (" +
                    "parent_guid_id INTEGER NOT NULL REFERENCES guid(guid_id) ON DELETE CASCADE," +
                    "child_guid_id  INTEGER NOT NULL REFERENCES guid(guid_id) ON DELETE CASCADE," +
                    "PRIMARY KEY (parent_guid_id, child_guid_id));",

            // Indizes
            "CREATE INDEX IF NOT EXISTS idx_gr_guid      ON guid_revision(guid_id);",
            "CREATE INDEX IF NOT EXISTS idx_ur_unit      ON unit_revision(unit_id);",
            "CREATE INDEX IF NOT EXISTS idx_ur_rev       ON unit_revision(rev_id);",
            "CREATE INDEX IF NOT EXISTS idx_go_child     ON guid_owner(child_guid_id);",
            "CREATE INDEX IF NOT EXISTS idx_guid_guid    ON guid(guid);",
            "CREATE INDEX IF NOT EXISTS idx_rev_code     ON revision(rev_code);"
    };

    /* ---------------------------------------------------------------------
     *  SQL – Prepared‑Statement‑Vorlagen
     * ------------------------------------------------------------------ */
    // Revision
    private static final String INS_REVISION               =
            "INSERT OR IGNORE INTO revision (rev_code, author, date, message, jiraIssue) VALUES (?,?,?,?,?)";
    private static final String SEL_REVISION_ID_BY_CODE    =
            "SELECT rev_id FROM revision WHERE rev_code = ? LIMIT 1";
    private static final String SEL_REV_CODE_BY_ID         =
            "SELECT rev_code FROM revision WHERE rev_id = ? LIMIT 1";
    private static final String SEL_REV_DATA_BY_ID         =
            "SELECT rev_code, author, date, message, jiraIssue FROM revision WHERE rev_id = ? LIMIT 1";

    // Unit
    private static final String INS_UNIT                   =
            "INSERT OR IGNORE INTO unit (name) VALUES (?)";
    private static final String SEL_UNIT_ID_BY_NAME        =
            "SELECT unit_id FROM unit WHERE name = ? LIMIT 1";

    // GUID
    private static final String INS_GUID                   =
            "INSERT OR IGNORE INTO guid (guid, unit_id) VALUES (?,?)";
    private static final String SEL_GUID_ID_BY_GUID        =
            "SELECT guid_id FROM guid WHERE guid = ? LIMIT 1";
    private static final String SEL_GUIDS_BY_UNIT_ID       =
            "SELECT guid FROM guid WHERE unit_id = ?";

    // Verknüpfungen
    private static final String INS_UNIT_REVISION          =
            "INSERT OR IGNORE INTO unit_revision (unit_id, rev_id) VALUES (?,?)";
    private static final String EXISTS_UNIT_REVISION       =
            "SELECT 1 FROM unit_revision WHERE unit_id = ? AND rev_id = ? LIMIT 1";

    private static final String INS_GUID_REVISION          =
            "INSERT OR IGNORE INTO guid_revision (guid_id, rev_id) VALUES (?,?)";
    private static final String EXISTS_GUID_REVISION       =
            "SELECT 1 FROM guid_revision WHERE guid_id = ? AND rev_id = ? LIMIT 1";
    private static final String SEL_REVISION_IDS_BY_GUID_ID=
            "SELECT rev_id FROM guid_revision WHERE guid_id = ?";

    private static final String SEL_GUIDS_BY_REV_ID        =
            "SELECT g.guid FROM guid g JOIN guid_revision gr ON g.guid_id = gr.guid_id WHERE gr.rev_id = ?";
    private static final String SEL_GUIDS_BY_REV_AND_UNIT  =
            "SELECT g.guid FROM guid g JOIN guid_revision gr ON g.guid_id = gr.guid_id WHERE gr.rev_id = ? AND g.unit_id = ?";

    private static final String INS_GUID_OWNER             =
            "INSERT OR IGNORE INTO guid_owner (parent_guid_id, child_guid_id) VALUES (?,?)";
    private static final String EXISTS_GUID_OWNER          =
            "SELECT 1 FROM guid_owner WHERE parent_guid_id = ? AND child_guid_id = ? LIMIT 1";

    /* ---------------------------------------------------------------------
     *  Instanz‑Felder
     * ------------------------------------------------------------------ */
    private Connection connection;   // wird ggf. von reopenConnection() erneuert
    private final String jdbcUrl;

    private final Consumer<String> traceAction;

    /* PreparedStatements */
    private PreparedStatement psInsRevision;
    private PreparedStatement psSelRevisionIdByCode;
    private PreparedStatement psSelRevCodeById;
    private PreparedStatement psSelRevDataById;

    private PreparedStatement psInsUnit;
    private PreparedStatement psSelUnitIdByName;

    private PreparedStatement psInsGuid;
    private PreparedStatement psSelGuidIdByGuid;

    private PreparedStatement psInsUnitRevision;
    private PreparedStatement psExistsUnitRevision;

    private PreparedStatement psInsGuidRevision;
    private PreparedStatement psExistsGuidRevision;
    private PreparedStatement psRevisionIdsByGuidId;

    private PreparedStatement psGuidsByUnitId;
    private PreparedStatement psGuidsByRevId;
    private PreparedStatement psGuidsByRevAndUnit;

    private PreparedStatement psInsGuidOwner;
    private PreparedStatement psExistsGuidOwner;
    
    private final static String  dbFileName = "cache.db";

    /* ---------------------------------------------------------------------
     *  Konstruktor
     * ------------------------------------------------------------------ */
    public RevisionDatabase(File dbDir, Consumer<String> traceAction) throws SQLException {
        
    	this.traceAction = traceAction;
    	
        if (!dbDir.exists() && !dbDir.mkdirs()) {
            throw new IllegalStateException("Verzeichnis " + dbDir + " konnte nicht angelegt werden.");
        }
        //this.jdbcUrl = "jdbc:sqlite:" + new File(dbDir, "cache.db").getAbsolutePath();
        
        
        this.jdbcUrl = "jdbc:sqlite:" + dbDir.getAbsolutePath() + File.separator + dbFileName;
        trace("DBURL: " + this.jdbcUrl);
        
        createDatabase();


        //trace("Database initialisiert (optimiert)");
    }
    
	public static String GetDBFileName() 
	{
    	return dbFileName;
    }

    private void createDatabase()
    {
    	try(Connection con = DriverManager.getConnection(jdbcUrl))
    	{
    		
    		        this.connection = con;
    		
    	
        //this.connection.setAutoCommit(false);
        try (Statement st = connection.createStatement()) {
            st.execute("PRAGMA journal_mode=WAL;");
            st.execute("PRAGMA synchronous   = NORMAL;");
            st.execute("PRAGMA cache_size    = -20000;"); // ≈ 80 MB
            st.execute("PRAGMA foreign_keys  = ON;");
            st.execute("PRAGMA temp_store    = MEMORY;");
        }
        try (Statement st = connection.createStatement()) {
            for (String ddl : DDL) st.execute(ddl);
        }
        
        prepareStatements();

        
    	
    }
    	catch (SQLException e)
		{
			e.printStackTrace();
		}
    }
    
    private void trace(String aMessage)
	{
		if (this.traceAction == null)
		{
			// no traceaction set...
			return;
		}
		aMessage = "DB: " + aMessage;

		this.traceAction.accept(aMessage);
	}

    /* ---------------------------------------------------------------------
     *  Utility
     * ------------------------------------------------------------------ */
    
	private void prepareStatements() throws SQLException	
    {
    	/* PreparedStatements */
        psInsRevision            = connection.prepareStatement(INS_REVISION, Statement.RETURN_GENERATED_KEYS);
        psSelRevisionIdByCode    = connection.prepareStatement(SEL_REVISION_ID_BY_CODE);
        psSelRevCodeById         = connection.prepareStatement(SEL_REV_CODE_BY_ID);
        psSelRevDataById         = connection.prepareStatement(SEL_REV_DATA_BY_ID);

        psInsUnit               = connection.prepareStatement(INS_UNIT, Statement.RETURN_GENERATED_KEYS);
        psSelUnitIdByName       = connection.prepareStatement(SEL_UNIT_ID_BY_NAME);

        psInsGuid               = connection.prepareStatement(INS_GUID, Statement.RETURN_GENERATED_KEYS);
        psSelGuidIdByGuid       = connection.prepareStatement(SEL_GUID_ID_BY_GUID);

        psInsUnitRevision       = connection.prepareStatement(INS_UNIT_REVISION);
        psExistsUnitRevision    = connection.prepareStatement(EXISTS_UNIT_REVISION);

        psInsGuidRevision       = connection.prepareStatement(INS_GUID_REVISION);
        psExistsGuidRevision    = connection.prepareStatement(EXISTS_GUID_REVISION);
        psRevisionIdsByGuidId   = connection.prepareStatement(SEL_REVISION_IDS_BY_GUID_ID);

        psGuidsByUnitId         = connection.prepareStatement(SEL_GUIDS_BY_UNIT_ID);
        psGuidsByRevId          = connection.prepareStatement(SEL_GUIDS_BY_REV_ID);
        psGuidsByRevAndUnit     = connection.prepareStatement(SEL_GUIDS_BY_REV_AND_UNIT);

        psInsGuidOwner          = connection.prepareStatement(INS_GUID_OWNER);
        psExistsGuidOwner       = connection.prepareStatement(EXISTS_GUID_OWNER);
    }
    

    public void commit() throws SQLException { connection.commit(); }
    public void rollback() throws SQLException { connection.rollback(); }

    /* ---------------------------------------------------------------------
     *  Revision
     * ------------------------------------------------------------------ */
    public int addRevision(int revCode, String author, String date, String message, String jira) throws SQLException {
        psInsRevision.setInt   (1, revCode);
        psInsRevision.setString(2, author);
        psInsRevision.setString(3, date);
        psInsRevision.setString(4, message);
        psInsRevision.setString(5, jira);
        int rows = psInsRevision.executeUpdate();
        if (rows > 0) {
            try (ResultSet keys = psInsRevision.getGeneratedKeys()) { keys.next(); return keys.getInt(1); }
        }
        return hasRevision(revCode); // bereits vorhanden
    }

    public int hasRevision(int revCode) throws SQLException {
        psSelRevisionIdByCode.setInt(1, revCode);
        try (ResultSet rs = psSelRevisionIdByCode.executeQuery()) {
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    public int getRevision(int revisionId) throws SQLException {
        psSelRevCodeById.setInt(1, revisionId);
        try (ResultSet rs = psSelRevCodeById.executeQuery()) {
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    /* ---------------------------------------------------------------------
     *  Unit
     * ------------------------------------------------------------------ */
    public int addUnit(String name) throws SQLException {
        psInsUnit.setString(1, name);
        int rows = psInsUnit.executeUpdate();
        if (rows > 0) {
            try (ResultSet keys = psInsUnit.getGeneratedKeys()) { keys.next(); return keys.getInt(1); }
        }
        psSelUnitIdByName.setString(1, name);
        try (ResultSet rs = psSelUnitIdByName.executeQuery()) { rs.next(); return rs.getInt(1); }
    }

    private int hasUnit(String name) throws SQLException {
        psSelUnitIdByName.setString(1, name);
        try (ResultSet rs = psSelUnitIdByName.executeQuery()) {
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    /* ---------------------------------------------------------------------
     *  GUID
     * ------------------------------------------------------------------ */
    private int insertGuid(String guid, int unitId) throws SQLException {
        psInsGuid.setString(1, guid);
        psInsGuid.setInt   (2, unitId);
        int rows = psInsGuid.executeUpdate();
        if (rows > 0) {
            try (ResultSet keys = psInsGuid.getGeneratedKeys()) { keys.next(); return keys.getInt(1); }
        }
        return getGuidId(guid);
    }

    public boolean addGUID(String guid, String unitName) throws SQLException {
        int unitId = addUnit(unitName);
        psSelGuidIdByGuid.setString(1, guid);
        try (ResultSet rs = psSelGuidIdByGuid.executeQuery()) {
            if (rs.next()) return false;   // existiert
        }
        insertGuid(guid, unitId);
        return true;
    }

    public int getGuidId(String guid) throws SQLException {
        psSelGuidIdByGuid.setString(1, guid);
        try (ResultSet rs = psSelGuidIdByGuid.executeQuery()) {
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    /* ---------------------------------------------------------------------
     *  Verknüpfungen unit_revision
     * ------------------------------------------------------------------ */
    public boolean hasUnitRevision(int unitId, int revisionId) throws SQLException {
        psExistsUnitRevision.setInt(1, unitId);
        psExistsUnitRevision.setInt(2, revisionId);
        try (ResultSet rs = psExistsUnitRevision.executeQuery()) {
            return rs.next();
        }
    }

    public boolean addUnitRevision(String unitName, int revCode) throws SQLException {
        int revisionId = hasRevision(revCode);
        if (revisionId == -1) return false;
        int unitId = addUnit(unitName);
        psInsUnitRevision.setInt(1, unitId);
        psInsUnitRevision.setInt(2, revisionId);
        psInsUnitRevision.executeUpdate();
        return true;
    }

    public boolean hasUnitRevision(String unitName, int revCode) throws SQLException {
        int unitId = hasUnit(unitName);
        int revId  = hasRevision(revCode);
        if (unitId == -1 || revId == -1) return false;
        return hasUnitRevision(unitId, revId);
    }

    /* ---------------------------------------------------------------------
     *  Verknüpfungen guid_revision
     * ------------------------------------------------------------------ */
    public boolean addRevisionToGUID(String guid, int revCode) throws SQLException {
        int guidId = getGuidId(guid);
        if (guidId == -1) return false;
        int revId = hasRevision(revCode);
        if (revId == -1) return false;
        psInsGuidRevision.setInt(1, guidId);
        psInsGuidRevision.setInt(2, revId);
        psInsGuidRevision.executeUpdate();
        return true;
    }

    public boolean addGUIDsToRevision(List<String> guids, int revCode) throws SQLException {
        int revId = hasRevision(revCode);
        if (revId == -1) return false;
        for (String g : guids) {
            int gId = getGuidId(g);
            if (gId == -1) return false;
            psInsGuidRevision.setInt(1, gId);
            psInsGuidRevision.setInt(2, revId);
            psInsGuidRevision.addBatch();
        }
        psInsGuidRevision.executeBatch();
        return true;
    }

    /* ---------------------------------------------------------------------
     *  guid_owner
     * ------------------------------------------------------------------ */
    public boolean addChildtoGuid(String parentGuid, String childGuid) throws SQLException {
        int parentId = getGuidId(parentGuid);
        int childId  = getGuidId(childGuid);
        if (parentId == -1 || childId == -1) return false;
        psInsGuidOwner.setInt(1, parentId);
        psInsGuidOwner.setInt(2, childId);
        psInsGuidOwner.executeUpdate();
        return true;
    }

    public boolean isParentOf(String parentGuid, String childGuid) throws SQLException {
        int parentId = getGuidId(parentGuid);
        int childId  = getGuidId(childGuid);
        if (parentId == -1 || childId == -1) return false;
        psExistsGuidOwner.setInt(1, parentId);
        psExistsGuidOwner.setInt(2, childId);
        try (ResultSet rs = psExistsGuidOwner.executeQuery()) { return rs.next(); }
    }

    /* ---------------------------------------------------------------------
     *  Abfragen
     * ------------------------------------------------------------------ */
    public List<String> getGUIDsByRevision(int revCode) throws SQLException {
        int revId = hasRevision(revCode);
        if (revId == -1) return null;
        List<String> list = new ArrayList<>();
        psGuidsByRevId.setInt(1, revId);
        try (ResultSet rs = psGuidsByRevId.executeQuery()) {
            while (rs.next()) list.add(rs.getString(1));
        }
        return list;
    }

    public List<String> getGUIDsByUnit(String unitName) throws SQLException {
        int unitId = hasUnit(unitName);
        if (unitId == -1) return null;
        List<String> list = new ArrayList<>();
        psGuidsByUnitId.setInt(1, unitId);
        try (ResultSet rs = psGuidsByUnitId.executeQuery()) {
            while (rs.next()) list.add(rs.getString(1));
        }
        return list;
    }

    public List<String> getGUIDsByRevision(String unitName, int revCode) throws SQLException {
        int unitId = hasUnit(unitName);
        int revId  = hasRevision(revCode);
        if (unitId == -1 || revId == -1) return null;
        List<String> list = new ArrayList<>();
        psGuidsByRevAndUnit.setInt(1, revId);
        psGuidsByRevAndUnit.setInt(2, unitId);
        try (ResultSet rs = psGuidsByRevAndUnit.executeQuery()) {
            while (rs.next()) list.add(rs.getString(1));
        }
        return list;
    }

    public List<Integer> getRevisionIdsByGUID(String guid) throws SQLException {
        int guidId = getGuidId(guid);
        if (guidId == -1) return null;
        List<Integer> ids = new ArrayList<>();
        psRevisionIdsByGuidId.setInt(1, guidId);
        try (ResultSet rs = psRevisionIdsByGuidId.executeQuery()) {
            while (rs.next()) ids.add(rs.getInt(1));
        }
        return ids;
    }

    public RevisionData getRevisionData(int revisionId) throws SQLException {
        psSelRevDataById.setInt(1, revisionId);
        try (ResultSet rs = psSelRevDataById.executeQuery()) {
            if (!rs.next()) return null;
            return new RevisionData(
                    rs.getInt("rev_code"),
                    rs.getString("author"),
                    rs.getString("date"),
                    rs.getString("message"),
                    rs.getString("jiraIssue")
            );
        }
    }

    /* ---------------------------------------------------------------------
     *  Verbindungsverwaltung
     * ------------------------------------------------------------------ */
    public boolean reopenConnection()
    {
        try
		{
			if (!connection.isClosed()) 
			{
				return true;
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
    
        try
        {
        connection = DriverManager.getConnection(jdbcUrl);
        
        prepareStatements();
        
        }
        catch (SQLException e)
        {
        	//e.printStackTrace();
        	trace("ReopenConnection failed: " + e.getMessage());
        	return false;
        }
        return true;
    }

    @Override public void close() throws SQLException { connection.close(); }

    /* ---------------------------------------------------------------------
     *  RevisionData DTO
     * ------------------------------------------------------------------ */
    public static class RevisionData {
        private final int revCode;
        private final String author;
        private final String date;
        private final String message;
        private final String jira;
        public RevisionData(int revCode, String author, String date, String message, String jira) {
            this.revCode = revCode; this.author = author; this.date = date; this.message = message; this.jira = jira;
        }
        public int getRevCode() { return revCode; }
        public String getAuthor() { return author; }
        public String getDate() { return date; }
        public String getRevisionMessage() { return message; }
        public String getJira()
        {
        	return jira;
        }
    }
}
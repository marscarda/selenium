package selenium.txttables.back;
//*************************************************************************
import hydrogen.andromeda.AndromedaTransactorFront;
import hydrogen.andromeda.ColSeparator;
import hydrogen.andromeda.NewTxtTable;
import hydrogen.root.DBConnector;
import hydrogen.root.HydroException;
import java.sql.Connection;
import selenium.BackMessagesHolder;
//*************************************************************************
public class TxtTableUploader extends Thread
{
    public static final String COLUMNPREFIX = "COL";
    //*********************************************************************
    private String dbhost = null;
    private String dbuser = null;
    private String dbpass = null;
    private String dbandromeda = null;
    //---------------------------------------------------------------------
    int rowcount = 0;
    int maxcellcount = 0;
    //---------------------------------------------------------------------
    private String tabname = null;
    private ColSeparator separator = ColSeparator.VERTICALBAR;
    private byte[] inputdata = null;
    private long userid = 0;
    private boolean fstheader = false;
    //---------------------------------------------------------------------
    private String[] rawrows = null;
    private ProcessingRow[] rows = null;
    private ProcessingRow headerrow = null;
    private NewTxtTable table = null;
    //=====================================================================
    private Connection dbconnection = null;
    private AndromedaTransactorFront transac = null;
    //*********************************************************************
    public void setDbConnectionData (String host, String user, String pass) {
        dbhost = host;
        dbuser = user;
        dbpass = pass;
    }
    //=====================================================================
    public void setDatabase (String database) { dbandromeda = database; }
    //*********************************************************************
    public void setTableName (String name) { tabname = name; }
    public void setData (byte[] data) { inputdata = data; }
    public void setSeparator (ColSeparator separator) { this.separator = separator; }
    public void setUseHeader (boolean header) { this.fstheader = header; }
    public void setUserId (long id) { userid = id; }
    //*********************************************************************
    @Override
    public void run () {
        //-------------------------------------------
        BackMessagesHolder.addMessage(userid, "Table [" + tabname + "] started to get processed", false);
        //-------------------------------------------
        boolean goingok = true;
        //-------------------------------------------
        if (goingok) 
            if (!initialize()) goingok = false;
        //-------------------------------------------
        if (goingok)
            if (!splitToRows()) goingok = false;
        //-------------------------------------------
        if (goingok)
            if (!loopRowsAndSplitCells()) goingok = false;
        //===========================================
        if (goingok)
            if (!completeRows()) goingok = false;
        //-------------------------------------------
        if (goingok)
            if (!setHeader()) goingok = false;
        //-------------------------------------------
        if (goingok)
            if (!startTable ()) goingok = false;
        //-------------------------------------------
        if (goingok)
            if (!loopAndAddRows()) goingok = false;
        //-------------------------------------------
        if (goingok)
            if (!finishJob()) goingok = false;
        //-------------------------------------------
        if (!goingok) {
            try { transac.rollBackTransaction(); }
            catch (Exception e) {}
        }
        //-------------------------------------------
        cleanUp();
        //-------------------------------------------
        if (goingok) 
            BackMessagesHolder.addMessage(userid, "Table [" + tabname + "] added", false);
        //-------------------------------------------
    }
    //=====================================================================
    private boolean initialize () {
        //=========================================================
        try { dbconnection = DBConnector.createDBConnection(dbhost, dbuser, dbpass); }
        catch (Exception e) {
            BackMessagesHolder.addMessage(userid, "Processing Table [" + tabname + "] failed. Internal server error", true);
            System.out.println("Upload Txt table failed. Trying to open the db connection");
            System.out.println(e.getMessage());
            return false;
        }
        //=========================================================
        transac = new AndromedaTransactorFront();
        transac.setDBConnection(dbconnection);
        transac.setDatabaseName(dbandromeda);
        //=========================================================
        return true;
        //=========================================================
    }
    //=====================================================================
    private boolean splitToRows () {
        //====================================================
        String uplddata;
        try { uplddata = new String(inputdata, "UTF-8"); }
        catch (Exception e) 
        {
            BackMessagesHolder.addMessage(userid, "Processing Table [" + tabname + "] failed. Invalid File", true);
            System.out.println(e.getMessage()); 
            return false;
        }
        //-----------------------------------------------------
        rawrows = uplddata.split("\\r?\\n");
        inputdata = null;
        rowcount = rawrows.length;
        return rowcount > 0;
        //-----------------------------------------------------
    }
    //=====================================================================
    private boolean loopRowsAndSplitCells () {
        rows = new ProcessingRow[rowcount];
        for (int n = 0; n < rowcount; n++) {
            if (rawrows[n].length() > 5000) {
                BackMessagesHolder.addMessage(userid, "Processing Table [" + tabname + "] failed. Invalid File. Rows are too long", true);
                return false;
            }
            rows[n] = new ProcessingRow();
            rows[n].setRaw(rawrows[n]);
            if (rows[n].cellcount > maxcellcount)
                maxcellcount = rows[n].cellcount;
        }
        return true;
    }
    //=====================================================================
    private boolean completeRows () {
        boolean skip = fstheader;
        //----------------------------------------------
        for (int n = 0; n < rowcount; n++) {
            if (skip) {
                skip = false;
                continue;
            }
            rows[n].completeCells(maxcellcount);;
        }
        //----------------------------------------------
        if (!fstheader) return true;
        //----------------------------------------------
        rows[0].CompleteCellsHeader(maxcellcount);
        return true;
    }
    //=====================================================================
    private boolean setHeader () {
        if (fstheader) {
            headerrow = rows[0];
            return true;
        }
        headerrow = new ProcessingRow();
        headerrow.CompleteCellsHeader(maxcellcount);
        return true;
    }
    //=====================================================================
    private boolean startTable () {
        //=================================================
        StringBuilder header = new StringBuilder();
        for (int n = 0; n < headerrow.cellcount; n++) {
            //---------------------------------------------
            if (n > 0){
                switch (separator) {
                    case COMMA: header.append(","); break;
                    case TAB: header.append("\t"); break;
                    case VERTICALBAR: header.append("|"); break;
                    default: 
                        BackMessagesHolder.addMessage(userid, "Processing Table [" + tabname + "] failed. Invalid column separator", true);
                        return false;
                }
            }
            //---------------------------------------------
            header.append(headerrow.cells[n]);
        }
        //=================================================
        try {
            table = new NewTxtTable();
            table.setTableName(tabname);
            table.setHeader(header.toString());
            table.setUserId(userid);
            table.setSeparator(separator);
            transac.startCreateTxtTable(table);
            return true;
        }
        catch (HydroException e) {
            BackMessagesHolder.addMessage(userid, "Processing Table [" + tabname + "] failed. " + e.getMessage(), true);
            return false;
        }
        catch (Exception e) {
            BackMessagesHolder.addMessage(userid, "Processing Table [" + tabname + "] failed. Internal server error", true);
            System.out.println("Upload Txt table failed.");
            System.out.println(e.getMessage());
            return false;
        }
        //=================================================
    }
    //=====================================================================
    private boolean loopAndAddRows () {
        //================================================
        int rowid = 1;
        boolean skip = fstheader;
        //================================================
        for (int n = 0; n < rowcount; n++) {
            //============================================
            if (skip) {
                skip = false;
                continue;
            }
            //--------------------------------------------
            if (!addRow(rows[n], rowid)) return false;
            rowid++;
            //============================================
        }
        //================================================
        return true;
        //================================================        
    }
    //=====================================================================
    private boolean addRow (ProcessingRow row, int rowid) {
        StringBuilder rowdata = new StringBuilder();
        for (int n = 0; n < row.cellcount; n++) {
            //---------------------------------------------
            if (n > 0){
                switch (separator) {
                    case COMMA: rowdata.append(","); break;
                    case TAB: rowdata.append("\t"); break;
                    case VERTICALBAR: rowdata.append("|"); break;
                }
            }
            //---------------------------------------------
            rowdata.append(row.cells[n]);
        }
        try { 
            transac.addTableRow(table.tableId(), rowid, rowdata.toString()); 
            return true;
        }
        catch (Exception e) {
            BackMessagesHolder.addMessage(userid, "Processing Table [" + tabname + "] failed. Internal server error", true);
            System.out.println("Upload Txt table failed.");
            System.out.println(e.getMessage());
            return false;
        }
    }
    //=====================================================================
    private boolean finishJob () {
        try { 
            transac.commitTransaction(); 
            return true;
        }
        catch (Exception e) {
            BackMessagesHolder.addMessage(userid, "Processing Table [" + tabname + "] failed. Internal server error", true);
            System.out.println("Upload Txt table failed.");
            System.out.println(e.getMessage());
            return false;
        }
    }
    //=====================================================================
    //private boolean 
    //=====================================================================
    private void cleanUp () {
        if (dbconnection == null) return;
        DBConnector.destroyDBConnection(dbconnection);
    }
    //=====================================================================
    public void test (String rawrow) {
        
        ProcessingRow row = new ProcessingRow();
        row.CompleteCellsHeader(6);

        for (String s : row.cells)
            System.out.println("(" + s + ")");
        
        /*
        ProcessingRow row = new ProcessingRow();
        row.setRaw(rawrow);
        row.CompleteCellsHeader(7);
        for (String s : row.cells)
            System.out.println("(" + s + ")");
        row = new ProcessingRow();
        row.generateHeaders(3);
        for (String s : row.cells)
            System.out.println("(" + s + ")");
           
            */
    }
    //*********************************************************************
    private class ProcessingRow {
        String[] cells = null;
        int cellcount = 0;
        //=================================================================
        void setRaw (String rawrow) {
            //--------------------------------------
            switch (separator) {
                case COMMA: cells = rawrow.split(","); break;
                case TAB: cells = rawrow.split("\t"); break;
                case VERTICALBAR: cells = rawrow.split("\\|"); break;
                default: 
                    cells = new String[1];
                    cells[0] = rawrow;
            }
            //--------------------------------------
            removeQuotes(cells);
            //--------------------------------------
            cellcount = cells.length;
            //--------------------------------------
        }
        //=================================================================
        void completeCells (int max) {
            if (cellcount >= max) return;
            if (cells == null) cells = new String[max];
            String[] newcells = new String[max];
            System.arraycopy(cells, 0, newcells, 0, cellcount);
            for (int n = cellcount; n < max; n++) newcells[n] = "";
            cells = newcells;
            cellcount = max;
        }
        //=================================================================
        void CompleteCellsHeader (int max) {
            if (cellcount >= max) return;
            if (cells == null) cells = new String[max];
            String[] newcells = new String[max];
            System.arraycopy(cells, 0, newcells, 0, cellcount);
            for (int n = cellcount; n < max; n++) newcells[n] = "COL" + (n + 1);
            cells = newcells;
            cellcount = max;
        }
        //=================================================================
        void generateHeaders (int max) {
            cells = new String[max];
            for (int n = 0; n < max; n++)
                cells[n] = "COL" + (n + 1);
            cellcount = max;
        }
        //=================================================================
        private void removeQuotes (String[] strin) {
            int indfrom, indto;
            int fcount = strin.length;
            String nstr;
            for (int n = 0; n < fcount; n++) {
                nstr = strin[n];
                indfrom = 0;
                indto = nstr.length();
                if (indto == 0) continue;
                if (nstr.codePointAt(0) == 34) indfrom++;
                if (nstr.codePointAt(indto - 1) == 34) indto--;
                if (indto < indfrom) strin[n] = "";
                else strin[n] = nstr.substring(indfrom, indto);
            }
        }
        //=================================================================
    }
    //*********************************************************************
}
//*************************************************************************

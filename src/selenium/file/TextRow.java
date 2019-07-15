package selenium.file;
//************************************************************************
@Deprecated
public class TextRow {
    //=============================================================
    private String rowtext = null;
    private int cellscount = 0;
    private String[] cells = null;
    private String separator = ",";
    //=============================================================
    public void setText (String rowtext) { 
        this.rowtext = rowtext; 
        cells = null;
        cellscount = 0;
    }
    //-------------------------------------------------------------
    public void setSeparator (String separator) {
        if (separator == null) return;
        this.separator = separator;
    }
    //=============================================================
    public void split () {
        if (rowtext == null) return;
        cells = rowtext.split(separator);
        cellscount = cells.length;
    }
    //=============================================================
    public int cellsCount () { return cellscount; }
    //-------------------------------------------------------------
    public String[] getCells () {
        if (cells == null) return new String[0];
        return cells;
    }
    //=============================================================
    public void updateCellsCount (int count) {
        if (count <= cellscount) return;
        String[] newcells = new String[count];
        if (cellscount != 0)
            System.arraycopy(cells, 0, newcells, 0, cellscount);
        for (int n = cellscount; n < count; n++)
            newcells[n] = "";
        cells = newcells;
        cellscount = count;
    }
    //=============================================================
}
//************************************************************************


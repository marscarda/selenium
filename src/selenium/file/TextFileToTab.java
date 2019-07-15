package selenium.file;
//****************************************************************************
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
//****************************************************************************
@Deprecated
public class TextFileToTab {
    //===============================================================
    private boolean analyzis = false;
    public boolean analyzisOk () { return analyzis; }
    //===============================================================
    String separator = null;
    public void setSeparator (String separator) {
        if (separator == null) return;
        this.separator = separator;
    }
    //===============================================================
    byte[] bytes = null;
    int bytescount = 0;
    public void setBytes (byte[] bytes) {
        this.bytes = bytes; 
        if (bytes != null) bytescount = bytes.length;
        else bytescount = 0;
    }
    //===============================================================
    private TextRow[] toprows = null;
    public TextRow[] topRows () {
        if (toprows == null) return new TextRow[0];
        return toprows;
    }
    //===============================================================
    public void analyzeTop () {
        //----------------------------------------------
        analyzis = false;
        if (bytes == null) return;
        int count = bytescount;
        if (count > 20000) count = 20000;
        byte[] topbytes = new byte[count];
        System.arraycopy(bytes, 0, topbytes, 0, count);
        String toppart;
        try { toppart = new String(bytes, StandardCharsets.UTF_8); }
        catch (Exception e) { return; }
        toprows = convertToRows(toppart);
        int rcount = toprows.length;
        //----------------------------------------------
        if (rcount < 2) {
            analyzis = false;
            return;
        }
        //----------------------------------------------
        //Let's remove the last row.
        int toprcount = rcount - 1;
        TextRow[] newrows = new TextRow[toprcount];
        System.arraycopy(toprows, 0, newrows, 0, toprcount);
        toprows = newrows;
        //----------------------------------------------
        int cells = 0;
        for (TextRow row : toprows)
            if (cells < row.cellsCount())
                cells = row.cellsCount();
        //----------------------------------------------
        for (TextRow row : toprows)
            row.updateCellsCount(cells);
        //----------------------------------------------
        if (toprcount > 12) {
            toprcount = 12;
            newrows = new TextRow[toprcount];
            System.arraycopy(toprows, 0, newrows, 0, toprcount);
            toprows = newrows;
        }
        //----------------------------------------------
        analyzis = true;
        //----------------------------------------------
    }
    //===============================================================
    private TextRow[] convertToRows (String text) {
        String[] strows = text.split("\\r?\\n");
        List <TextRow> rows = new ArrayList<>();
        for (String strow : strows) {
            TextRow row = new TextRow();
            row.setText(strow);
            row.setSeparator(separator);
            row.split();
            rows.add(row);
        }
        return rows.toArray(new TextRow[0]);
    }
    //===============================================================
    
    //===============================================================
}
//****************************************************************************

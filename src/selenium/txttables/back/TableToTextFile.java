package selenium.txttables.back;
//****************************************************************************
import hydrogen.andromeda.AndromedaTransactorFront;
import hydrogen.andromeda.TableRow;
import hydrogen.andromeda.TxtTable;
import hydrogen.root.ErrorCode;
import hydrogen.root.HydroException;
//****************************************************************************
public class TableToTextFile 
{
    //=======================================================
    long userid = 0;
    long tableid = 0;
    AndromedaTransactorFront transac = null;
    String tablename = null;
    byte[] data = null;
    //=======================================================
    public void setUserId (long userid) { this.userid = userid; }
    public void setTableId (long tableid) { this.tableid = tableid; }
    public void setTransactor (AndromedaTransactorFront transac) { this.transac = transac; }
    //=======================================================
    public String getTableName () {
        if (tablename == null) return "";
        return tablename;
    }
    public byte[] getData () {
        if (data == null) return new byte[0];
        return data;
    }
    //=======================================================
    public void execute () throws HydroException, Exception
    {
        TxtTable table = transac.getTableById(tableid);
        if (userid != 0) {
            if (userid != table.userID())
                throw new HydroException("Not Authorized", ErrorCode.NOTAUTHORIZED);
        }
        TableRow[] rows = transac.getRows(tableid, false, 0);
        StringBuilder strtab = new StringBuilder(table.tableHeader());
        strtab.append("\r\n");
        for (TableRow row : rows) {
            strtab.append(row.rowData());
            strtab.append("\r\n");
        }
        data = strtab.toString().getBytes();
        tablename = table.tableName();
    }
    //=======================================================
}
//****************************************************************************

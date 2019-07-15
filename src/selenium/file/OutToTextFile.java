package selenium.file;
//****************************************************************************
import argon.inoutdata.address.AddressFieldType;
import hydrogen.processes.CloudProcess;
import hydrogen.processes.ProcessTransactor;
//****************************************************************************
@Deprecated
public class OutToTextFile 
{
    //=======================================================
    private long processid = 0;
    private long userid = 0;
    String processname = null;
    public void setProcessId(long processid) { this.processid = processid; }
    public void setOwnerUserId (long userid) { this.userid = userid; }
    //=======================================================
    String[] fieldnames = null;
    AddressFieldType[] fieldtypes = null;
    int fieldscount = 0;
    //=======================================================
    private ProcessTransactor transactor = null;
    public void setProcessTransactor (ProcessTransactor transactor) { this.transactor = transactor; }
    //*******************************************************
    public byte[] getFileData () throws Exception
    {
        //===================================================
        if (processid == 0)
            throw new Exception("Process Id must be set before calling getFileData");
        if (transactor == null)
            throw new Exception("Process Transactor must be set before calling getFileData");
        //===================================================
        this.initializeProcess();
        //===================================================
        TextRow[] rows = this.getRows();
        StringBuilder content = new StringBuilder();
        //===================================================
        //The columns names.
        boolean isFirst = true;
        for (int n = 0; n < fieldscount; n++) {
            if (!isFirst) content.append("|");
            isFirst = false;
            switch (fieldtypes[n]) {
                //case ID: content.append("ID"); break;
                case STREET: content.append("Street"); break;
                case NUMBER: content.append("Number"); break;
                case BOUNDARY:
                    content.append(fieldnames[n]);
                    break;
                case LATITUDE: content.append("Latitude"); break;
                case LONGITUDE: content.append("Longitude"); break;
                case RESOLVELEVEL: content.append("Level");
            }
        }
        content.append("\r\n");
        //===================================================
        String[] cells;
        for (TextRow row : rows) {
            cells = row.getCells();
            isFirst = true;
            for (int n = 0; n < fieldscount; n++) { 
                if (!isFirst) content.append("|");
                isFirst = false;
                content.append(cells[n]);
            }
            content.append("\r\n");
        }
        //===================================================
        return content.toString().getBytes();
        //===================================================
    }
    //=======================================================
    public long ownerUserId () { return userid; }
    //=======================================================
    public String getProcessName () {
        if (processname == null) return "";
        return processname;
    }
    //*******************************************************
    private TextRow[] getRows () throws Exception
    {
        //---------------------------------------------------
        if (processid == 0)
            throw new Exception("Process Id must be set before calling getRows");
        if (transactor == null)
            throw new Exception("Process Transactor must be set before calling getRows");
        //---------------------------------------------------
        String[] rawrows = transactor.getRawOutputRows(processid);
        int count = rawrows.length;
        TextRow[] rows = new TextRow[count];
        TextRow row;
        for (int n = 0; n < count; n++) {
            row = new TextRow();
            row.setSeparator("\\|");
            row.setText(rawrows[n]);
            row.split();
            rows[n] = row;
        }
        //---------------------------------------------------
        return rows;
        //---------------------------------------------------        
    }
    //=======================================================
    private void initializeProcess () throws Exception
    {
        //------------------------------------------
        CloudProcess process = transactor.getProcessById(processid, false);
        fieldnames = process.getOutputFields().split("\\|");
        fieldscount = fieldnames.length;
        fieldtypes = new AddressFieldType[fieldscount];
        for (int n = 0; n < fieldscount; n++) 
            fieldtypes[n] = AddressFieldType.fromString(fieldnames[n]);
        //------------------------------------------
        userid = process.userID();
        processname = process.getName();
        //------------------------------------------
    }
    //*******************************************************
}
//****************************************************************************


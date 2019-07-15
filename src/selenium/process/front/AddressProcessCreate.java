package selenium.process.front;
//**************************************************************************
import hydrogen.andromeda.AndrProcess;
import hydrogen.andromeda.AndromedaTransactorFront;
import hydrogen.andromeda.ProcessCode;
import hydrogen.andromeda.ProcessStatus;
import hydrogen.andromeda.TxtTable;
import hydrogen.dictionary.Dictionary;
import hydrogen.dictionary.DictionaryTransactorFront;
import hydrogen.root.ErrorCode;
import hydrogen.root.HydroException;
//**************************************************************************
public class AddressProcessCreate 
{
    //==============================================================
    private AndromedaTransactorFront andrtransac = null;
    private DictionaryTransactorFront dicttransac = null;
    public void setAndromedaTransactor (AndromedaTransactorFront trans) { andrtransac = trans; }
    public void setDIctionaryTransactor (DictionaryTransactorFront trans) { dicttransac = trans; }
    //==============================================================
    private long userid = 0;
    public void setUserID (long userid) { this.userid = userid; }
    //==============================================================
    private long tableid = 0;
    public void setTableID (long tabid) { tableid = tabid; }
    //==============================================================
    private String dictionarycode = null;
    public void setDictionaryCode (String code) { dictionarycode = code; }
    //==============================================================
    /**
     * Creates a new AddressResolve Process. Returns the new processe's ID.
     * @return
     * @throws HydroException
     * @throws Exception 
     */
    public long execute () throws HydroException, Exception {
        //-----------------------------------------------------------
        int electrons = andrtransac.getAvailableElectrons(userid);
        if (electrons < 1) 
            throw new HydroException("Not enought credit to start a new process", ErrorCode.NOCREDIT);
        //-----------------------------------------------------------
        Dictionary dictionary = dicttransac.getDictionary(dictionarycode);
        TxtTable table = andrtransac.getTableById(tableid);
        if (table.userID() != userid)
            throw new HydroException("Not authorized", ErrorCode.NOTAUTHORIZED);
        if (table.processCode() != ProcessCode.NONE)
            throw new HydroException("Table " + table.tableName() + " is already being processed", ErrorCode.UNAVAILABLE);
        //-----------------------------------------------------------
        AndrProcess process = new AndrProcess();
        process.setProcessCode(ProcessCode.ADDRESSRESOLVE);
        process.setUserId(userid);
        process.setTableId(tableid);
        process.setDictionaryCode(dictionary.getCode());
        process.setStatus(ProcessStatus.CREATE);
        andrtransac.createProcess(process);
        return process.processID();
        //-----------------------------------------------------------
    }
    //==============================================================
}
//**************************************************************************

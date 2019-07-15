package selenium.process.front;
//**************************************************************************
import argon.inoutdata.address.AddressFieldType;
import hydrogen.andromeda.AndrProcess;
import hydrogen.andromeda.AndromedaTransactorFront;
import hydrogen.andromeda.ProcessCode;
import hydrogen.andromeda.ProcessStatus;
import hydrogen.andromeda.TxtTable;
import hydrogen.dictionary.Dictionary;
import hydrogen.dictionary.DictionaryTransactorFront;
import hydrogen.dictionary.TerritorialDivision;
import hydrogen.root.ErrorCode;
import hydrogen.root.HydroException;
//**************************************************************************
public class ProcessAddressSetReady {
    //==============================================================
    private AndromedaTransactorFront andrtransac = null;
    private DictionaryTransactorFront dicttransac = null;
    public void setAndromedaTransactor (AndromedaTransactorFront trans) { andrtransac = trans; }
    public void setDIctionaryTransactor (DictionaryTransactorFront trans) { dicttransac = trans; }
    //==============================================================
    private long processid = 0;
    public void setProcessID (long processid) { this.processid = processid; }
    //==============================================================
    private long userid = 0;
    public void setUserID (long userid) { this.userid = userid; }
    //==============================================================
    private String inputfields = null;
    private String outputfields = null;
    public void setInputFields (String inputfields) { this.inputfields = inputfields; }
    //==============================================================
    public void execute () throws HydroException, Exception
    {
        //-----------------------------------------------------------
        int electrons = andrtransac.getAvailableElectrons(userid);
        if (electrons < 1) 
            throw new HydroException("Not enought credit to start a new process", ErrorCode.NOCREDIT);
        //----------------------------------------------------------
        AndrProcess process = andrtransac.getProcessByID(processid);
        if (process.userID() != userid)
            throw new HydroException("Not authorized", ErrorCode.NOTAUTHORIZED);
        //----------------------------------------------------------
        TxtTable table = andrtransac.getTableById(process.tableID());
        if (table.processCode() != ProcessCode.NONE)
            throw new HydroException("Table " + table.tableName() + " is already being processed", ErrorCode.UNAVAILABLE);
        //----------------------------------------------------------
        Dictionary dictionary = dicttransac.getDictionary(process.dictionaryCode());
        //----------------------------------------------------------
        table.setProcessCode(ProcessCode.ADDRESSRESOLVE);
        outputfields = buildOutputFields(dictionary);
        process.setInputFields(inputfields);
        process.setOutputFields(outputfields);
        process.setStatus(ProcessStatus.CREATED);
        //----------------------------------------------------------
        andrtransac.startTransaction();
        andrtransac.updateProcess(process);
        andrtransac.updateTxtTable(table);
        andrtransac.resetProcessRows(table.tableID());
        andrtransac.commitTransaction();
        //----------------------------------------------------------
    }
    //==============================================================
    private String buildOutputFields (Dictionary dict) {
        TerritorialDivision[] territorials = dict.getTerritorialDivisions();
        StringBuilder outputfields = new StringBuilder();
        outputfields.append(AddressFieldType.STREET.toString());
        outputfields.append(",");
        outputfields.append(AddressFieldType.NUMBER.toString());
        outputfields.append(",");
        outputfields.append(AddressFieldType.SUITE.toString());
        outputfields.append(",");
        for (TerritorialDivision territorial : territorials) {
            outputfields.append(territorial.getDenominationCode());
            outputfields.append(",");
        }
        outputfields.append(AddressFieldType.POSTAL.toString());
        outputfields.append(",");
        outputfields.append(AddressFieldType.LATITUDE.toString());
        outputfields.append(",");
        outputfields.append(AddressFieldType.LONGITUDE.toString());
        outputfields.append(",");
        outputfields.append(AddressFieldType.RESOLVELEVEL.toString());
        return outputfields.toString();
    }
    //==============================================================
}
//**************************************************************************
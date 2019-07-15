package selenium.dictionary.front;
//*************************************************************************
import hydrogen.dictionary.DictionaryTransactorFront;
import hydrogen.dictionary.NewRedirection;
import hydrogen.dictionary.NewUnion;
import hydrogen.root.HydroException;
//*************************************************************************
public class RoadsRedirectAdding {
    //=====================================================================
    private DictionaryTransactorFront dtrans = null;
    //private GeographicTransactor geotrns = null;
    public void SetTransactor (DictionaryTransactorFront t) { dtrans = t; }
    //=====================================================================
    private String dictionary = null;
    private String pakage = null;
    private String idcode = null;
    private String input = null;
    private String redirto = null;
    private String parents = null;
    //=====================================================================
    public void setDictionary (String d) { dictionary = d; }
    public void setPackage (String p) { pakage = p; }
    public void setIdCode (String idc) { idcode = idc; }
    public void setInputString (String i) { input = i; }
    public void setRedirectTo (String r) { redirto = r; }
    public void setParents (String p) { parents = p; }
    //=====================================================================
    public void execute () throws HydroException, Exception {
        //---------------------------------------------------
        dtrans.startTransaction();
        //---------------------------------------------------
        try {
            createRedirection ();
            createUnions ();
        }
        catch (HydroException e) {
            dtrans.rollBackTransaction();
            throw e;
        }
        //---------------------------------------------------
        dtrans.commitTransaction();
        //---------------------------------------------------
    }
    //=====================================================================
    private void createRedirection () throws HydroException, Exception {
        //---------------------------------------------------
        NewRedirection redir = new NewRedirection();
        redir.setDictionary(dictionary);
        redir.setPackage(pakage);
        redir.setIdCode(idcode);
        redir.setInputName(input);
        redir.setRedirTo(redirto);
        //---------------------------------------------------
        dtrans.addRoadRedirection(redir);
        //---------------------------------------------------
    }
    //=====================================================================
    private void createUnions () throws HydroException, Exception {
        //---------------------------------------------------
        if (parents == null) return;
        if (parents.length() == 0) return;
        //---------------------------------------------------
        NewUnion union;
        String[] parentids = parents.split(",");
        //---------------------------------------------------
        for (String p : parentids) {
            union = new NewUnion();
            union.setCountry(dictionary);
            union.setParent(p);
            union.setChild(idcode);
            union.setPackage(pakage);
            dtrans.addRoadRedirectionUnion(union);
        }
        //---------------------------------------------------
    }
    //=====================================================================
}
//*************************************************************************




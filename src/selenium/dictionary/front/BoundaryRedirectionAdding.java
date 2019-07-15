package selenium.dictionary.front;
//*************************************************************************
import hydrogen.dictionary.DictionaryTransactorFront;
import hydrogen.dictionary.NewRedirection;
import hydrogen.dictionary.NewUnion;
import hydrogen.root.HydroException;
//*************************************************************************
public class BoundaryRedirectionAdding {
    //=====================================================================
    private DictionaryTransactorFront dtransc = null;
    //private GeographicTransactor geotrns = null;
    public void SetTransactor (DictionaryTransactorFront t) { dtransc = t; }
    //=====================================================================
    private String dictionary = null;
    private String pakage = null;
    private String idcode = null;
    private String division = null;
    private String input = null;
    private String redirto = null;
    private String parents = null;
    //=====================================================================
    public void setDictionary (String d) { dictionary = d; }
    public void setPackage (String p) { pakage = p; }
    public void setIdCode (String idc) { idcode = idc; }
    public void setDivision (String d) { division = d; }
    public void setInputString (String i) { input = i; }
    public void setRedirectTo (String r) { redirto = r; }
    public void setParents (String p) { parents = p; }
    //=====================================================================
    public void execute () throws HydroException, Exception {
        //---------------------------------------------------
        dtransc.startTransaction();
        //---------------------------------------------------
        try {
            createRedirection ();
            createUnions ();
        }
        catch (HydroException e) {
            dtransc.rollBackTransaction();
            throw e;
        }
        //---------------------------------------------------
        dtransc.commitTransaction();
        //---------------------------------------------------
    }
    //=====================================================================
    private void createRedirection () throws HydroException, Exception {
        //---------------------------------------------------
        NewRedirection redir = new NewRedirection();
        redir.setDictionary(dictionary);
        redir.setPackage(pakage);
        redir.setIdCode(idcode);
        redir.setDivision(division);
        redir.setInputName(input);
        redir.setRedirTo(redirto);
        //---------------------------------------------------
        dtransc.addBoundaryRedirection(redir);
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
            dtransc.addBoundaryRedirectionUnion(union);
        }
        //---------------------------------------------------
    }
    //=====================================================================
}
//*************************************************************************

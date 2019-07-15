package selenium.file;
//*************************************************************************
import argon.inoutdata.address.AddressFieldType;
import hydrogen.dictionary.Dictionary;
import hydrogen.dictionary.TerritorialDivision;
//*************************************************************************
/*
@Deprecated
public class FieldsSelector 
{
    //============================================================
    Country country;
    public void setCountry (Country country) { 
        this.country = country;
    }
    //============================================================
    private selectorAddressField[] fields = null;
    public selectorAddressField[] getToSelectFields () {
        if (fields == null) {
            //--------------------------------------------
            TerritorialDivision[] lvterrit = getLevelTerritorials();
            int count = lvterrit.length + 3;
            fields = new selectorAddressField[count];
            //--------------------------------------------
            fields[0] = new selectorAddressField();
            //fields[0].setType(AddressFieldType.ID);
            //--------------------------------------------
            fields[1] = new selectorAddressField();
            fields[1].setType(AddressFieldType.STREET);
            //--------------------------------------------
            fields[2] = new selectorAddressField();
            fields[2].setType(AddressFieldType.NUMBER);
            //--------------------------------------------
            int ind = 3;
            for (TerritorialDivision t : lvterrit) {
                fields[ind] = new selectorAddressField();
                fields[ind].setType(AddressFieldType.BOUNDARY);
                fields[ind].setName(t.getDenominationCode());
                fields[ind].setDisplayName(t.getDenomination());
                ind++;
            }
            //--------------------------------------------
        }
        return fields;
    }
    //============================================================
    private TerritorialDivision[] getLevelTerritorials () {
        if (country == null) return new TerritorialDivision[0];
        TerritorialDivision[] territ = country.getTerritorialDivisions();
        int tlevelcount = 0;
        //--------------------------------------------------------
        //We count the leveled territorials
        for (TerritorialDivision t : territ)
            if (t.getLevel() != 0) tlevelcount++;
        //--------------------------------------------------------
        TerritorialDivision[] lvterrit = new TerritorialDivision[tlevelcount];
        int index = 0;
        for (TerritorialDivision t : territ)
            if (t.getLevel() != 0) {
                lvterrit[index] = t;
                index++;
            }
        //--------------------------------------------------------
        return lvterrit;
        //--------------------------------------------------------
    }
    //============================================================
}
*/
//*************************************************************************


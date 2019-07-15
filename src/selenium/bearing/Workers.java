package selenium.bearing;
//****************************************************************************
import argon.resolve.AddrResolver;
//****************************************************************************
public class Workers 
{
    //==================================================================
    Transactors trnsctors = null;
    public void setTransactors (Transactors t) { trnsctors = t; }
    //==================================================================
    /*
    Resolver addresolverold = null;
    @Deprecated
    public Resolver getAddressResolver () throws Exception
    {
        if (addresolverold == null)
        {
            addresolverold = new Resolver();
            addresolverold.setGeographicTransactor(trnsctors.getGeographicTransactor());
        }
        return addresolverold;
    }
    */
    //==================================================================
    AddrResolver addrresolver = null;
    public AddrResolver getAddressResolver () throws Exception
    {
        if (addrresolver == null)
        {
            addrresolver = new AddrResolver();
            //addrresolver.setDictionaryTransactor(trnsctors.getGeographicTransactor());
        }
        return addrresolver;
    }
            
    
    //==================================================================
}
//****************************************************************************



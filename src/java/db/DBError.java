package db;

import javax.faces.context.FacesContext;

public class DBError extends Exception{
    
    public DBError() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }
}

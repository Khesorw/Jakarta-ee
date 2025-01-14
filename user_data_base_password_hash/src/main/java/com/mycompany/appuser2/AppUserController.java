package com.mycompany.appuser2;

import entity.AppUser;
import com.mycompany.appuser2.util.JsfUtil;
import com.mycompany.appuser2.util.PaginationHelper;

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@Named("appUserController")
@SessionScoped
public class AppUserController implements Serializable {

    private AppUser current;
    private DataModel items = null;
    
    
    //binds to jsf secret input for for password attribute 
    private String notHashed = "";
    
    
    // for edited password
    private String editPassword;

    public String getEditPassword() {
        return editPassword;
    }

    public void setEditPassword(String editPassword) {
        this.editPassword = editPassword;
    }

    public String getNotHashed() {
        return notHashed;
    }

    public void setNotHashed(String notHashed) {
        this.notHashed = notHashed;
    }
    @EJB
    private com.mycompany.appuser2.AppUserFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public AppUserController() {
    }

    public AppUser getSelected() {
        if (current == null) {
            current = new AppUser();
            selectedItemIndex = -1;
        }
        return current;
    }

    private AppUserFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (AppUser) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new AppUser();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        
        
        String hasedValue = stringHasher(notHashed);
        current.setPassword(hasedValue);
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("AppUserCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (AppUser) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }
    
    
    
    
   /*
    *Check if the password field for the update page is empth then save the pervious password else set the new password.
   
    **/ 
    public String update() {
        
        
        String perviousPassword = current.getPassword();
        
      
        
        if(editPassword.length() > 0)
        {
            String newPassword = stringHasher(editPassword);
            
            current.setPassword(newPassword);
        
        }
        else{
            current.setPassword(perviousPassword);
       
        }
        
        
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("AppUserUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (AppUser) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    
    
    /**
     Hash the string using SHA1 algorithm
     * 
     * @param text: String
     * @return String 
     
     */
    private String stringHasher(String text){
        
        if(text == null){
            text = "";
        }
        
        byte[] bytes;
        
        try{
        
         MessageDigest md = MessageDigest.getInstance("SHA1");
         md.update(text.getBytes());
         
         bytes = md.digest();
         
         StringBuffer bf = new StringBuffer();
         
         for( byte b : bytes){
             
             //convert to hex make sure unsigned
             bf.append(String.format("%02x", b & 0xff));
         
         }
         
         text = bf.toString();
         
         return text;
        
        }catch(Exception e){
            
            e.printStackTrace();
        
        }
        
        return text;
    
    }//stringHasher()
    
    
    
    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("AppUserDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public AppUser getAppUser(java.lang.Long id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = AppUser.class)
    public static class AppUserControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AppUserController controller = (AppUserController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "appUserController");
            return controller.getAppUser(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof AppUser) {
                AppUser o = (AppUser) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + AppUser.class.getName());
            }
        }

    }

}

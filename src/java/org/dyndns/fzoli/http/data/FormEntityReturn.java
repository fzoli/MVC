package org.dyndns.fzoli.http.data;

import org.apache.http.client.entity.UrlEncodedFormEntity;

/**
 *
 * @author zoli
 */
public class FormEntityReturn extends HttpReturn<UrlEncodedFormEntity> {

    public FormEntityReturn(Exception ex) {
        super(ex);
    }

    public FormEntityReturn(UrlEncodedFormEntity data) {
        super(0, data);
    }
    
    public UrlEncodedFormEntity getFormEntity() {
        return getData();
    }
    
}
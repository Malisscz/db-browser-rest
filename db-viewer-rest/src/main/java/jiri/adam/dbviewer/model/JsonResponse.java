package jiri.adam.dbviewer.model;

import lombok.Data;

/**
 * Wrapper holding status and generic data
 */
@Data
public class JsonResponse {

    private String status;
    private Object data;

    public JsonResponse(String status, Object data) {
        this.status = status;
        this.data = data;
    }

}

package jiri.adam.dbviewer.api;

import lombok.Data;

@Data
public class JsonResponse {

    private String status;
    private Object data;

    public JsonResponse(String status, Object data) {
        this.status = status;
        this.data=data;
    }

}

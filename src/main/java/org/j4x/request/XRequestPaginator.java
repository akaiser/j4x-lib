package org.j4x.request;

import com.google.gson.annotations.SerializedName;
import org.j4x.constants.XTableContstants.TablePaginatorEvent;

public class XRequestPaginator {

    @SerializedName("event")
    private TablePaginatorEvent event;

    public TablePaginatorEvent getEvent() {
        return event;
    }

    public void setEvent(TablePaginatorEvent event) {
        this.event = event;
    }
}

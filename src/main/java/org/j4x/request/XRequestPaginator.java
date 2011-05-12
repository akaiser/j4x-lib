package org.j4x.request;

import com.google.gson.annotations.SerializedName;
import org.j4x.constants.XTableContstants.DQTablePaginatorEvent;

public class XRequestPaginator {

    @SerializedName("event")
    private DQTablePaginatorEvent event;

    public DQTablePaginatorEvent getEvent() {
        return event;
    }

    public void setEvent(DQTablePaginatorEvent event) {
        this.event = event;
    }
}

package org.j4x.request;

import com.google.gson.annotations.SerializedName;
import org.j4x.constants.XTableContstants.TablePagingEvent;

public class XRequestPaging {

    @SerializedName("event")
    private TablePagingEvent event;

    public TablePagingEvent getEvent() {
        return event;
    }

    public void setEvent(TablePagingEvent event) {
        this.event = event;
    }
}

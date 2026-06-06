package com.backend.allreva.concert.concert.kopis;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JacksonXmlRootElement(localName = "dbs")
public class KopisConcertSummaryResponse {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "db")
    private List<Db> dbList;

    public List<Db> getDbList() {
        return dbList == null ? new ArrayList<>() : dbList;
    }

    @Getter
    @NoArgsConstructor
    public static class Db {
        @JacksonXmlProperty(localName = "mt20id")
        private String id;

        @JacksonXmlProperty(localName = "prfstate")
        private String prfState;
    }
}

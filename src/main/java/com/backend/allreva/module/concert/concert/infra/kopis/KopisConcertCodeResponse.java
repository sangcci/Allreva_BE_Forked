package com.backend.allreva.module.concert.concert.infra.kopis;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "dbs")
@XmlAccessorType(XmlAccessType.FIELD)
public class KopisConcertCodeResponse {
    @XmlElement(name = "db")
    private List<Db> dbList;

    public List<Db> getDbList() {
        return dbList == null ? new ArrayList<>() : dbList;
    }

    @Getter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Db {
        @XmlElement(name = "mt20id")
        private String id;

        @XmlElement(name = "prfstate")
        private String prfState;
    }
}

package com.ontology.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tbl_claim")
@Data
public class Claim {
    @Id
    @GeneratedValue(generator = "JDBC")
    private String id;

    private String ontid;
    private String claimId;
    private String claim;
    private String txHash;
    private Date createTime;

}

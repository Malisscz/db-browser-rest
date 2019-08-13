package jiri.adam.dbviewer.db.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DB_CONNECTION")
@Data
public class DbConnection {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;
    private String hostname;
    private String port;
    private String databaseName;
    private String username;
    private String password;

}
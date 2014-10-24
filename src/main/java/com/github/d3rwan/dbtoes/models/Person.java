package com.github.d3rwan.dbtoes.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Person
 * 
 * @author d3rwan
 * 
 */
@Entity
@Table(name = "people")
public class Person {

    /** id */
    @Id
    @Column(name = "person_id")
    private String id;

    /** last name */
    @Column(name = "last_name")
    private String lastName;

    /** first name */
    @Column(name = "first_name")
    private String firstName;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String toString() {
        return "id:" + id + ",firstName: " + firstName + ", lastName: " + lastName;
    }
}
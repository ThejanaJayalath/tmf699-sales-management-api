package com.tmforum.salesmanagement.model;

import javax.persistence.*;

@Entity
@Table(name = "contact_medium")
public class ContactMedium {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String mediumType;
    private String preferred;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "characteristic_id")
    private MediumCharacteristic characteristic;

    public ContactMedium() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMediumType() {
        return mediumType;
    }

    public void setMediumType(String mediumType) {
        this.mediumType = mediumType;
    }

    public String getPreferred() {
        return preferred;
    }

    public void setPreferred(String preferred) {
        this.preferred = preferred;
    }

    public MediumCharacteristic getCharacteristic() {
        return characteristic;
    }

    public void setCharacteristic(MediumCharacteristic characteristic) {
        this.characteristic = characteristic;
    }
}
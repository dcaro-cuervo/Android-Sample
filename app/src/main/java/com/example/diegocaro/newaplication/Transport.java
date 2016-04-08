package com.example.diegocaro.newaplication;

/**
 * Created by diego.caro on 11/03/2016.
 */
public class Transport {

    // private properties
    private int _id;
    private String _name;
    private String _phone;

    // Empty contructor
    public Transport()
    {}

    // Constructor
    public Transport(String name, String phone)
    {
        super();
        this._name = name;
        this._phone = phone;
    }

    // getting ID
    public int getID()
    {
        return this._id;
    }

    // setting ID
    public void setID(int newId)
    {
        this._id = newId;
    }

    // getting name
    public String getName()
    {
        return this._name;
    }

    // setting name
    public void setName(String newName)
    {
        this._name = newName;
    }

    // getting phone
    public String getPhone()
    {
        return this._phone;
    }

    // setting phone
    public void setPhone(String newPhone)
    {
        this._phone = newPhone;
    }

}

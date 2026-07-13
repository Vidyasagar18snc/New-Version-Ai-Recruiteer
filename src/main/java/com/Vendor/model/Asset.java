package com.Vendor.model;

import lombok.Data;

import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data

@Document(collection = "assets")

public class Asset {

    @Id
    private String id;

    private String assetId;

    private String assetName;

    private String category;

    private String brand;

    private String serialNumber;

    private String status;
    private String accessories;

}
package com.example.brandtests.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

public class Item implements Parcelable {
    private Long productId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private Integer weight;
    private String primaryImageUrl;

    // Constructor, Getters, and Setters
    public Item(Long productId, String name, BigDecimal price, Integer quantity, Integer weight, String primaryImageUrl) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.weight = weight;
        this.primaryImageUrl = primaryImageUrl;
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public void setPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
    }

    // Parcelable implementation
    protected Item(Parcel in) {
        if (in.readByte() == 0) {
            productId = null;
        } else {
            productId = in.readLong();
        }
        name = in.readString();
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = new BigDecimal(in.readString());
        }
        if (in.readByte() == 0) {
            quantity = null;
        } else {
            quantity = in.readInt();
        }
        if (in.readByte() == 0) {
            weight = null;
        } else {
            weight = in.readInt();
        }
        primaryImageUrl = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (productId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(productId);
        }
        dest.writeString(name);
        if (price == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeString(price.toString());
        }
        if (quantity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(quantity);
        }
        if (weight == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(weight);
        }
        dest.writeString(primaryImageUrl);
    }
}

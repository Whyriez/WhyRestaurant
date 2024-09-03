package com.example.whyrestaurant.model

class CartModel {
    private var id: String = ""
    private var name: String = ""
    private var img: String = ""
    private var price: String = ""
    private var quantity: Int = 0

    constructor(id: String, name:String, img: String, price: String, quantity:Int){
        this.id = id
        this.name = name
        this.img = img
        this.price = price
        this.quantity = quantity
    }

    fun getId(): String{
        return id
    }

    fun setId(id: String){
        this.id = id
    }

    fun getName(): String{
        return name
    }

    fun setName(name: String){
        this.name = name
    }

    fun getImage(): String{
        return img
    }

    fun setImage(img: String){
        this.img = img
    }

    fun getPrice(): String{
        return price
    }

    fun setPrice(price: String){
        this.price = price
    }

    fun getQuantity(): Int{
        return quantity
    }

    fun setQuantity(quantity: Int){
        this.quantity = quantity
    }
}
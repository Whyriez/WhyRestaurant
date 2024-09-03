package com.example.whyrestaurant.model

class ItemGambarModel {
    private var id: String = ""
    private var img: String

    constructor(id: String, img:String){
        this.id = id
        this.img = img
    }

    fun getId(): String{
        return id
    }

    fun setId(id: String){
        this.id = id
    }
    fun getImage(): String{
        return img
    }

    fun setImage(img: String){
        this.img = img
    }
}
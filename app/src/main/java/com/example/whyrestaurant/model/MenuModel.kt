package com.example.whyrestaurant.model

class MenuModel {
    private var title: String = ""

    constructor(title: String){
        this.title = title
    }

    fun getTitle(): String{
        return title
    }

    fun setTitle(title: String){
        this.title = title
    }

}
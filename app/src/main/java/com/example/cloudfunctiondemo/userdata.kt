package com.example.cloudfunctiondemo

public data class userdata(var subscribedToMailingList:Boolean?,var email:String?){
    constructor () : this(null,null) {

    }
}
package com.example.cloudfunctiondemo

/**
 * Data class for storing the value of firebase response.
 */
public data class userdata(var subscribedToMailingList:Boolean?, var email:String?){
    constructor () : this(null,null) {

    }
}
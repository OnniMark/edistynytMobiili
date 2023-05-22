package com.example.edistynytandroidkurssi.datatypes.Feedback

import com.google.gson.annotations.SerializedName


data class Feedback (

  @SerializedName("id"       ) var id       : Int?    = null,
  @SerializedName("name"     ) var name     : String? = null,
  @SerializedName("location" ) var location : String? = null,
  @SerializedName("value"    ) var value    : String? = null

)

{
  // yliajetaan luokan toStrinmetodi
  override fun toString(): String {
    return "${name}: ${location}"
  }
}
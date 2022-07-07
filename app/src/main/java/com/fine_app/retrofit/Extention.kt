package com.fine_app.retrofit
//문자열이 제이슨 형태인가?
fun String?.isJsonObject():Boolean{
    return this?.startsWith("{")==true && this.endsWith("}")
}
//문자열이 제이슨 배열인지
fun String?.isJsonArray():Boolean{
    return this?.startsWith("[")==true && this.endsWith("]")
}

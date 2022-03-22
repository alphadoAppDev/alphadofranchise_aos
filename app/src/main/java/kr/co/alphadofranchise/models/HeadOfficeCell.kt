package kr.co.alphadofranchise.models

data class HeadOfficeCell(
    var line0Array : ArrayList<String>,
    var line1Array : ArrayList<String>,
    var innerHeadOfficeArray : ArrayList<Any>,
    var isShow : Boolean = false
)

data class HeadOfficeInnerCell(
    var innerHeadOfficeArray : ArrayList<String>,
)
package kr.co.alphadofranchise.models

data class RegionCell(
    var line0Array : ArrayList<String>,
    var line1Array:  ArrayList<String>,
    var innerRegionArray : ArrayList<Any>,
    var isShow : Boolean = false
)

data class RegionInnerCell(
    var innerRegionArray : ArrayList<String>,
)
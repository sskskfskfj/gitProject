package kotlin1.kopring.Dto

data class ResponseDto<T> (
    val status : Int,
    val message : String,
    val data : T
)
package com.hans.smartTB_admin.Model

import com.google.firebase.firestore.DocumentId

class dataRiwayat {
    val namaNasabah : String? = null
    val tanggal : com.google.firebase.Timestamp? = null
    val status : String? = null
    val pendapatan : String? = null
    val email: String? = null
    @DocumentId
    val docID : String = ""
}
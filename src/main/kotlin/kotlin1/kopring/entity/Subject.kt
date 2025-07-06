package kotlin1.kopring.entity

import jakarta.persistence.*
import lombok.AccessLevel
import lombok.Getter
import lombok.NoArgsConstructor
import java.time.LocalDate

@Entity
class Subject (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id : Long = 0,

    @Column(nullable = false, name = "subject_name")
    open val subjectName : String = "",

    @Column(name = "created_date")
    open val date : LocalDate = LocalDate.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    open var user : UserEntity? = null
){
    fun assignUser(user : UserEntity){
        this.user = user
    }
}
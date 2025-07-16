package kotlin1.kopring.entity

import jakarta.persistence.*


@Entity
@Table(name = "user_entity")
class UserEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0,
    open val githubId: String = "",
    open val username: String = "",
    open val email: String? = "",
    open val avatarUrl: String? = "",

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user")
    val subjects : MutableList<Subject> = mutableListOf()
){
    constructor() : this(0, "", "", null, null, mutableListOf())

}
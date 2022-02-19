package com.dnd.sixth.lmsservice.data.network.base

import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Retrofit

//레트로핏을 기반하여 동작
// 사용할 메소드 선언 // 사용할 HTTP CRUD동작들을 정의해두는 인터페이스
abstract class BaseApi: KoinComponent {
    val retrofit: Retrofit by inject()

}